package org.scientificcenter.security;

import org.scientificcenter.service.impl.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserServiceImpl userServiceImpl;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;
    private final PasswordEncoder passwordEncoder;

    public WebSecurityConfig(final UserServiceImpl userServiceImpl, final TokenAuthenticationFilter tokenAuthenticationFilter, final PasswordEncoder passwordEncoder) {
        this.userServiceImpl = userServiceImpl;
        this.tokenAuthenticationFilter = tokenAuthenticationFilter;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Autowired
    public void configureGlobal(final AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(this.userServiceImpl).passwordEncoder(this.passwordEncoder);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http.sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .antMatchers("/socket/**", "/api/registration/**", "/api/magazine/**", "/api/scientific_paper/**", "/api/users/**")
                .permitAll()
                .antMatchers("/api/scientific_areas/**")
                .hasAuthority("ROLE_ADMINISTRATOR")
                .antMatchers("/api/payment/**")
                .hasAnyAuthority("ROLE_USER", "ROLE_EDITOR", "ROLE_REVIEWER")
                .antMatchers("/api/files/**")
                .hasAnyAuthority("ROLE_EDITOR", "ROLE_REVIEWER")
                .anyRequest().authenticated()
                .and()
                .addFilterBefore(this.tokenAuthenticationFilter, BasicAuthenticationFilter.class);

        http.csrf().disable();
    }

    @Override
    public void configure(final WebSecurity web) {
        web.ignoring().antMatchers(HttpMethod.POST, "/api/login");
        web.ignoring().antMatchers(HttpMethod.GET, "/", "/webjars/**", "/*.html", "/favicon.ico", "/**/*.html", "/**/*.css", "/**/*.js", "/**/*.jpg", "/**/*.png", "/**/*.gif");
        web.ignoring().antMatchers("/v2/api-docs", "/configuration/ui", "/swagger-resources/**", "/configuration/security");
    }
}