package es.ibm.usermanagement.repository;

import es.ibm.usermanagement.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ISearchUserRepository extends JpaRepository<UserEntity, UUID> , JpaSpecificationExecutor<UserEntity> {

    @Query(value = "SELECT CASE WHEN COUNT(*) > 0 THEN true ELSE false END " +
            "FROM users " +
            "WHERE name = :name AND last_name = :lastName " +
            "AND age = :age AND postal_code = :postalCode",
            nativeQuery = true)
    boolean existsUserByNaturalKey(@Param("name") String name,
                                   @Param("lastName") String lastName,
                                   @Param("age") int age,
                                   @Param("postalCode") String postalCode);



}
