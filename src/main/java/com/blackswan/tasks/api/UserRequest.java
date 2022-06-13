package com.blackswan.tasks.api;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
import javax.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.FieldDefaults;

@Builder(toBuilder = true)
@NoArgsConstructor
@AllArgsConstructor
@Getter
@FieldDefaults(level = PRIVATE)
@EqualsAndHashCode
@ToString
public class UserRequest {

    @NotBlank(message = "username is mandatory")
    @JsonProperty("username")
    String userName;
    @NotBlank(message = "first name is mandatory")
    @JsonProperty("first_name")
    String firstName;
    @NotBlank(message = "last name is mandatory")
    @JsonProperty("last_name")
    String lastName;

}
