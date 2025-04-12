package es.ibm.usermanagement.service;

import es.ibm.usermanagement.dto.StatusResponse;
import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.dto.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IUserService {
    CompletableFuture<StatusResponse> createUser(UserCreateRequest request);
    UserResponse getUser(UUID id) throws Exception;
    Page<UserResponse> searchUsers(String name, Integer age, Pageable pageable);
}
