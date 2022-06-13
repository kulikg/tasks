package com.blackswan.tasks;

import static com.blackswan.tasks.Fixtures.aUserRequest;
import static com.blackswan.tasks.Fixtures.otherUserRequest;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

import com.blackswan.tasks.api.UserRequest;
import com.blackswan.tasks.api.UserResponse;
import java.util.List;
import java.util.stream.Stream;
import lombok.val;
import org.junit.jupiter.api.Test;

class UserRequestControllerTest extends AbstractTest {

    @Test
    void createUser() {
        val status = post("api/user", aUserRequest).getStatusCode();
        assertThat(status).isEqualTo(CREATED);
    }

    @Test
    void listUsers() {
        Long aUserId = post("api/user", aUserRequest).getBody().getId();
        Long otherUserId = post("api/user", otherUserRequest).getBody().getId();
        val actual = get("api/user", UserResponse[].class).getBody();
        val expected = List.of(
                UserResponse.builder()
                        .userName(aUserRequest.getUserName())
                        .firstName(aUserRequest.getFirstName())
                        .lastName(aUserRequest.getLastName())
                        .id(aUserId)
                        .build(),
                UserResponse.builder()
                        .userName(otherUserRequest.getUserName())
                        .firstName(otherUserRequest.getFirstName())
                        .lastName(otherUserRequest.getLastName())
                        .id(otherUserId)
                        .build()
        );
        assertThat(actual).hasSameElementsAs(expected);
    }

    @Test
    void updateUser() {
        val userId = post("api/user", aUserRequest).getBody().getId();
        val updateUser = UserRequest.builder().firstName("changed").build();
        val status = put("api/user/" + userId, updateUser).getStatusCode();
        assertThat(status).isEqualTo(OK);
        val actual = get("api/user/" + userId, UserResponse.class).getBody().getFirstName();
        assertThat(actual).isEqualTo("changed");
    }

    @Test
    void addAlreadyExistingUser() {
        post("api/user", aUserRequest);
        val actual = post("api/user", aUserRequest).getStatusCode();
        assertThat(actual).isEqualTo(BAD_REQUEST);
    }

    @Test
    void userWithoutUsername() {
        val invalidUser = aUserRequest.toBuilder().userName("").build();
        val actual = post("api/user", invalidUser).getStatusCode();
        assertThat(actual).isEqualTo(BAD_REQUEST);
    }

}
