package mj.project.JWT.controller;

import mj.project.JWT.exception.ResourceNotFoundExceptionDto;
import mj.project.JWT.jwt.PrincipalDetails;
import mj.project.JWT.model.UserMaster;
import mj.project.JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @Autowired
    @Qualifier("AU-UserRepository")
    private UserRepository userRepository;

    @GetMapping("/")
    public String home() {
        return "<h1>Welcome Home</h1>";
    }

    @GetMapping("/user")
    public UserMaster getCurrentUser(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        return userRepository.findByEmail(principalDetails.getEmail())
                .orElseThrow(() -> new ResourceNotFoundExceptionDto("User", "email", principalDetails.getEmail()));
    }
}
