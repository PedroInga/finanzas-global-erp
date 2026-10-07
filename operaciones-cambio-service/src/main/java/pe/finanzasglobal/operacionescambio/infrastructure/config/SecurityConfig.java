package pe.finanzasglobal.operacionescambio.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Seguridad del microservicio (autenticación, autorización por rol y por endpoint).
 * Para la demostración usa HTTP Basic con usuarios en memoria; en el ERP real el servicio es un
 * Resource Server que valida el JWT emitido por el servicio de seguridad (spring-boot-starter-oauth2-resource-server).
 * La autorización por rol (@PreAuthorize en el controlador) es la misma en ambos casos.
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(a -> a
                        .requestMatchers(HttpMethod.GET, "/actuator/health").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /** Usuarios de DEMOSTRACIÓN (en producción provienen del servicio de seguridad, nunca del código). */
    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername("cajero").password(encoder.encode("cajero123")).roles("CAJERO").build(),
                User.withUsername("supervisor").password(encoder.encode("super123")).roles("SUPERVISOR").build(),
                User.withUsername("auditor").password(encoder.encode("audit123")).roles("AUDITOR").build());
    }
}
