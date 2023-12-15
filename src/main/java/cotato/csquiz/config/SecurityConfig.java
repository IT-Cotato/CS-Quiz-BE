package cotato.csquiz.config;

import cotato.csquiz.config.filter.JwtAuthenticationFilter;
import cotato.csquiz.config.filter.JwtAuthorizationFilter;
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

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final RefreshTokenRepository refreshTokenRepository;

    @Bean
    public BCryptPasswordEncoder encoder() {
        return new BCryptPasswordEncoder();
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
                .addFilterBefore(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests(request -> request
                        .requestMatchers("/v1/api/member/**").hasAnyRole("GENERAL", "MEMBER", "ADMIN", "EDUCATION")
                        .anyRequest().permitAll()
                );

        return http.build();
    }
}
