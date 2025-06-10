package dev.itltcanz.bankapi.controller.impl;

import dev.itltcanz.bankapi.controller.UserController;
import dev.itltcanz.bankapi.dto.user.UserDtoCreate;
import dev.itltcanz.bankapi.dto.user.UserDtoPatch;
import dev.itltcanz.bankapi.dto.user.UserDtoPut;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class UserControllerImpl implements UserController {

  private final UserServiceImpl userService;

  public ResponseEntity<UserDtoResponse> createUser(UserDtoCreate userDto) {
    var createdUser = userService.createUser(userDto);
    return new ResponseEntity<>(createdUser, HttpStatus.CREATED);
  }

  public ResponseEntity<UserDtoResponse> getUserById(String userId) {
    var userDto = userService.getUserById(userId);
    return ResponseEntity.ok(userDto);
  }

  public ResponseEntity<Page<UserDtoResponse>> getUsers(int page, int size) {
    var pageable = PageRequest.of(page, size);
    return ResponseEntity.ok(userService.getUsers(pageable));
  }

  public ResponseEntity<Void> deleteUser(String userId) {
    userService.deleteUser(userId);
    return ResponseEntity.noContent().build();
  }

  public ResponseEntity<UserDtoResponse> updateUser(String userId, UserDtoPut userDto) {
    var updatedUser = userService.updateUser(userId, userDto);
    return ResponseEntity.ok(updatedUser);
  }

  public ResponseEntity<UserDtoResponse> patchUser(String userId, UserDtoPatch userDto) {
    var updatedUser = userService.updateUser(userId, userDto);
    return ResponseEntity.ok(updatedUser);
  }
}