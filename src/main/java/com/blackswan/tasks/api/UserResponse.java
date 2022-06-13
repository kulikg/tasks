package com.blackswan.tasks.api;

import static lombok.AccessLevel.PRIVATE;

import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UserResponse {

    Long id;
    @JsonProperty("username")
    String userName;
    @JsonProperty("first_name")
    String firstName;
    @JsonProperty("last_name")
    String lastName;

}
