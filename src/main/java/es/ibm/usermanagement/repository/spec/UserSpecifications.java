package es.ibm.usermanagement.repository.spec;

import es.ibm.usermanagement.entity.UserEntity;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {
    public static Specification<UserEntity> hasName(String name) {
        return (root, query, cb) -> {
            if (name == null) return null;
            return cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<UserEntity> hasAge(Integer age) {
        return (root, query, cb) -> {
            if (age == null) return null;
            return cb.equal(root.get("age"), age);
        };
    }
}
