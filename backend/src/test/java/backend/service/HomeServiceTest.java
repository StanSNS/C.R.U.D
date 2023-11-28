package backend.service;

import backend.dto.UserDetailsDTO;
import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.exception.AccessDeniedException;
import backend.exception.ResourceNotFoundException;
import backend.repository.UserEntityRepository;
import backend.util.ValidateData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeServiceTest {

    @Mock
    private ValidateData validateData;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HomeService homeService;

    @Test
    void testGetAllUsers_ValidCredentials_ReturnsUserDetailsList() {
        String email = "test@example.com";
        String password = "password";

        UserEntity mockedUserEntity = new UserEntity();
        when(validateData.validateUserWithPassword(email, password)).thenReturn(mockedUserEntity);

        ArrayList<UserEntity> mockedUserEntities = new ArrayList<>();
        mockedUserEntities.add(new UserEntity());
        when(userEntityRepository.findAll()).thenReturn(mockedUserEntities);

        ArrayList<UserDetailsDTO> mockedUserDetailsDTOs = new ArrayList<>();
        mockedUserDetailsDTOs.add(new UserDetailsDTO());
        when(modelMapper.map(any(), eq(UserDetailsDTO.class)))
                .thenReturn(mockedUserDetailsDTOs.get(0), mockedUserDetailsDTOs.subList(1, mockedUserDetailsDTOs.size())
                        .toArray(new UserDetailsDTO[0]));

        List<UserDetailsDTO> result = homeService.getAllUsers(email, password);

        assertEquals(mockedUserDetailsDTOs.size(), result.size());

        assertEquals(mockedUserDetailsDTOs, result);

        verify(validateData, times(1)).validateUserWithPassword(email, password);
        verify(userEntityRepository, times(1)).findAll();
        verify(modelMapper, times(mockedUserEntities.size())).map(any(), eq(UserDetailsDTO.class));
    }

    @Test
    void testDeleteUser_UserNotFound_ThrowsResourceNotFoundException() {
        String email = "test@example.com";
        String password = "password";
        String userToDeleteEmail = "nonExistingUser@example.com";

        UserEntity userEntity = new UserEntity();

        when(validateData.validateUserWithPassword(email, password)).thenReturn(userEntity);
        when(userEntityRepository.findByEmail(userToDeleteEmail)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> homeService.deleteUser(email, password, userToDeleteEmail));

        verify(validateData, times(1)).validateUserWithPassword(email, password);
        verify(userEntityRepository, times(1)).findByEmail(userToDeleteEmail);
        verify(userEntityRepository, never()).delete(any());
    }

    @Test
    void testDeleteUser_ValidCredentials_AdminUserDeletesNonAdminUser() {
        String email = "admin@example.com";
        String password = "adminPassword";
        String userToDeleteEmail = "nonAdminUser@example.com";

        UserEntity adminUser = new UserEntity();

        RoleEntity adminRoleEntity = new RoleEntity();
        adminRoleEntity.setName("ROLE_ADMIN");

        Set<RoleEntity> adminRoleEntitySet = new HashSet<>();
        adminRoleEntitySet.add(adminRoleEntity);
        adminUser.setRoles(adminRoleEntitySet);

        UserEntity nonAdminUserToDelete = new UserEntity();
        RoleEntity nonAdminRoleEntity = new RoleEntity();
        nonAdminRoleEntity.setName("ROLE_USER");

        Set<RoleEntity> nonAdminRoleEntitySet = new HashSet<>();
        nonAdminRoleEntitySet.add(nonAdminRoleEntity);
        nonAdminUserToDelete.setRoles(nonAdminRoleEntitySet);

        when(validateData.validateUserWithPassword(email, password)).thenReturn(adminUser);
        when(userEntityRepository.findByEmail(userToDeleteEmail)).thenReturn(nonAdminUserToDelete);
        when(validateData.isUserAdmin(adminUser.getRoles())).thenReturn(true);
        when(validateData.isUserAdmin(nonAdminUserToDelete.getRoles())).thenReturn(false);

        homeService.deleteUser(email, password, userToDeleteEmail);

        verify(userEntityRepository, times(1)).delete(nonAdminUserToDelete);
    }

    @Test
    void testDeleteUser_AccessDenied_ThrowsAccessDeniedException() {
        String adminEmail = "admin@example.com";
        String adminPassword = "adminPassword";
        String nonAdminUserToDeleteEmail = "nonAdminUser@example.com";

        UserEntity nonAdminUser = new UserEntity();

        RoleEntity nonAdminRoleEntity = new RoleEntity();
        nonAdminRoleEntity.setName("ROLE_USER");

        Set<RoleEntity> nonAdminRoleEntitySet = new HashSet<>();
        nonAdminRoleEntitySet.add(nonAdminRoleEntity);
        nonAdminUser.setRoles(nonAdminRoleEntitySet);

        UserEntity adminUser = new UserEntity();
        RoleEntity adminRoleEntity = new RoleEntity();
        adminRoleEntity.setName("ROLE_USER");

        Set<RoleEntity> adminRoleEntitySet = new HashSet<>();
        adminRoleEntitySet.add(adminRoleEntity);
        adminUser.setRoles(adminRoleEntitySet);

        when(validateData.validateUserWithPassword(adminEmail, adminPassword)).thenReturn(adminUser);
        when(userEntityRepository.findByEmail(nonAdminUserToDeleteEmail)).thenReturn(nonAdminUser);
        when(validateData.isUserAdmin(adminUser.getRoles())).thenReturn(false);

        assertThrows(AccessDeniedException.class,
                () -> homeService.deleteUser(adminEmail, adminPassword, nonAdminUserToDeleteEmail));

        verify(userEntityRepository, never()).delete(any());
    }


    @Test
    void testLogoutUser_ValidCredentials_LogsOutUser() {
        String email = "test@example.com";
        String password = "password";

        when(validateData.validateUserWithPassword(email, password)).thenReturn(new UserEntity());

        SecurityContext securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

        homeService.logoutUser(email, password);

        verify(validateData, times(1)).validateUserWithPassword(email, password);
    }

    @Test
    void testChangePhoneNumber_ValidCredentials_UserPhoneNumberChanged() {
        String email = "test@example.com";
        String password = "password";
        String emailUserToChange = "userToChange@example.com";
        String newPhoneNumber = "1234567890";

        UserEntity mockedUserEntity = new UserEntity();
        when(validateData.validateUserWithPassword(email, password)).thenReturn(mockedUserEntity);
        when(userEntityRepository.findByEmail(emailUserToChange)).thenReturn(mockedUserEntity);

        homeService.changePhoneNumber(email, password, emailUserToChange, newPhoneNumber);

        verify(validateData, times(1)).validateUserWithPassword(email, password);
        verify(userEntityRepository, times(1)).findByEmail(emailUserToChange);
        verify(userEntityRepository, times(1)).save(any());
    }

    @Test
    void testChangePhoneNumber_UserNotFound_ThrowsResourceNotFoundException() {
        String email = "test@example.com";
        String password = "password";
        String emailUserToChange = "nonExistingUser@example.com";
        String newPhoneNumber = "1234567890";

        when(validateData.validateUserWithPassword(email, password)).thenReturn(new UserEntity());
        when(userEntityRepository.findByEmail(emailUserToChange)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> homeService.changePhoneNumber(email, password, emailUserToChange, newPhoneNumber));

        verify(validateData, times(1)).validateUserWithPassword(email, password);
        verify(userEntityRepository, times(1)).findByEmail(emailUserToChange);
        verify(userEntityRepository, never()).save(any());
    }

}
