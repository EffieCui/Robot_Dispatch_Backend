package com.flag.robot_dispatch.config;

import com.flag.robot_dispatch.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.sql.DataSource;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

   @Autowired
   private DataSource dataSource;

   @Autowired
   private JwtFilter jwtFilter;

   @Override
   protected void configure(HttpSecurity http) throws Exception {
      http
              .authorizeRequests()
              .antMatchers(HttpMethod.POST, "/register/*").permitAll()
              .antMatchers(HttpMethod.POST, "/authenticate/*").permitAll()
              .antMatchers("/stays").hasAuthority("ROLE_HOST")
              .antMatchers("/stays/*").hasAuthority("ROLE_HOST")
              .antMatchers("/search").hasAuthority("ROLE_GUEST")
              .antMatchers("/reservations").hasAuthority("ROLE_GUEST")
              .antMatchers("/reservations/*").hasAuthority("ROLE_GUEST")
              .anyRequest().authenticated()
              .and()
              .csrf()
              .disable();
      http
              .sessionManagement()
              .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
              .and()
              .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
   }

   @Bean
   public PasswordEncoder passwordEncoder() {
      return new BCryptPasswordEncoder();
   }

   @Override
   protected void configure(AuthenticationManagerBuilder auth) throws Exception {
      auth.jdbcAuthentication().dataSource(dataSource)
              .passwordEncoder(passwordEncoder())
              .usersByUsernameQuery("SELECT username, password, enabled FROM user WHERE username = ?")
              .authoritiesByUsernameQuery("SELECT username, authority FROM authority WHERE username = ?");
   }

   @Override
   @Bean
   public AuthenticationManager authenticationManagerBean() throws Exception {
      return super.authenticationManagerBean();
   }
}
