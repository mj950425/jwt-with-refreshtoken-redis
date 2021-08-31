package mj.project.JWT.config;


import mj.project.JWT.jwt.JwtAuthenticationEntryPoint;
import mj.project.JWT.jwt.JwtCommonAuthorizationFilter;
import mj.project.JWT.jwt.JwtTokenUtil;
import mj.project.JWT.jwt.PrincipalDetailsService;
import mj.project.JWT.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true)

public class SecurityConfig extends WebSecurityConfigurerAdapter{

    @Autowired
    private PrincipalDetailsService principalDetailsService;
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    @Qualifier("AU-UserRepository")
    private UserRepository userRepository;
    @Autowired
    private CorsConfig corsConfig;
    @Bean
    public BCryptPasswordEncoder encodePWD() {
        return new BCryptPasswordEncoder();
    }

    @Autowired
    JwtTokenUtil jwtTokenUtil;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
    http
        .addFilter(corsConfig.corsFilter())
        .csrf().disable() //csrf 토큰
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
    .and()
        .formLogin().disable()
        .httpBasic().disable()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)
            .and()
            .addFilter(new JwtCommonAuthorizationFilter(authenticationManager(),userRepository,jwtTokenUtil))
            .authorizeRequests()
                .antMatchers("/user/**").access("hasRole('ROLE_USER')")
                .antMatchers("/admin/**").access("hasRole('ROLE_ADMIN')")
                .anyRequest().permitAll();
    }
}
