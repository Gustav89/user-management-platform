package es.ibm.usermanagement.repository;

import es.ibm.usermanagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ICreateUserRepository extends JpaRepository<UserEntity, UUID>{

}
