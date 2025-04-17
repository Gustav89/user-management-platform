package es.ibm.usermanagement.service.impl;

import es.ibm.usermanagement.dto.StatusResponse;
import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.dto.UserResponse;
import es.ibm.usermanagement.entity.UserEntity;
import es.ibm.usermanagement.exception.custom.AsyncTaskFailureException;
import es.ibm.usermanagement.exception.custom.UserAlreadyExistsException;
import es.ibm.usermanagement.exception.custom.UserNotFoundExeption;
import es.ibm.usermanagement.mapper.IUserMapper;
import es.ibm.usermanagement.repository.ISearchUserRepository;
import es.ibm.usermanagement.repository.spec.UserSpecifications;
import es.ibm.usermanagement.service.IUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.kafka.support.SendResult;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;


@Slf4j
@Service("UserService")
public class UserServiceImpl implements IUserService {

    private final ISearchUserRepository userRepository;
    private final IUserMapper userMapper;

    private final KafkaTemplate<String, UserCreateRequest> userCreateRequestKafka;

    public  UserServiceImpl (ISearchUserRepository repository, IUserMapper userMapper, KafkaTemplate<String, UserCreateRequest> userCreateRequestKafka){
        this.userRepository = repository;
        this.userMapper = userMapper;
        this.userCreateRequestKafka = userCreateRequestKafka;
    }

    @Override
    @Async
    public CompletableFuture<StatusResponse>  createUser(UserCreateRequest request) {

        log.info("init create user event");
        if(userRepository.existsUserByNaturalKey(request.getName(), request.getLastName(), request.getAge(), request.getPostalCode())){
            throw new UserAlreadyExistsException("User already exists");
        }
        CompletableFuture<StatusResponse> future = new CompletableFuture<>();


        ListenableFuture<SendResult<String, UserCreateRequest>> kafkaFuture = userCreateRequestKafka.send("user-registration-events", request);
        kafkaFuture.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(SendResult<String, UserCreateRequest> result) {
                future.complete(StatusResponse.builder().message("User creation request successfully processed").build());
            }
            @Override
            public void onFailure(Throwable ex) {
                future.completeExceptionally(new AsyncTaskFailureException("The async task failed with error: " + ex.getMessage(), ex));

            }
        });

        return future;
    }

    @Override
    @Cacheable(value = "users", key = "#id" , cacheManager = "ttlCacheManager")
    public UserResponse getUser(UUID id) throws Exception {
        Optional<UserEntity> userEntity = userRepository.findById(id);
        if(userEntity.isPresent()){
            return userMapper.toResponse(userEntity.get());
        }
        throw new UserNotFoundExeption("User not Exist");
    }


    @Cacheable(value = "userSearchCache",
            key = "T(java.util.Objects).hash(#name, #age, #pageable.pageNumber, #pageable.pageSize, #pageable.sort)",
            cacheManager = "ttlCacheManager")
    @Override
    public Page<UserResponse> searchUsers(String name, Integer age, Pageable pageable) {
        Specification<UserEntity> spec = Specification
                .where(UserSpecifications.hasName(name))
                .and(UserSpecifications.hasAge(age));

        Page<UserEntity> userResponsePage =  userRepository.findAll(spec, pageable);
      return userResponsePage.map(userMapper::toResponse);
    }
}
