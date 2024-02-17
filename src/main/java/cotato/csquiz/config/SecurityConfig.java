package cotato.csquiz.config;

import cotato.csquiz.config.filter.JwtAuthenticationFilter;
import cotato.csquiz.config.filter.JwtAuthorizationFilter;
import cotato.csquiz.config.filter.JwtExceptionFilter;
import cotato.csquiz.config.jwt.JwtUtil;
import cotato.csquiz.config.jwt.RefreshTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private static final String[] WHITE_LIST = {
            "/v1/api/generation",
            "/v1/api/session",
            "/swagger-ui/**"
    };

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;
    private final CorsFilter corsFilter;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return httpSecurity.getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        AuthenticationManagerBuilder sharedObject = http.getSharedObject(AuthenticationManagerBuilder.class);
        AuthenticationManager authenticationManager = sharedObject.build();
        http.authenticationManager(authenticationManager);

        http.csrf().disable()
                .cors().disable()
                .formLogin().disable()
                .addFilter(new JwtAuthenticationFilter(authenticationManager, jwtUtil, refreshTokenRepository))
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtExceptionFilter(), JwtAuthorizationFilter.class)
                .addFilter(corsFilter)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/v1/api/admin/**").hasRole("ADMIN")
                        .requestMatchers(WHITE_LIST).permitAll()
                        .requestMatchers("/v1/api/generation/**").hasAnyRole("GENERAL", "ADMIN")
                        .requestMatchers("/v1/api/session/**").hasAnyRole("GENERAL", "ADMIN")
                        .requestMatchers("/v1/api/socket/**").hasAnyRole("EDUCATION", "ADMIN")
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
