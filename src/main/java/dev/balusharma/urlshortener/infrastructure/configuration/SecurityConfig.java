
package dev.balusharma.urlshortener.infrastructure.configuration;

import dev.balusharma.urlshortener.infrastructure.security.OAuth2SuccessHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    @Value("${app.oauth2.callback-uri}")
    private String oauth2CallbackUri;

    public SecurityConfig(OAuth2SuccessHandler oAuth2SuccessHandler) {
        this.oAuth2SuccessHandler = oAuth2SuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/{shortUrl}").permitAll()
                        .anyRequest().authenticated()
                )
                // Google OAuth2 login
                .oauth2Login(oauth2 -> oauth2
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri(oauth2CallbackUri)
                        )
                        .successHandler(oAuth2SuccessHandler)
                )
                // Validate our application's JWT Bearer tokens
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(Customizer.withDefaults())
                );

        return http.build();
    }
}
