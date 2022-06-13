package com.blackswan.tasks.service;

import static java.util.stream.Collectors.toList;
import static lombok.AccessLevel.PRIVATE;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.blackswan.tasks.api.UserRequest;
import com.blackswan.tasks.api.UserResponse;
import com.blackswan.tasks.domain.UserEntity;
import com.blackswan.tasks.repository.UserRepository;
import java.util.List;
import java.util.Optional;
import javax.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;

    @Transactional
    public Long createNewUser(UserRequest userRequest) {
        val userEntity = UserEntity.builder()
                .userName(userRequest.getUserName())
                .firstName(userRequest.getFirstName())
                .lastName(userRequest.getLastName())
                .build();
        return userRepository.save(userEntity).getId();
    }

    @Transactional
    public void updateUser(Long id, UserRequest userRequest) {
        val existingEntity = userRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST, "no such user"));
        val ongoingUserUpdate = existingEntity.toBuilder();
        Optional.ofNullable(userRequest.getUserName()).ifPresent(ongoingUserUpdate::userName);
        Optional.ofNullable(userRequest.getFirstName()).ifPresent(ongoingUserUpdate::firstName);
        Optional.ofNullable(userRequest.getLastName()).ifPresent(ongoingUserUpdate::lastName);
        userRepository.save(ongoingUserUpdate.build());
    }

    @Transactional
    public Optional<UserResponse> findUser(Long userId) {
        return userRepository.findById(userId)
                .map(this::translateEntityToResponse);
    }

    @Transactional
    public List<UserResponse> listAllUser() {
        return userRepository.findAll()
                .stream()
                .map(this::translateEntityToResponse)
                .collect(toList());
    }

    private UserResponse translateEntityToResponse(UserEntity entity) {
        return UserResponse.builder()
                .id(entity.getId())
                .userName(entity.getUserName())
                .firstName(entity.getFirstName())
                .lastName(entity.getLastName())
                .build();

    }
}
