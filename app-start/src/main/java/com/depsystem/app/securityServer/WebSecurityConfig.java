/**
 * @author JOJO
 * @class WebSecurityConfig
 * @date 2023/4/20
 * @apiNote
 */

package com.depsystem.app.securityServer;

import cn.hutool.json.JSONUtil;


import com.depsystem.app.securityServer.handler.AuthenticationsFailureHandler;
import com.depsystem.app.securityServer.handler.AuthenticationsSuccessHandler;
import com.depsystem.app.securityServer.securityFilter.AuthenticationServerImpl;
import com.depsystem.app.systemServer.util.ResponseResult;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.io.IOException;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig {

    @Resource
    AuthenticationServerImpl authenticationServer;

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity httpSecurity) throws Exception {
        return  httpSecurity.userDetailsService(authenticationServer)
                .getSharedObject(AuthenticationManagerBuilder.class)
                .build();
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        http.authorizeHttpRequests(author ->
                        author
                                .requestMatchers("/captcha").permitAll()
                                .requestMatchers("/index").hasAnyRole("admin1")
                                .requestMatchers("/{name}/**").hasAnyRole("admin2","admin3")
                                .anyRequest().authenticated()
                )
                .formLogin()
                .loginProcessingUrl("/api/login")
                .successHandler(new AuthenticationsSuccessHandler())
                .failureHandler(new AuthenticationsFailureHandler())
                .and()
                .logout()
                .logoutUrl("/api/logout")
                .and()
                .cors()
                .configurationSource(this.configurationSource())
                .and()
                .csrf().disable()
                .exceptionHandling()
                .authenticationEntryPoint(this::onAuthenticationFailure);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    /**
     * 跨域配置
     */
    private CorsConfigurationSource configurationSource(){
        CorsConfiguration cors = new CorsConfiguration();
        cors.addAllowedOriginPattern("*");
        cors.addAllowedHeader("*");
        cors.addAllowedMethod("*");
        cors.setAllowCredentials(true);
        cors.addExposedHeader("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**",cors);
        return source;
    }
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException {
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(JSONUtil.toJsonPrettyStr(ResponseResult.failed(401,exception.getMessage())));
    }
}
