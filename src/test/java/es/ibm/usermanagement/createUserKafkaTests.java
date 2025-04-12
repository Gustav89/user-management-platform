package es.ibm.usermanagement;


import es.ibm.usermanagement.dto.StatusResponse;
import es.ibm.usermanagement.dto.UserCreateRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import es.ibm.usermanagement.exception.custom.AsyncTaskFailureException;
import es.ibm.usermanagement.exception.custom.UserAlreadyExistsException;
import es.ibm.usermanagement.repository.ISearchUserRepository;
import es.ibm.usermanagement.service.impl.UserServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@ExtendWith(MockitoExtension.class)
public class createUserKafkaTests {

    @Mock
    private KafkaTemplate<String, UserCreateRequest> userCreateRequestKafka;

    @Mock
    private ISearchUserRepository userRepository;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void testCreateUser_Success() {
        UserCreateRequest request = new UserCreateRequest("Gustavo", "Alvarez", 35, true,"1722");

        // Simulamos que el usuario NO existe
        when(userRepository.existsUserByNaturalKey("Gustavo", "Alvarez", 35, "1722")).thenReturn(false);

        // Simulamos el comportamiento del KafkaTemplate
        SettableListenableFuture<SendResult<String, UserCreateRequest>> kafkaFuture = new SettableListenableFuture<SendResult<String, UserCreateRequest>>();
        kafkaFuture.set(mock(SendResult.class));
        when(userCreateRequestKafka.send(eq("user-registration-events"), any(UserCreateRequest.class)))
                .thenReturn(kafkaFuture);

        // Ejecutamos el método
        CompletableFuture<StatusResponse> future = userService.createUser(request);

        // Esperamos el resultado
        StatusResponse result = future.join();

        // Verificamos que se procesó correctamente
        assertEquals("User creation request successfully processed", result.getMessage());
        verify(userCreateRequestKafka).send("user-registration-events", request);
    }

    @Test
    void testCreateUser_AlreadyExists() {
        UserCreateRequest request = new UserCreateRequest("Gustavo", "Moyano", 25, true,"67890");

        when(userRepository.existsUserByNaturalKey("Gustavo", "Moyano", 25, "67890")).thenReturn(true);

        assertThrows(UserAlreadyExistsException.class, () -> {
            userService.createUser(request);
        });
    }

    @Test
    void testCreateUser_KafkaFails() {
        UserCreateRequest request = new UserCreateRequest("Max", "Payne", 40, true, "90210");

        when(userRepository.existsUserByNaturalKey(any(), any(), anyInt(), any())).thenReturn(false);

        SettableListenableFuture<SendResult<String, UserCreateRequest>> kafkaFuture = new SettableListenableFuture<>();
        kafkaFuture.setException(new RuntimeException("Kafka is down"));

        when(userCreateRequestKafka.send(eq("user-registration-events"), any(UserCreateRequest.class)))
                .thenReturn(kafkaFuture);

        CompletableFuture<StatusResponse> future = userService.createUser(request);

        ExecutionException ex = assertThrows(ExecutionException.class, future::get);
        assertTrue(ex.getCause() instanceof AsyncTaskFailureException);
        assertTrue(ex.getCause().getMessage().contains("Kafka is down"));
    }



}
