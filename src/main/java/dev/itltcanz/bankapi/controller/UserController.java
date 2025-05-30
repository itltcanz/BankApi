package dev.itltcanz.bankapi.controller;

import dev.itltcanz.bankapi.dto.user.UserDtoResponse;
import dev.itltcanz.bankapi.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/users")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class UserController {
    private final UserService userService;

    @GetMapping
    @Secured("ROLE_ADMIN")
    @Operation(
        summary = "Retrieve users with pagination",
        description = "Fetches a paginated list of users. Requires admin privileges"
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized access"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<Page<UserDtoResponse>> getUsers(
        @Parameter(description = "Page number (0-based)", example = "0")
        @RequestParam(defaultValue = "0") int page,
        @Parameter(description = "Number of items per page", example = "10")
        @RequestParam(defaultValue = "10") int size) {
        var pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(userService.getUsers(pageable));
    }
}
