package es.ibm.usermanagement.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.entity.UserEntity;
import es.ibm.usermanagement.mapper.IUserMapper;
import es.ibm.usermanagement.repository.ICreateUserRepository;
import es.ibm.usermanagement.repository.ISearchUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;



@Slf4j
@Service
public class CreateUserService {

    private final ObjectMapper objectMapper;
    private final ICreateUserRepository userRepository;
    private final IUserMapper userMapper;

    public CreateUserService(ObjectMapper objectMapper, ICreateUserRepository userRepository, IUserMapper userMapper) {
        this.objectMapper = objectMapper;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    @Transactional
    @KafkaListener(topics = "user-registration-events", groupId = "user-group")
    public void createUser(String request) {
        log.info("init create user with kafkaListener");
        try {
            UserCreateRequest event = objectMapper.readValue(request, UserCreateRequest.class);
            UserEntity user = userMapper.toEntity(event);
            userRepository.save(user);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
}
