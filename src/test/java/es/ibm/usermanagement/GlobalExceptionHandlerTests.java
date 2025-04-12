package es.ibm.usermanagement;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.ibm.usermanagement.controller.UserController;
import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.dto.UserResponse;
import es.ibm.usermanagement.exception.custom.AsyncTaskFailureException;
import es.ibm.usermanagement.exception.custom.UserAlreadyExistsException;
import es.ibm.usermanagement.exception.custom.UserNotFoundExeption;
import es.ibm.usermanagement.exception.handler.GlobalExceptionHandler;
import es.ibm.usermanagement.repository.ISearchUserRepository;
import es.ibm.usermanagement.service.IUserService;
import es.ibm.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;


@WebMvcTest({UserController.class, GlobalExceptionHandler.class})
public class GlobalExceptionHandlerTests {


    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private IUserService userService;
    private UserCreateRequest userCreateRequest;
    private UserResponse userResponse;
    private  String jsonUser;
    private String jsonUserEdadnovalid;
    private  ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

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
            userCreateRequest.setAge(35);
            jsonUser = objectMapper.writeValueAsString(userCreateRequest);
            userCreateRequest.setAge(1007);
            jsonUserEdadnovalid = objectMapper.writeValueAsString(userCreateRequest);

            System.out.println(jsonUser);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }



    @Test
    public void handleUserAlreadyExistsExceptionTest() throws Exception {
        doThrow(new UserAlreadyExistsException("User already exists")).when(userService).createUser(any(UserCreateRequest.class));


        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.message").value("User already exists"));


    }

    @Test
    public void handleAsyncTaskFailureExceptionTest() throws Exception {

        doThrow(new AsyncTaskFailureException("The async task failed with error:")).when(userService).createUser(any(UserCreateRequest.class));


        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUser))
                .andExpect(jsonPath("$.status").value(500))
                .andExpect(jsonPath("$.message").value("The async task failed with error:"));


    }

    @Test
    public void handleUserNotFoundExceptionTest() throws Exception {

        doThrow(new UserNotFoundExeption("User not Exist")).when(userService).getUser(UUID.fromString("bc076e6e-cf94-4701-b90d-ce0059e8434f"));

        mockMvc.perform(get("/api/v1/users/bc076e6e-cf94-4701-b90d-ce0059e8434f")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("User not Exist"));
    }

    @Test
    public void handleInvalidParamExceptionTest() throws Exception {
        mockMvc.perform(get("/api/v1/users/search").param("age","1007")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Invalid age"));
    }


    @Test
    public void handleInvalidParamExceptionAgeNotValidFormatTest() throws Exception {
        mockMvc.perform(get("/api/v1/users/search").param("age","A1007")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Invalid age"));
    }

    @Test
    public void handleInvalidParamExceptionInvalidNameTest() throws Exception {
        mockMvc.perform(get("/api/v1/users/search").param("name","A1007 *")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()))
                .andExpect(jsonPath("$.message").value("Invalid name"));
    }

    @Test
    public void handleMethodArgumentNotValidExceptionTest() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonUserEdadnovalid))
                .andExpect(jsonPath("$.status").value(HttpStatus.BAD_REQUEST.value()));


    }

}
