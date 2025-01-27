package com.t2.t2.Security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
@EnableWebSecurity
public class SecurityConfig{
   

   @Autowired
   private UserAuthService userAuthService;

   @Autowired
   public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception{
      auth.userDetailsService(userAuthService).passwordEncoder(passwordEncoder());
   }

   @Bean
   public PasswordEncoder passwordEncoder(){
    return new BCryptPasswordEncoder();
   }

   @Bean
   public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
       http
           .csrf(csrf -> csrf.disable())
           .authorizeHttpRequests(auth -> auth
               .requestMatchers("/static/**", "/css/**", "/images/**","/js/**").permitAll()
               .requestMatchers("/newuser", "/register", "/entrar").permitAll()
               .requestMatchers("/condutor").hasRole("CONDUTOR")
               .requestMatchers("/passageiro", "/index").hasRole("PASSAGEIRO")
               .requestMatchers("/admin").hasRole("ADMIN")
               .requestMatchers("/aceitarViagem","/remover","/procurarPassageiro","/passageiroViagens","/removerPedido","/procurarCondutor","/aprovar","/desaprovar","/join","/viagenspedidas","/viagensdisponiveis", "/leave","/search").authenticated()
             
           )
           .formLogin(loginForm -> loginForm
               .loginPage("/entrar")
               .loginProcessingUrl("/j_spring_security_check")
               .failureUrl("/entrar?error")
               .successHandler((request, response, authentication) -> {
                   authentication.getAuthorities().forEach(auth -> {
                       String role = auth.getAuthority();
                       try {
                           if ("ROLE_CONDUTOR".equals(role)) {
                               response.sendRedirect("/condutor");
                           } else if ("ROLE_PASSAGEIRO".equals(role)) {
                               response.sendRedirect("/passageiro");
                           } else if ("ROLE_ADMIN".equals(role)) {
                               response.sendRedirect("/admin");
                           } else {
                               response.sendRedirect("/register");
                           }
                       } catch (Exception e) {
                           e.printStackTrace();
                       }
                   });
               })
               .permitAll()
               .usernameParameter("username")
               .passwordParameter("password")
           )
           .logout(logout -> logout.permitAll());
           
       return http.build();
   }
     
  
}
