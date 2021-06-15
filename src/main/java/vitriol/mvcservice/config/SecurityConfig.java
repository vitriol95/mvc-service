package vitriol.mvcservice.config;

import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(WebSecurity web) throws Exception {

        web
                .ignoring()
                .mvcMatchers("/img/**","/h2-console/**")
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());

    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .authorizeRequests()
                .mvcMatchers("/", "/login", "/sign-up").permitAll()
                .anyRequest()
                .authenticated();

        http
                .formLogin()
                .loginPage("/login").permitAll();

        http
                .logout()
                .logoutSuccessUrl("/");

        http
                .headers().frameOptions().disable();
    }
}
