package backend.service;

import backend.dto.EditDetailsDTO;
import backend.dto.UserDetailsDTO;
import backend.entity.RoleEntity;
import backend.entity.UserEntity;
import backend.exception.*;
import backend.repository.UserEntityRepository;
import backend.util.ValidateData;
import backend.util.ValidationUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static backend.constants.ActionConst.*;
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
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserEntityRepository userEntityRepository;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private HomeService homeService;

    @Test
    void testGetSelectedUser_ValidUserDetails_ReturnsUserDetailsDTO() {
        String userEmail = "test@example.com";
        String userPassword = "password";
        String selectedUserEmail = "selectedUser@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(selectedUserEmail);

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setEmail(selectedUserEmail);

        when(validateData.validateUserWithPassword(userEmail, userPassword)).thenReturn(new UserEntity());
        when(userEntityRepository.findByEmail(selectedUserEmail)).thenReturn(userEntity);
        when(modelMapper.map(userEntity, UserDetailsDTO.class)).thenReturn(userDetailsDTO);
        when(validationUtil.isValid(userDetailsDTO)).thenReturn(true);

        UserDetailsDTO result = homeService.getSelectedUser(userEmail, userPassword, selectedUserEmail);

        assertEquals(selectedUserEmail, result.getEmail());

        verify(validateData, times(1)).validateUserWithPassword(userEmail, userPassword);
        verify(userEntityRepository, times(1)).findByEmail(selectedUserEmail);
        verify(modelMapper, times(1)).map(userEntity, UserDetailsDTO.class);
        verify(validationUtil, times(1)).isValid(userDetailsDTO);
    }

    @Test
    void testGetSelectedUser_UserNotFound_ThrowsResourceNotFoundException() {
        String userEmail = "test@example.com";
        String userPassword = "password";
        String selectedUserEmail = "nonExistingUser@example.com";

        when(validateData.validateUserWithPassword(userEmail, userPassword)).thenReturn(new UserEntity());
        when(userEntityRepository.findByEmail(selectedUserEmail)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> homeService.getSelectedUser(userEmail, userPassword, selectedUserEmail));

        verify(validateData, times(1)).validateUserWithPassword(userEmail, userPassword);
        verify(userEntityRepository, times(1)).findByEmail(selectedUserEmail);
        verify(modelMapper, never()).map(any(), eq(UserDetailsDTO.class));
        verify(validationUtil, never()).isValid(any());
    }

    @Test
    void testGetSelectedUser_InvalidUserDetails_ThrowsDataValidationException() {
        String userEmail = "test@example.com";
        String userPassword = "password";
        String selectedUserEmail = "selectedUser@example.com";

        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(selectedUserEmail);

        UserDetailsDTO userDetailsDTO = new UserDetailsDTO();
        userDetailsDTO.setEmail(selectedUserEmail);

        when(validateData.validateUserWithPassword(userEmail, userPassword)).thenReturn(new UserEntity());
        when(userEntityRepository.findByEmail(selectedUserEmail)).thenReturn(userEntity);
        when(modelMapper.map(userEntity, UserDetailsDTO.class)).thenReturn(userDetailsDTO);
        when(validationUtil.isValid(userDetailsDTO)).thenReturn(false);

        assertThrows(DataValidationException.class,
                () -> homeService.getSelectedUser(userEmail, userPassword, selectedUserEmail));

        verify(validateData, times(1)).validateUserWithPassword(userEmail, userPassword);
        verify(userEntityRepository, times(1)).findByEmail(selectedUserEmail);
        verify(modelMapper, times(1)).map(userEntity, UserDetailsDTO.class);
        verify(validationUtil, times(1)).isValid(userDetailsDTO);
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
    void testUpdateUserData() {
        UserEntity userToBeEdited = new UserEntity();
        EditDetailsDTO newUserDataObject = new EditDetailsDTO();
        newUserDataObject.setFirstName("John");
        newUserDataObject.setLastName("Doe");
        newUserDataObject.setEmail("john.doe@example.com");
        newUserDataObject.setPhoneNumber("1234567890");
        newUserDataObject.setPassword("newPassword");

        UserEntity result = homeService.updateUserData(userToBeEdited, newUserDataObject);

        assertEquals("John", result.getFirstName());
        assertEquals("Doe", result.getLastName());
        assertEquals("john.doe@example.com", result.getEmail());
        assertEquals("1234567890", result.getPhoneNumber());
        verify(passwordEncoder).encode("newPassword");
    }

    @Test
    void testUpdateFieldIfNotEmpty() {
        String newValue = "John";
        Consumer<String> fieldUpdater = mock(Consumer.class);

        homeService.updateFieldIfNotEmpty(newValue, fieldUpdater);

        verify(fieldUpdater).accept(newValue);
    }

    @Test
    void testEditUserDetails_EmailAlreadyExists_ThrowsResourceAlreadyExistsException() {
        String email = "test@example.com";
        String password = "password";
        String emailUserToChange = "user@example.com";
        EditDetailsDTO newUserDataObject = new EditDetailsDTO();
        newUserDataObject.setEmail("new@example.com");

        UserEntity loggedUser = new UserEntity();
        loggedUser.setEmail(email);

        UserEntity userToBeEdited = new UserEntity();
        userToBeEdited.setEmail(emailUserToChange);

        when(validateData.validateUserWithPassword(email, password)).thenReturn(loggedUser);
        when(userEntityRepository.findByEmail(emailUserToChange)).thenReturn(userToBeEdited);
        when(userEntityRepository.existsByEmail(newUserDataObject.getEmail())).thenReturn(true);

        assertThrows(ResourceAlreadyExistsException.class,
                () -> homeService.editUserDetails(email, password, emailUserToChange, newUserDataObject));
    }

    @Test
    void testEditUserDetails_UserNotFound_ThrowsResourceNotFoundException() {
        String email = "test@example.com";
        String password = "password";
        String emailUserToChange = "nonExistingUser@example.com";
        EditDetailsDTO newUserDataObject = new EditDetailsDTO();

        when(validateData.validateUserWithPassword(email, password)).thenReturn(new UserEntity());
        when(userEntityRepository.findByEmail(emailUserToChange)).thenReturn(null);

        assertThrows(ResourceNotFoundException.class,
                () -> homeService.editUserDetails(email, password, emailUserToChange, newUserDataObject));
    }

    @Test
    void testEditUserDetails_AccessDenied_ThrowsAccessDeniedException() {
        String email = "test@example.com";
        String password = "password";
        String emailUserToChange = "otherUser@example.com";
        EditDetailsDTO newUserDataObject = new EditDetailsDTO();

        UserEntity loggedUser = new UserEntity();
        loggedUser.setEmail(email);

        UserEntity userToBeEdited = new UserEntity();
        userToBeEdited.setEmail(emailUserToChange);

        when(validateData.validateUserWithPassword(email, password)).thenReturn(loggedUser);
        when(userEntityRepository.findByEmail(emailUserToChange)).thenReturn(userToBeEdited);

        assertThrows(AccessDeniedException.class,
                () -> homeService.editUserDetails(email, password, emailUserToChange, newUserDataObject));
    }

    @Test
    void getAllUsersByParameter_WithValidInput_ReturnsPageOfUserDetailsDTO() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "John";
        Integer page = 0;
        Integer size = 10;

        when(userEntityRepository.findAllByFirstName(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<UserDetailsDTO> result = homeService.getAllUsersByParameter(email, password, searchTerm, SEARCH_USERS_BY_FIRST_NAME, page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllByFirstName(eq(searchTerm), any(PageRequest.class));
    }

    @Test
    void getAllUsersByParameter_WithInvalidUserDetailsDTO_ThrowsDataValidationException() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "John";
        Integer page = 0;
        Integer size = 10;

        UserEntity userEntity = new UserEntity();
        when(userEntityRepository.findAllByFirstName(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(userEntity)));

        when(modelMapper.map(eq(userEntity), eq(UserDetailsDTO.class)))
                .thenReturn(new UserDetailsDTO());

        when(validationUtil.isValid(any(UserDetailsDTO.class)))
                .thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                homeService.getAllUsersByParameter(email, password, searchTerm, SEARCH_USERS_BY_FIRST_NAME, page, size));

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllByFirstName(eq(searchTerm), any(PageRequest.class));
        verify(modelMapper).map(eq(userEntity), eq(UserDetailsDTO.class));
        verify(validationUtil).isValid(any(UserDetailsDTO.class));
    }

    @Test
    void getAllUsersByParameter_WithLastNameOption_ReturnsPageOfUserDetailsDTO() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "Doe";
        Integer page = 0;
        Integer size = 10;

        when(userEntityRepository.findAllByLastName(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<UserDetailsDTO> result = homeService.getAllUsersByParameter(email, password, searchTerm, SEARCH_USERS_BY_LAST_NAME, page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllByLastName(eq(searchTerm), any(PageRequest.class));
    }

    @Test
    void getAllUsersByParameterWithLastNameOption_WithInvalidUserDetailsDTO_ThrowsDataValidationException() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "John";
        Integer page = 0;
        Integer size = 10;

        UserEntity userEntity = new UserEntity();
        when(userEntityRepository.findAllByLastName(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(userEntity)));

        when(modelMapper.map(eq(userEntity), eq(UserDetailsDTO.class)))
                .thenReturn(new UserDetailsDTO());

        when(validationUtil.isValid(any(UserDetailsDTO.class)))
                .thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                homeService.getAllUsersByParameter(email, password, searchTerm, SEARCH_USERS_BY_LAST_NAME, page, size));

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllByLastName(eq(searchTerm), any(PageRequest.class));
        verify(modelMapper).map(eq(userEntity), eq(UserDetailsDTO.class));
        verify(validationUtil).isValid(any(UserDetailsDTO.class));
    }

    @Test
    void getAllUsersByParameter_WithPhoneNumberOption_ReturnsPageOfUserDetailsDTO() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "1234567890";
        Integer page = 0;
        Integer size = 10;

        when(userEntityRepository.findAllByPhoneNumber(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<UserDetailsDTO> result = homeService.getAllUsersByParameter(email, password, searchTerm, SEARCH_USERS_BY_PHONE_NUMBER, page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllByPhoneNumber(eq(searchTerm), any(PageRequest.class));
    }

    @Test
    void getAllUsersByParameterWithPhoneNumberOption_WithInvalidUserDetailsDTO_ThrowsDataValidationException() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "John";
        Integer page = 0;
        Integer size = 10;

        UserEntity userEntity = new UserEntity();
        when(userEntityRepository.findAllByPhoneNumber(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(userEntity)));

        when(modelMapper.map(eq(userEntity), eq(UserDetailsDTO.class)))
                .thenReturn(new UserDetailsDTO());

        when(validationUtil.isValid(any(UserDetailsDTO.class)))
                .thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                homeService.getAllUsersByParameter(email, password, searchTerm, SEARCH_USERS_BY_PHONE_NUMBER, page, size));

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllByPhoneNumber(eq(searchTerm), any(PageRequest.class));
        verify(modelMapper).map(eq(userEntity), eq(UserDetailsDTO.class));
        verify(validationUtil).isValid(any(UserDetailsDTO.class));
    }

    @Test
    void getAllUsersByParameter_WithEmailOption_ReturnsPageOfUserDetailsDTO() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "test@example.com";
        Integer page = 0;
        Integer size = 10;

        when(userEntityRepository.findAllByEmail(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<UserDetailsDTO> result = homeService.getAllUsersByParameter(email, password, searchTerm, SEARCH_USERS_BY_EMAIL, page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllByEmail(eq(searchTerm), any(PageRequest.class));
    }

    @Test
    void getAllUsersByParameterWithEmailOption_WithInvalidUserDetailsDTO_ThrowsDataValidationException() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "John";
        Integer page = 0;
        Integer size = 10;

        UserEntity userEntity = new UserEntity();
        when(userEntityRepository.findAllByEmail(eq(searchTerm), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(userEntity)));

        when(modelMapper.map(eq(userEntity), eq(UserDetailsDTO.class)))
                .thenReturn(new UserDetailsDTO());

        when(validationUtil.isValid(any(UserDetailsDTO.class)))
                .thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                homeService.getAllUsersByParameter(email, password, searchTerm, SEARCH_USERS_BY_EMAIL, page, size));

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllByEmail(eq(searchTerm), any(PageRequest.class));
        verify(modelMapper).map(eq(userEntity), eq(UserDetailsDTO.class));
        verify(validationUtil).isValid(any(UserDetailsDTO.class));
    }

    @Test
    void getAllUsersByParameter_WithMissingParameter_ThrowsMissingParameterException() {
        String email = "test@example.com";
        String password = "password";
        String searchTerm = "Doe";
        Integer page = 0;
        Integer size = 10;

        assertThrows(MissingParameterException.class, () ->
                homeService.getAllUsersByParameter(email, password, searchTerm, "INVALID_OPTION", page, size));
    }

    @Test
    void getAllUsersByDefault_WithValidInput_ReturnsPageOfUserDetailsDTO() {
        String email = "test@example.com";
        String password = "password";
        Integer page = 0;
        Integer size = 10;

        when(userEntityRepository.findAll(any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<UserDetailsDTO> result = homeService.getAllUsersByDefault(email, password, page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAll(any(PageRequest.class));
    }

    @Test
    void getAllUsersByDefault_WithInvalidUserDetailsDTO_ThrowsDataValidationException() {
        String email = "test@example.com";
        String password = "password";
        Integer page = 0;
        Integer size = 10;

        UserEntity userEntity = new UserEntity();
        when(userEntityRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(userEntity)));

        when(modelMapper.map(eq(userEntity), eq(UserDetailsDTO.class)))
                .thenReturn(new UserDetailsDTO());

        when(validationUtil.isValid(any(UserDetailsDTO.class)))
                .thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                homeService.getAllUsersByDefault(email, password, page, size));

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAll(any(PageRequest.class));
        verify(modelMapper).map(eq(userEntity), eq(UserDetailsDTO.class));
        verify(validationUtil).isValid(any(UserDetailsDTO.class));
    }


    @Test
    void getAllUsersOrderedByLastNameAndDateOfBirth_WithValidInput_ReturnsPageOfUserDetailsDTO() {
        String email = "test@example.com";
        String password = "password";
        Integer page = 0;
        Integer size = 10;

        when(userEntityRepository.findAllUsersOrderedByLastNameAndDateOfBirth(any(PageRequest.class)))
                .thenReturn(Page.empty());

        Page<UserDetailsDTO> result = homeService.getAllUsersOrderedByLastNameAndDateOfBirth(email, password, page, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllUsersOrderedByLastNameAndDateOfBirth(any(PageRequest.class));
    }

    @Test
    void getAllUsersOrderedByLastNameAndDateOfBirth_WithInvalidUserDetailsDTO_ThrowsDataValidationException() {
        String email = "test@example.com";
        String password = "password";
        Integer page = 0;
        Integer size = 10;

        UserEntity userEntity = new UserEntity();
        when(userEntityRepository.findAllUsersOrderedByLastNameAndDateOfBirth(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(Collections.singletonList(userEntity)));

        when(modelMapper.map(eq(userEntity), eq(UserDetailsDTO.class)))
                .thenReturn(new UserDetailsDTO());

        when(validationUtil.isValid(any(UserDetailsDTO.class)))
                .thenReturn(false);

        assertThrows(DataValidationException.class, () ->
                homeService.getAllUsersOrderedByLastNameAndDateOfBirth(email, password, page, size));

        verify(validateData).validateUserWithPassword(eq(email), eq(password));
        verify(userEntityRepository).findAllUsersOrderedByLastNameAndDateOfBirth(any(PageRequest.class));
        verify(modelMapper).map(eq(userEntity), eq(UserDetailsDTO.class));
        verify(validationUtil).isValid(any(UserDetailsDTO.class));
    }

}
