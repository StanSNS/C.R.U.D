package backend.controller;

import backend.dto.AuthResponseDTO;
import backend.dto.EditDetailsDTO;
import backend.dto.UserDetailsDTO;
import backend.exception.MissingParameterException;
import backend.service.HomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static backend.constants.ActionConst.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class HomeControllerTest {

    @Mock
    private HomeService homeService;

    @InjectMocks
    private HomeController homeController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(homeController).build();
    }


    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.delete("/home")
                        .param("email", "test@email.com")
                        .param("password", "testPassword")
                        .param("userToDeleteEmail", "userToDelete@email.com"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(homeService, times(1))
                .deleteUser("test@email.com",
                        "testPassword",
                        "userToDelete@email.com");
    }

    @Test
    void testLogoutUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/home")
                        .param("email", "test@email.com")
                        .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(homeService, times(1)).logoutUser("test@email.com", "testPassword");
    }

    @Test
    public void testEditUser() {
        HomeService homeService = mock(HomeService.class);

        HomeController homeController = new HomeController(homeService);

        String email = "test@example.com";
        String password = "testPassword";
        String emailUserToChange = "userToChange@example.com";
        EditDetailsDTO newUserDataObject = new EditDetailsDTO();
        AuthResponseDTO expectedResponse = new AuthResponseDTO();

        when(homeService.editUserDetails(email, password, emailUserToChange, newUserDataObject))
                .thenReturn(expectedResponse);

        ResponseEntity<AuthResponseDTO> responseEntity = homeController.editUser(email, password, emailUserToChange, newUserDataObject);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(expectedResponse, responseEntity.getBody());

        verify(homeService, times(1)).editUserDetails(email, password, emailUserToChange, newUserDataObject);
    }

    @Test
    void getUsers_AllUsersDefault_ReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", ALL_USERS_DEFAULT)
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("currentPage", "1")
                        .param("sizeOnPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getUsers_AllUsersSortByLastNameAndDOB_ReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", ALL_USERS_SORT_BY_LAST_NAME_AND_DOB)
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("currentPage", "1")
                        .param("sizeOnPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getUsers_AllUsersFoundByParameter_ReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", ALL_USERS_FOUND_BY_PARAMETER)
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("searchTerm", "searchTerm")
                        .param("selectedSearchOption", "11232")
                        .param("currentPage", "1")
                        .param("sizeOnPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void getUsers_Get_SelectedUser_ReturnsOk() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", GET_SELECTED_USER)
                        .param("email", "test@example.com")
                        .param("password", "password")
                        .param("currentPage", "1")
                        .param("sizeOnPage", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    void testMissingParameterError() {
        assertThrows(MissingParameterException.class, () -> homeController
                .getUsers("InvalidAction",
                        "test@example.com",
                        "password",
                        "selectedUserEmail",
                        "2",
                        "213",
                        1,
                        2
                ));
    }

}
