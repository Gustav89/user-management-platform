package es.ibm.usermanagement.dto;


import lombok.*;

import javax.validation.constraints.*;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserCreateRequest {

    @NotBlank(message = "Last name must not be blank")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+([\\s'-][A-ZÁÉÍÓÚÑa-záéíóúñ]+)*$", message = "Invalid name format")
    private String name;

    @NotBlank(message = "Last name must not be blank")
    @Pattern(regexp = "^[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+([\\s'-][A-ZÁÉÍÓÚÑa-záéíóúñ]+)*$", message = "Invalid lastName format")
    private String lastName;

    @Min(value = 1, message = "Age must be greater than 0")
    @Max(value = 150, message = "Age must be realistic")
    private Integer age;

    @NotNull
    private Boolean isSubscribed;

    @Pattern(regexp = "^[A-Za-z0-9\\-\\s]{3,10}$", message = "Invalid postal code format")
    private String postalCode;

}
