package dev.itltcanz.bankapi.service.impl;

import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;
import dev.itltcanz.bankapi.repository.UserRepo;
import dev.itltcanz.bankapi.service.AuthService;
import dev.itltcanz.bankapi.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("authService")
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

  private final UserRepo userRepo;
  private final UserService userService;
  private final PasswordEncoder encoder;
  private final AuthenticationManager authManager;
  private final ModelMapper modelMapper;

  @Override
  public User getCurrentUser() {
    var username = SecurityContextHolder.getContext().getAuthentication().getName();
    return userService.findUserById(username);
  }
  @Override
  public UserDtoResponse register(UserDtoRegistration userDto) {
    if (userRepo.existsByUsername(userDto.getUsername())) {
      throw new UsernameAlreadyUseException(
          "A user with nickname " + userDto.getUsername() + " already exists");
    }
    var user = modelMapper.map(userDto, User.class);
    user.setPassword(encoder.encode(user.getPassword()));
    user.setRole(Role.ROLE_USER);
    var savedUser = userRepo.save(user);
    return modelMapper.map(savedUser, UserDtoResponse.class);
  }

  @Override
  public void verify(UserDtoRegistration userDto) {
    authManager.authenticate(
        new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
  }
}
