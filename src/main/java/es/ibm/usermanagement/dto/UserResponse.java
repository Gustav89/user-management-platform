package es.ibm.usermanagement.dto;


import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private String name;
    private String lastName;
    private Integer age;
    private Boolean isSubscribed;
    private String postalCode;
    private LocalDateTime createdAt;
}
