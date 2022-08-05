package june1.vgen.open.common.config;

import june1.vgen.open.common.filter.JwtFilter;
import june1.vgen.open.common.jwt.JwtAccessDeniedHandler;
import june1.vgen.open.common.jwt.JwtAuthenticationEntryPoint;
import june1.vgen.open.common.jwt.TokenProvider;
import june1.vgen.open.service.RedisUserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static june1.vgen.open.common.ConstantInfo.*;
import static org.springframework.http.HttpMethod.POST;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private static final String IGNORE_FAVICON = "/favicon.ico";
    private static final String IGNORE_ERROR = "/error";

    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final RedisUserService redisUserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        //오직 POST 를 통한 json 로그인만 허용
        http.httpBasic().disable()
                .formLogin().disable();

        //cors 처리를 위해..
        http.cors().configurationSource(corsConfigurationSource());

        //cross site request forgery disable
        http.csrf().disable();

        //JWT 인증을 사용하므로 세션(x)
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        //JWT 필터를 적용
        http.addFilterBefore(new JwtFilter(tokenProvider, redisUserService),
                UsernamePasswordAuthenticationFilter.class);

        //인증(정체 확인) 예외처리, 인가(자원 접근) 예외처리 등록
        http.exceptionHandling()
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler);

        http.authorizeRequests()
                .antMatchers(POST, "/auth/**").permitAll()
                .antMatchers("/member/**").authenticated()
                .antMatchers("/company/**").authenticated()
                .anyRequest().authenticated();
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy(ROLE_ADMIN + " > " + ROLE_MANAGER + " > " + ROLE_USER);
        return roleHierarchy;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration conf = new CorsConfiguration();
        conf.addAllowedOrigin("*");
        conf.addAllowedHeader("*");
        conf.addAllowedMethod("*");
        //conf.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }

    @Override
    public void configure(WebSecurity web) {
        web.ignoring().antMatchers(
                IGNORE_FAVICON,
                IGNORE_ERROR);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}