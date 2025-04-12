package es.ibm.usermanagement;


import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.dto.UserResponse;
import es.ibm.usermanagement.entity.UserEntity;

import es.ibm.usermanagement.exception.custom.UserAlreadyExistsException;
import es.ibm.usermanagement.exception.custom.UserNotFoundExeption;
import es.ibm.usermanagement.mapper.IUserMapper;
import es.ibm.usermanagement.repository.ISearchUserRepository;
import es.ibm.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.core.KafkaTemplate;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceTests {

    @Mock
    private ISearchUserRepository userRepository;
    @Mock
    private IUserMapper userMapper;
    @Mock
    private KafkaTemplate<String, UserCreateRequest> kafkaTemplate;
    @InjectMocks
    private UserServiceImpl userService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createUserShouldThrowUserAlreadyExistsExceptionWhenUserExistsTest() {
        when(userRepository.existsUserByNaturalKey(any(String.class), any(String.class), any(Integer.class), any(String.class))).thenReturn(true);
        assertThrows(UserAlreadyExistsException.class, () -> userService.createUser(getRequest()));
    }

    @Test
    void getUser_shouldReturnUserResponse_whenUserExists() throws Exception {

        UUID userId = UUID.randomUUID();
        when(userRepository.findById(any(UUID.class))).thenReturn(Optional.of(getUser(userId)));
        when(userMapper.toResponse(any(UserEntity.class))).thenReturn(getUserResponse(userId));
        UserResponse userResponse = userService.getUser(userId);

        assertNotNull(userResponse);
        assertEquals("Gustavo", userResponse.getName());
        assertEquals("Moyano", userResponse.getLastName());
        assertEquals(30, userResponse.getAge());
        assertEquals("1722", userResponse.getPostalCode());
    }

    @Test
    void getUser_shouldThrowUserNotFoundException_whenUserDoesNotExist() {
        UUID userId = UUID.randomUUID();
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(UserNotFoundExeption.class, () -> userService.getUser(userId));
    }



    @Test
    void searchUsers_shouldReturnPagedResults() {
        String name = "Gustavo";
        Integer age = 30;
        List<UserEntity> users = getUserList();
        Pageable pageable = mock(Pageable.class);

        Page<UserEntity> page = new PageImpl<>(users,pageable,users.size());
        when(userRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);
        Page<UserResponse> result = userService.searchUsers(name, age, pageable);
        assertNotNull(result);
        verify(userRepository, times(1)).findAll(any(Specification.class), eq(pageable));
    }





    private UserCreateRequest getRequest(){
        return UserCreateRequest.builder()
                .age(30)
                .isSubscribed(true)
                .lastName("Moyano")
                .name("Gustavo")
                .postalCode("1722").build();
    }


    private UserEntity getUser(UUID userId){

        return  UserEntity.builder()
                .id(userId)
                .name("Gustavo")
                .lastName("Moyano")
                .age(30)
                .isSubscribed(true)
                .postalCode("1722").build();
    }

    private UserResponse getUserResponse(UUID userId){

        return UserResponse.builder()
                .id(userId)
                .name("Gustavo")
                .lastName("Moyano")
                .age(30)
                .isSubscribed(true)
                .postalCode("1722").build();
    }



    private List<UserEntity> getUserList(){
        ArrayList<UserEntity> users = new ArrayList<>();
        users.add(getUser(UUID.randomUUID()));
        return users;
    }





}
