package dev.itltcanz.bankapi.service;

import dev.itltcanz.bankapi.dto.user.UserDtoRegistration;
import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.entity.User;
import dev.itltcanz.bankapi.entity.enumeration.Role;
import dev.itltcanz.bankapi.exception.NotFoundException;
import dev.itltcanz.bankapi.exception.UsernameAlreadyUseException;
import dev.itltcanz.bankapi.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepo userRepo;
    private final PasswordEncoder encoder;
    private final AuthenticationManager authManager;
    private final ModelMapper modelMapper;

    public UserDtoResponse register(UserDtoRegistration userDto) {
        if (userRepo.existsByUsername(userDto.getUsername())) {
            throw new UsernameAlreadyUseException("A user with nickname " + userDto.getUsername() + " already exists");
        }
        var user = modelMapper.map(userDto, User.class);
        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.ROLE_USER.toString());
        var savedUser = userRepo.save(user);
        return modelMapper.map(savedUser, UserDtoResponse.class);
    }

    public Page<UserDtoResponse> getUsers(PageRequest pageable) {
        var userPage = userRepo.findAll(pageable);
        return userPage.map(user -> modelMapper.map(user, UserDtoResponse.class));
    }

    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepo.findByUsername(username)
            .orElseThrow(() -> new NotFoundException("A user with nickname " + username + " was not found"));
    }

    public void verify(UserDtoRegistration userDto) {
        authManager.authenticate(new UsernamePasswordAuthenticationToken(userDto.getUsername(), userDto.getPassword()));
    }

    public void hasRights(String compareUserId) {
        var currentUser = getCurrentUser();
        if (!currentUser.getRole().equals(Role.ROLE_ADMIN.toString()) && !currentUser.getId().equals(UUID.fromString(compareUserId))) {
            throw new AccessDeniedException("Access denied");
        }
    }
}