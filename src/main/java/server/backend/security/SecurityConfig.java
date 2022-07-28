package server.backend.security;

import server.backend.filter.CustomAuthenticationFilter;
import server.backend.filter.CustomAuthorizationFilter;
import server.backend.repository.IUserRepo;
import server.backend.service.UserServiceImpl;
import lombok.RequiredArgsConstructor;
// import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.http.HttpMethod.GET;

import java.util.Arrays;


@Configuration @EnableWebSecurity @RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final UserServiceImpl userDetailsService;


    private final IUserRepo userRepo;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }


    // @Bean
    // CorsConfigurationSource corsConfigurationSource()
    // {
    //   CorsConfiguration configuration = new CorsConfiguration();
    //   configuration.setAllowedOrigins(Arrays.asList("*"));
    //   configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "DELETE"));
    //   UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    //   source.registerCorsConfiguration("/**", configuration);
    //   return source;
    // }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean(), userRepo);
        //http.cors().configurationSource(corsConfigurationSource())
        customAuthenticationFilter.setFilterProcessesUrl("/api/v1/login");
        http.cors()
        .and()
        .csrf().disable()
        .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        .and()
        .authorizeRequests().antMatchers("/api/v1/login", "/api/v1/token_refresh", "/api/v1/register").permitAll()
        .and()
        .authorizeRequests().antMatchers().hasAnyRole("ADMIN")
        .and()
        .authorizeRequests().anyRequest().authenticated()
        .and().addFilter(customAuthenticationFilter)
        .addFilterBefore(new server.backend.filter.CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", 
        "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setExposedHeaders(Arrays.asList("*"));
        UrlBasedCorsConfigurationSource source = new 
        UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

   
}