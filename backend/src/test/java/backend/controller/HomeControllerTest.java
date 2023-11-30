package backend.controller;

import backend.dto.UserDetailsDTO;
import backend.service.HomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static backend.constants.ActionConst.*;
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
    void testSortUsers() throws Exception {
        when(homeService.getAllUsersByDefault(anyString(), anyString()))
                .thenReturn(Collections.singletonList(new UserDetailsDTO()));
        when(homeService.getAllUsersOrderedByLastNameAndDateOfBirth(anyString(), anyString()))
                .thenReturn(Collections.singletonList(new UserDetailsDTO()));
        when(homeService.getAllUsersByParameter(anyString(), anyString(), anyString(), anyString()))
                .thenReturn(Collections.singletonList(new UserDetailsDTO()));
        when(homeService.getSelectedUser(anyString(), anyString(), anyString()))
                .thenReturn(new UserDetailsDTO());

        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", ALL_USERS_DEFAULT)
                        .param("email", "test@email.com")
                        .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());

        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", ALL_USERS_SORT_BY_LAST_NAME_AND_DOB)
                        .param("email", "test@email.com")
                        .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());

        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", ALL_USERS_FOUND_BY_PARAMETER)
                        .param("email", "test@email.com")
                        .param("password", "testPassword")
                        .param("searchTerm", "LastName")
                        .param("selectedSearchOption", "LastName"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isArray());

        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", GET_SELECTED_USER)
                        .param("email", "test@email.com")
                        .param("selectedUserEmail", "testPassword")
                        .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$").isNotEmpty());

        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("action", "UNKNOWN_ACTION")
                        .param("email", "test@email.com")
                        .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        verify(homeService, times(1)).getAllUsersByDefault(anyString(), anyString());
        verify(homeService, times(1)).getAllUsersOrderedByLastNameAndDateOfBirth(anyString(), anyString());
        verify(homeService, times(1)).getAllUsersByParameter(anyString(), anyString(), anyString(), anyString());
        verify(homeService, times(1)).getSelectedUser(anyString(), anyString(),anyString());
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

}
