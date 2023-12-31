package backend.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SpringSecurityConfig implements WebMvcConfigurer {

    /**
     * initializing dependencies with lombok @RequiredArgsConstructor
     */
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Creates a BCryptPasswordEncoder instance for password hashing and validation.
     *
     * @return A BCryptPasswordEncoder instance for password encoding.
     */
    @Bean
    public static PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Configures security filters and policies for HTTP requests using Spring Security.
     *
     * @param http The HttpSecurity object to configure.
     * @return A SecurityFilterChain with the defined security rules.
     * @throws Exception if there's an issue configuring security.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // Disable CSRF protection
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests((authorize) -> {
            // Define request matchers and permissions
            authorize.requestMatchers(
                    "/",
                    "/auth/login",
                    "/auth/register",
                    "/home"
            ).permitAll();
            authorize.requestMatchers(AUTH_SWAGGER_WHITELIST).permitAll();
            authorize.anyRequest().authenticated();
        });
        http.httpBasic(Customizer.withDefaults());
        http.exceptionHandling(exception -> exception.authenticationEntryPoint(authenticationEntryPoint));
        return http.build();
    }


    /**
     * Whitelist of URL patterns for which authentication is not required. Requests to these endpoints are allowed without
     * authentication, and they typically include authentication-related endpoints and Swagger API documentation.
     *
     * <p>The URLs in this whitelist are:</p>
     * <ul>
     *     <li>{@code "/api/v1/auth/**"}: Endpoints related to authentication.</li>
     *     <li>{@code "/v3/api-docs/**"}: Swagger API documentation JSON endpoint.</li>
     *     <li>{@code "/v3/api-docs.yaml"}: Swagger API documentation YAML endpoint.</li>
     *     <li>{@code "/swagger-ui/**"}: Swagger UI resources.</li>
     *     <li>{@code "/swagger-ui.html"}: Swagger UI HTML page.</li>
     * </ul>
     */
    private final String[] AUTH_SWAGGER_WHITELIST = {
            "/api/v1/auth/**",
            "/v3/api-docs/**",
            "/v3/api-docs.yaml",
            "/swagger-ui/**",
            "/swagger-ui.html",
    };


    /**
     * Creates an AuthenticationManager using the provided AuthenticationConfiguration.
     *
     * @param configuration The AuthenticationConfiguration object to configure the manager.
     * @return An AuthenticationManager instance.
     * @throws Exception if there's an issue creating the AuthenticationManager.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

}