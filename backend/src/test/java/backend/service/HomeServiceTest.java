package backend.service;

import backend.dto.UserDetailsDTO;
import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.exception.AccessDeniedException;
import backend.exception.DataValidationException;
import backend.exception.ResourceNotFoundException;
import backend.repository.UserEntityRepository;
import backend.util.ValidateData;
import backend.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeServiceTest {

    @Mock
    private ValidateData validateData;

    @Mock
    private ValidationUtil validationUtil;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HomeService homeService;

    @Test
    void testGetAllUsersByDefault() {
        String email = "test@example.com";
        String password = "password";

        when(userEntityRepository.findAll()).thenReturn(new ArrayList<>());

        List<UserDetailsDTO> result = homeService.getAllUsersByDefault(email, password);

        assertEquals(0, result.size());

        verify(validateData).validateUserWithPassword(email, password);
        verify(userEntityRepository).findAll();
        verify(validationUtil, times(0)).isValid(any());
        verify(modelMapper, times(0)).map(any(), eq(UserDetailsDTO.class));
    }

    @Test
    void testGetAllUsersByDefault_ValidationFails() {
        String email = "test@example.com";
        String password = "password";

        when(userEntityRepository.findAll()).thenReturn(List.of(new UserEntity()));

        when(modelMapper.map(any(), eq(UserDetailsDTO.class))).thenAnswer(invocation -> {
            UserEntity userEntity = invocation.getArgument(0);
            return new UserDetailsDTO();
        });

        when(validationUtil.isValid(any())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            homeService.getAllUsersByDefault(email, password);
        });
        verify(validateData).validateUserWithPassword(email, password);
        verify(userEntityRepository).findAll();
        verify(modelMapper, times(1)).map(any(), eq(UserDetailsDTO.class));
        verify(validationUtil).isValid(any());
    }

    @Test
    void testGetAllUsersOrderedByLastNameAndDateOfBirth() {
        String email = "test@example.com";
        String password = "password";

        when(userEntityRepository.findAllUsersOrderedByLastNameAndDateOfBirth())
                .thenReturn(List.of(new UserEntity()));

        when(modelMapper.map(any(), eq(UserDetailsDTO.class))).thenAnswer(invocation -> {
            UserEntity userEntity = invocation.getArgument(0);
            return new UserDetailsDTO();
        });

        when(validationUtil.isValid(any())).thenReturn(true);
        List<UserDetailsDTO> result = homeService.getAllUsersOrderedByLastNameAndDateOfBirth(email, password);

        assertEquals(1, result.size());

        verify(validateData).validateUserWithPassword(email, password);
        verify(userEntityRepository).findAllUsersOrderedByLastNameAndDateOfBirth();
        verify(modelMapper, times(1)).map(any(), eq(UserDetailsDTO.class));
        verify(validationUtil).isValid(any());
    }

    @Test
    void testGetRandomUser_ValidCredentials_ReturnsRandomUser() {
        String email = "test@example.com";
        String password = "password";

        UserEntity mockedUserEntity = new UserEntity();
        when(validateData.validateUserWithPassword(email, password)).thenReturn(mockedUserEntity);

        UserEntity userEntity1 = new UserEntity();
        userEntity1.setFirstName("User1");
        userEntity1.setId(1L);

        UserEntity userEntity2 = new UserEntity();
        userEntity2.setFirstName("User2");
        userEntity2.setId(2L);

        UserEntity userEntity3 = new UserEntity();
        userEntity3.setFirstName("User3");
        userEntity3.setId(3L);

        List<UserEntity> mockedUserEntities = new ArrayList<>();
        mockedUserEntities.add(userEntity1);
        mockedUserEntities.add(userEntity2);
        mockedUserEntities.add(userEntity3);

        when(userEntityRepository.findAll()).thenReturn(mockedUserEntities);

        List<UserDetailsDTO> result = homeService.getRandomUser(email, password);

        assertNotNull(result);

        verify(validateData, times(1)).validateUserWithPassword(email, password);
        verify(userEntityRepository, times(1)).findAll();
    }

    @Test
    void testGetRandomUser_NoUsersAvailable_ThrowsResourceNotFoundException() {
        String email = "test@example.com";
        String password = "password";

        UserEntity mockedUserEntity = new UserEntity();
        when(validateData.validateUserWithPassword(email, password)).thenReturn(mockedUserEntity);

        when(userEntityRepository.findAll()).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> homeService.getRandomUser(email, password));

        verify(validateData, times(1)).validateUserWithPassword(email, password);
        verify(userEntityRepository, times(1)).findAll();
        verify(modelMapper, never()).map(any(UserEntity.class), eq(UserDetailsDTO.class));
    }

    @Test
    void testGetAllUsersSortedByLastName() {
        String email = "test@example.com";
        String password = "password";
        String lastNameSearch = "Doe";

        List<UserEntity> mockedUsers = new ArrayList<>();
        mockedUsers.add(createUser("John", "Doe"));
        mockedUsers.add(createUser("Jane", "Doe"));

        when(userEntityRepository.findAllByLastName(lastNameSearch)).thenReturn(mockedUsers);

        when(modelMapper.map(any(), eq(UserDetailsDTO.class))).thenAnswer(invocation -> {
            UserEntity userEntity = invocation.getArgument(0);
            return new UserDetailsDTO();
        });

        when(validationUtil.isValid(any())).thenReturn(true);

        List<UserDetailsDTO> result = homeService.getAllUsersSortedByLastName(email, password, lastNameSearch);

        assertEquals(2, result.size());

        verify(validateData).validateUserWithPassword(email, password);
        verify(userEntityRepository).findAllByLastName(lastNameSearch);
        verify(modelMapper, times(2)).map(any(), eq(UserDetailsDTO.class));
        verify(validationUtil, times(2)).isValid(any());
    }

    @Test
    void testGetAllUsersSortedByLastName_ValidationFails() {
        String email = "test@example.com";
        String password = "password";
        String lastNameSearch = "Doe";

        List<UserEntity> mockedUsers = new ArrayList<>();
        mockedUsers.add(createUser("John", "Doe"));
        mockedUsers.add(createUser("Jane", "Doe"));

        when(userEntityRepository.findAllByLastName(lastNameSearch)).thenReturn(mockedUsers);

        when(modelMapper.map(any(), eq(UserDetailsDTO.class))).thenAnswer(invocation -> {
            UserEntity userEntity = invocation.getArgument(0);
            return new UserDetailsDTO();
        });

        when(validationUtil.isValid(any())).thenReturn(false);

        assertThrows(DataValidationException.class, () -> {
            homeService.getAllUsersSortedByLastName(email, password, lastNameSearch);
        });

        verify(validateData).validateUserWithPassword(email, password);
        verify(userEntityRepository).findAllByLastName(lastNameSearch);
    }

    private UserEntity createUser(String firstName, String lastName) {
        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(firstName);
        userEntity.setLastName(lastName);
        return userEntity;
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
