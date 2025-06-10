package dev.itltcanz.bankapi.service.impl;

import dev.itltcanz.bankapi.dto.user.UserDto;
import dev.itltcanz.bankapi.dto.user.UserDtoCreate;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;
import dev.itltcanz.bankapi.repository.UserRepo;
import dev.itltcanz.bankapi.service.UserService;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service("userService")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepo userRepo;
  private final ModelMapper modelMapper;
  private final PasswordEncoder passwordEncoder;

  @Override
  public UserDtoResponse createUser(UserDtoCreate userDto) {
    if (userRepo.existsByUsername(userDto.getUsername())) {
      throw new UsernameAlreadyUseException(
          "Username " + userDto.getUsername() + " is already in use");
    }
    var user = new User();
    user.setUsername(userDto.getUsername());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setRole(userDto.getRole());
    var savedUser = userRepo.save(user);
    return modelMapper.map(savedUser, UserDtoResponse.class);
  }

  @Override
  public Page<UserDtoResponse> getUsers(PageRequest pageable) {
    var userPage = userRepo.findAll(pageable);
    return userPage.map(user -> modelMapper.map(user, UserDtoResponse.class));
  }

  @Override
  public void deleteUser(String userId) {
    var user = userRepo.findById(UUID.fromString(userId))
        .orElseThrow(() -> new NotFoundException("User with id " + userId + " not found"));
    userRepo.delete(user);
  }

  @Override
  public UserDtoResponse updateUser(String userId, UserDto userDto) {
    var user = findUserById(userId);
    if (userRepo.existsByUsername(userDto.getUsername())) {
      throw new UsernameAlreadyUseException(
          "Username " + userDto.getUsername() + " is already in use");
    }
    user.setUsername(userDto.getUsername());
    user.setPassword(passwordEncoder.encode(userDto.getPassword()));
    user.setRole(userDto.getRole());
    var savedUser = userRepo.save(user);
    return modelMapper.map(savedUser, UserDtoResponse.class);
  }

  @Override
  public User findUserById(String userId) {
    return userRepo.findById(UUID.fromString(userId))
        .orElseThrow(() -> new NotFoundException("A user with id " + userId + " was not found"));
  }

  @Override
  public UserDtoResponse getUserById(String userId) {
    return modelMapper.map(findUserById(userId), UserDtoResponse.class);
  }
}