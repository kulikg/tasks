package com.blackswan.tasks.controller;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.blackswan.tasks.api.IdResponse;
import com.blackswan.tasks.api.UserRequest;
import com.blackswan.tasks.api.UserResponse;
import com.blackswan.tasks.service.UserService;
import java.util.List;
import javax.validation.Valid;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
@Value
@Slf4j
public class UserController {

    UserService userService;

    @GetMapping
    @ResponseStatus(OK)
    public List<UserResponse> listAllUser() {
        return userService.listAllUser();
    }

    @GetMapping("/{id}")
    @ResponseStatus(OK)
    public UserResponse getUserById(@PathVariable Long id) {
        return userService.findUser(id).orElseThrow();
    }

    @PostMapping
    @ResponseStatus(CREATED)
    public IdResponse createNewUser(@Valid @RequestBody UserRequest userRequest) {
        return new IdResponse(userService.createNewUser(userRequest));
    }

    @PutMapping("/{id}")
    @ResponseStatus(OK)
    public String updateUser(@PathVariable Long id, @RequestBody UserRequest userRequest) {
        userService.updateUser(id, userRequest);
        return "OK";
    }

}
