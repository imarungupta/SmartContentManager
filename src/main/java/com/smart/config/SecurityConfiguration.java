package com.smart.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfiguration;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration  {
//https://www.codejava.net/frameworks/spring-boot/fix-websecurityconfigureradapter-deprecated
// https://spring.io/blog/2022/02/21/spring-security-without-the-websecurityconfigureradapter
    @Bean
    public UserDetailsService getUserDetailsService(){
        return new UserDetailServiceImpl();
    }

    @Bean
    public BCryptPasswordEncoder getPasswordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // Pass the above bean to this DaoAuthenticationProvider to authenticate
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider daoAuthenticationProvider= new DaoAuthenticationProvider();
        daoAuthenticationProvider.setUserDetailsService(this.getUserDetailsService());
        daoAuthenticationProvider.setPasswordEncoder(this.getPasswordEncoder());

        return daoAuthenticationProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
         /* http
                .authorizeHttpRequests((auth) -> auth
                        .anyRequest().authenticated()
                )
                .httpBasic(withDefaults());

        return http.build();*/
        http.authorizeHttpRequests((auth)->{
            try {
                auth.requestMatchers("/admin/**")
                        .hasRole("ADMIN")
                        .requestMatchers("/user/**")
                        .hasRole("USER").requestMatchers("/**")
                        .permitAll();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).formLogin(login->login.loginPage("/signin")
                        .loginProcessingUrl("/do-login")
                        .defaultSuccessUrl("/user/index")) // .failureUrl("/login-fail")
                .csrf(AbstractHttpConfigurer::disable);
        //.httpBasic(Customizer.withDefaults()
       return http.build();
    }
    protected void configure(AuthenticationManagerBuilder authenticationManagerBuilder)throws Exception{
        authenticationManagerBuilder.authenticationProvider(daoAuthenticationProvider());
    }

    @Bean
    public AuthenticationManager authenticationManagerBean(AuthenticationConfiguration  configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}