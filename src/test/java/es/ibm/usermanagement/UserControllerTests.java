package es.ibm.usermanagement;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.ibm.usermanagement.controller.UserController;
import es.ibm.usermanagement.dto.StatusResponse;
import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.dto.UserResponse;
import es.ibm.usermanagement.service.IUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


public class UserControllerTests {
    @InjectMocks
    private UserController userController;
    @Mock
    private IUserService userService;
    private MockMvc mockMvc;
    private UserCreateRequest userCreateRequest;
    private UserResponse userResponse;
    private  String jsonUser;
    private  ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();


        userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName("Gustavo");
        userCreateRequest.setLastName("Alvarez");
        userCreateRequest.setAge(35);
        userCreateRequest.setPostalCode("1722");
        userCreateRequest.setIsSubscribed(true);

        userResponse = new UserResponse();
        userResponse.setId(UUID.randomUUID());
        userResponse.setName("Gustavo");
        userResponse.setLastName("Alvarez");
        userResponse.setAge(35);

        objectMapper = new ObjectMapper();

        try {
            jsonUser = objectMapper.writeValueAsString(userCreateRequest);
            System.out.println(jsonUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void createUserTest() throws Exception {

        StatusResponse statusResponse = new StatusResponse("User created successfully");
        CompletableFuture<StatusResponse> future = CompletableFuture.completedFuture(statusResponse);
        when(userService.createUser(any(UserCreateRequest.class))).thenReturn(future);


        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.message").value("User created successfully")); // Verificamos el contenido de la respuesta
    }

    @Test
    void getUserTest() throws Exception {

        when(userService.getUser(any(UUID.class))).thenReturn(userResponse);


        mockMvc.perform(get("/api/v1/users/{id}", userResponse.getId())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Gustavo"))
                .andExpect(jsonPath("$.lastName").value("Alvarez"))
                .andExpect(jsonPath("$.age").value(35)); // Verificamos los valores del usuario
    }

    @Test
    void searchUsersTest() {

        List<UserResponse> users = getListResponse();
        Pageable pageable = mock(Pageable.class);
        Page<UserResponse> page = new PageImpl<>(users,pageable,users.size());

        when(userService.searchUsers(any(String.class), any(Integer.class), eq(pageable))).thenReturn(page);

       userController.searchUsers("Gustavo",35, pageable);
    }



    private List<UserResponse> getListResponse(){

        List<UserResponse> aux = new ArrayList<>();
        aux.add(userResponse);
        return aux;
    }



}
