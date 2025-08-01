package eu.kaesebrot.dev.shortener.api;
import java.net.URI;
import java.util.UUID;

import eu.kaesebrot.dev.shortener.model.*;
import eu.kaesebrot.dev.shortener.service.AuthService;
import eu.kaesebrot.dev.shortener.service.AuthUserDetailsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import eu.kaesebrot.dev.shortener.repository.ShortenerUserRepository;
import eu.kaesebrot.dev.shortener.service.EmailConfirmationTokenService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;


@RestController
@RequestMapping("${shortener.hosting.subdirectory:}/api/v1/auth")
@Tag(name = "auth", description = "The auth API")
public class AuthController {
    private final ShortenerUserRepository userRepository;
    private final EmailConfirmationTokenService confirmationTokenService;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    AuthController(ShortenerUserRepository userRepository, EmailConfirmationTokenService confirmationTokenService, AuthService authService, AuthUserDetailsService authUserDetailsService, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.confirmationTokenService = confirmationTokenService;
        this.authService = authService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("users")
    AuthUser registerUser(HttpServletRequest request, @Valid @RequestBody AuthUserCreationRequest authUserCreationRequest) {

        if (userRepository.existsByEmail(authUserCreationRequest.getEmail()) || userRepository.existsByUsername(authUserCreationRequest.getUsername())) {
            throw new IllegalArgumentException("User already exists!");
        }

        AuthUser user = new AuthUser(authUserCreationRequest.getUsername(), passwordEncoder.encode(authUserCreationRequest.getRawPassword()), authUserCreationRequest.getEmail());
        userRepository.save(user);
        
        confirmationTokenService.generateAndSendConfirmationTokenToUser(user, URI.create(request.getRequestURL().toString()), String.format("api/v1/shortener/users/%s/confirm", user.getId().toString()));

        return user;
    }

    @GetMapping("users/{id}")
    AuthUser getUser(@PathVariable UUID id) {
        return userRepository.findById(id).orElseThrow();
    }

    @GetMapping("users")
    Page<AuthUser> getUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @GetMapping("users/{id}/confirm/{token}")
    AuthUser confirmUserAccount(@PathVariable("id") UUID id, @PathVariable("token") String rawToken) {
        AuthUser user = userRepository.findById(id).orElseThrow();
        confirmationTokenService.redeemToken(user, rawToken);

        return userRepository.findById(id).orElseThrow();
    }

    @PostMapping("login")
    AuthResponseInitial getJwt(@Valid @RequestBody AuthRequestInitial request) {
        AuthResponseBase responseBase = authService.authenticate(request);

        // TODO
        String rawRefreshToken = "token";
        passwordEncoder.encode(rawRefreshToken);
        return new AuthResponseInitial(responseBase, rawRefreshToken);
    }

    @PostMapping("refresh")
    AuthResponseRefresh refreshJwt(@Valid @RequestBody AuthRequestRefresh request) {
        return null;
    }
}
