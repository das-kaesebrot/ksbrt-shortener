package eu.kaesebrot.dev.shortener.api;
import java.net.URI;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.kaesebrot.dev.shortener.model.ShortenerUser;
import eu.kaesebrot.dev.shortener.model.UserCreation;
import eu.kaesebrot.dev.shortener.repository.ShortenerUserRepository;
import eu.kaesebrot.dev.shortener.service.EmailConfirmationTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("/api/v1/shortener/users")
@Tag(name = "users", description = "The User API")
public class UserController {
    private final ShortenerUserRepository userRepository;
    private final EmailConfirmationTokenService confirmationTokenService;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    UserController(ShortenerUserRepository userRepository, EmailConfirmationTokenService confirmationTokenService) {
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
    }

    @PostMapping
    ShortenerUser registerUser(HttpServletRequest request, @Valid @RequestBody UserCreation userCreation) {

        if (userRepository.existsByEmail(userCreation.getEmail()) || userRepository.existsByUsername(userCreation.getUsername())) {
            throw new IllegalArgumentException("User already exists!");
        }

        ShortenerUser user = new ShortenerUser(userCreation.getUsername(), passwordEncoder.encode(userCreation.getRawPassword()), userCreation.getEmail());
        
        confirmationTokenService.generateAndSendConfirmationTokenToUser(user, URI.create(request.getRequestURL().toString()), "/api/v1/shortener/users/confirm");

        return user;
    }

    @GetMapping("{id}")
    ShortenerUser getUser(@PathVariable Long id) {
        return userRepository.findById(id).orElseThrow();
    }

    @GetMapping("confirm/{rawToken}")
    void confirmUserAccount(@PathVariable String rawToken) {
        confirmationTokenService.redeemToken(rawToken);
    }
}
