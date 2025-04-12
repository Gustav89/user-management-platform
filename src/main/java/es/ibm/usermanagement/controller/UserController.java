package es.ibm.usermanagement.controller;

import es.ibm.usermanagement.dto.StatusResponse;
import es.ibm.usermanagement.dto.UserCreateRequest;
import es.ibm.usermanagement.dto.UserResponse;
import es.ibm.usermanagement.service.IUserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    private final IUserService userService;

    public UserController (IUserService userService){
        this.userService = userService;
    }


    @PostMapping(produces = "application/json")
    public ResponseEntity<StatusResponse> createUser(@RequestBody @Valid UserCreateRequest request) {
        return ResponseEntity.accepted().body(userService.createUser(request).join());
    }

    @GetMapping(value = "/{id}", produces =  "application/json")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) throws Exception {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @GetMapping(value = "/search" , produces =  "application/json")
    public ResponseEntity<Page<UserResponse>> searchUsers(
            @RequestParam(required = false)  String name,
            @RequestParam(required = false)  Integer age,
            Pageable pageable
    ) {
        return ResponseEntity.ok(userService.searchUsers(name,age,pageable));
    }
}

