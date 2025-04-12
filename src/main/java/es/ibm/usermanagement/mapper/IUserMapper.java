package es.ibm.usermanagement.mapper;


import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.dto.UserResponse;
import es.ibm.usermanagement.entity.UserEntity;
import org.mapstruct.Mapper;



@Mapper(componentModel = "spring")
public interface IUserMapper {

    UserResponse toResponse(UserEntity user);
    UserEntity toEntity(UserCreateRequest request);


}
