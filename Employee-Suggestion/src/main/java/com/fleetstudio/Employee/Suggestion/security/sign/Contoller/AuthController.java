
package com.fleetstudio.Employee.Suggestion.security.sign.Contoller;
import com.fleetstudio.Employee.Suggestion.model.Employee;
import com.fleetstudio.Employee.Suggestion.security.sign.dto.AuthResponse;
import com.fleetstudio.Employee.Suggestion.security.sign.dto.LoginRequest;
import com.fleetstudio.Employee.Suggestion.security.sign.dto.LoginResponse;
import com.fleetstudio.Employee.Suggestion.security.sign.dto.RegisterRequest;
import com.fleetstudio.Employee.Suggestion.security.sign.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
        // Create a new user
        Employee user = new Employee();
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setName(registerRequest.getName());
        user.setPosition(registerRequest.getPosition());
        user.setDepartment(registerRequest.getDepartment());
        user.setRole("ROLE_"+registerRequest.getRole());  // Set default role
        boolean isRegistered = userService.register(user);

        if (isRegistered) {
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("User " + user.getName() + " registered successfully.");
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("User registration failed.");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> signInUser(@RequestBody LoginRequest loginRequest) {
        AuthResponse authResponse = userService.authenticate(loginRequest);

        if (authResponse != null) {

            return ResponseEntity.ok(new LoginResponse("Login successful",authResponse.getToken(),authResponse.getRole()));
        }


        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new LoginResponse("Invalid credentials.",null));

    }
}
