package es.ibm.usermanagement;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.entity.UserEntity;
import es.ibm.usermanagement.mapper.IUserMapper;
import es.ibm.usermanagement.repository.ICreateUserRepository;
import es.ibm.usermanagement.service.impl.CreateUserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

public class CreateUserServiceTests {

    @InjectMocks
    private CreateUserService createUserService;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private ICreateUserRepository userRepository;

    @Mock
    private IUserMapper userMapper;

    private UserCreateRequest userCreateRequest;
    private String userCreateRequestJson;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);


        userCreateRequest = new UserCreateRequest();
        userCreateRequest.setName("Gustavo");
        userCreateRequest.setLastName("Alvarez");
        userCreateRequest.setAge(35);
        userCreateRequest.setPostalCode("1722");


        userCreateRequestJson = "{\"name\":\"Gustavo\",\"lastName\":\"Alvarez\",\"age\":35,\"postalCode\":\"1722\"}";
    }

    @Test
    void testCreateUser() throws JsonProcessingException {

        when(objectMapper.readValue(userCreateRequestJson, UserCreateRequest.class)).thenReturn(userCreateRequest);


        UserEntity userEntity = new UserEntity();
        userEntity.setName(userCreateRequest.getName());
        userEntity.setLastName(userCreateRequest.getLastName());
        userEntity.setAge(userCreateRequest.getAge());
        userEntity.setPostalCode(userCreateRequest.getPostalCode());


        when(userMapper.toEntity(userCreateRequest)).thenReturn(userEntity);


        createUserService.createUser(userCreateRequestJson);

        verify(userRepository, times(1)).save(userEntity);
    }

    @Test
    void testCreateUserWithJsonProcessingException() throws JsonProcessingException {

        when(objectMapper.readValue(userCreateRequestJson, UserCreateRequest.class)).thenThrow(JsonProcessingException.class);
        try {
            createUserService.createUser(userCreateRequestJson);
        } catch (Exception e) {

            verify(userRepository, never()).save(any(UserEntity.class));
        }
    }
}
