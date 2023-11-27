package backend.controller;

import backend.dto.UserDetailsDTO;
import backend.service.HomeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

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
    void testGetAllUsers() throws Exception {
        UserDetailsDTO userDto = new UserDetailsDTO();
        when(homeService.getAllUsers(anyString(), anyString())).thenReturn(Collections.singletonList(userDto));

        mockMvc.perform(MockMvcRequestBuilders.get("/home")
                        .param("email", "test@email.com")
                        .param("password", "testPassword"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON));
        verify(homeService, times(1)).getAllUsers("test@email.com", "testPassword");
    }


    @Test
    void testDeleteUser() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.put("/home")
                        .param("email", "test@email.com")
                        .param("password", "testPassword")
                        .param("userToDeleteEmail", "userToDelete@email.com"))
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(homeService, times(1)).deleteUser("test@email.com", "testPassword", "userToDelete@email.com");
    }




}
