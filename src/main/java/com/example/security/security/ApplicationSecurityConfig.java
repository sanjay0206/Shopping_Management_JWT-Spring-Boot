package com.example.security.security;

import com.example.security.security.jwt.JWTConfig;
import com.example.security.security.jwt.JWTService;
import com.example.security.security.jwt.JWTTokenVerifier;
import com.example.security.security.jwt.JWTUsernamePasswordAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    private final PasswordConfig passwordConfig;
    private final UserDetailsService userDetailsService;
    private final JWTConfig jwtConfig;
    private final JWTService jwtService;

    @Autowired
    public ApplicationSecurityConfig(PasswordConfig passwordConfig,
                                     UserDetailsService userDetailsService,
                                     JWTConfig jwtConfig,
                                     JWTService jwtService) {
        this.passwordConfig = passwordConfig;
        this.userDetailsService = userDetailsService;
        this.jwtConfig = jwtConfig;
        this.jwtService = jwtService;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.cors(Customizer.withDefaults());

        JWTUsernamePasswordAuthFilter usernamePasswordAuthFilter
                = new JWTUsernamePasswordAuthFilter(
                        authenticationManager(), jwtConfig, jwtService)
                .getJWTAuthenticationFilter();
        JWTTokenVerifier jwtTokenVerifier = new JWTTokenVerifier(jwtConfig, jwtService);
        http// by default uses a Bean by the name of corsConfigurationSource
                .cors()
                .and()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(usernamePasswordAuthFilter)
                .addFilterAfter(jwtTokenVerifier, JWTUsernamePasswordAuthFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/v1/auth/signIn").permitAll()
                .antMatchers("/api/v1/auth/signUp").permitAll()
                .antMatchers("/api/v1/auth/signOut").permitAll()
                .anyRequest()
                .authenticated();
    }


    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Collections.singletonList("http://127.0.0.1:5500"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cookie", "Accept", "Content-Type"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordConfig.passwordEncoder());
        return authProvider;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authProvider());
    }

    @Bean
    public AuthenticationManager authenticationManager() throws Exception {
        return super.authenticationManager();
    }
}
