package dev.itltcanz.bankapi.controller.impl;

import dev.itltcanz.bankapi.controller.AuthController;
import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.security.JwtGenerator;
import dev.itltcanz.bankapi.service.impl.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

  private final AuthServiceImpl authService;
  private final JwtGenerator jwtGenerator;

  public ResponseEntity<UserDtoResponse> register(UserDtoRegistration userDto) {
    var userDtoRegistration = authService.register(userDto);
    return new ResponseEntity<>(userDtoRegistration, HttpStatus.CREATED);
  }

  public ResponseEntity<String> login(UserDtoRegistration userDto) {
    authService.verify(userDto);
    return ResponseEntity.ok(jwtGenerator.generateToken(userDto.getUsername()));
  }

}
