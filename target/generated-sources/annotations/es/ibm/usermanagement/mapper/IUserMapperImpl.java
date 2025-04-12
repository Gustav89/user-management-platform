package es.ibm.usermanagement.mapper;

import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.dto.UserResponse;
import es.ibm.usermanagement.entity.UserEntity;
import javax.annotation.processing.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-04-12T08:29:14-0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 21.0.5 (Oracle Corporation)"
)
@Component
public class IUserMapperImpl implements IUserMapper {

    @Override
    public UserResponse toResponse(UserEntity user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.uuid( user.getUuid() );
        userResponse.name( user.getName() );
        userResponse.lastName( user.getLastName() );
        userResponse.age( user.getAge() );
        userResponse.isSubscribed( user.getIsSubscribed() );
        userResponse.postalCode( user.getPostalCode() );
        userResponse.createdAt( user.getCreatedAt() );

        return userResponse.build();
    }

    @Override
    public UserEntity toEntity(UserCreateRequest request) {
        if ( request == null ) {
            return null;
        }

        UserEntity.UserEntityBuilder userEntity = UserEntity.builder();

        userEntity.name( request.getName() );
        userEntity.lastName( request.getLastName() );
        userEntity.age( request.getAge() );
        userEntity.isSubscribed( request.getIsSubscribed() );
        userEntity.postalCode( request.getPostalCode() );

        return userEntity.build();
    }
}
