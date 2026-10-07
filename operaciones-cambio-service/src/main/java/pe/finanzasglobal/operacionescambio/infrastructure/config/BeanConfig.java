package pe.finanzasglobal.operacionescambio.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pe.finanzasglobal.operacionescambio.application.port.in.AnularOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.in.ConsultarOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.in.RegistrarOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.out.ClientePort;
import pe.finanzasglobal.operacionescambio.application.port.out.EventPublisherPort;
import pe.finanzasglobal.operacionescambio.application.port.out.OperacionRepositoryPort;
import pe.finanzasglobal.operacionescambio.application.port.out.TipoCambioPort;
import pe.finanzasglobal.operacionescambio.application.service.AnularOperacionService;
import pe.finanzasglobal.operacionescambio.application.service.ConsultarOperacionService;
import pe.finanzasglobal.operacionescambio.application.service.RegistrarOperacionService;

/** Composición: los casos de uso no llevan anotaciones de Spring; se cablean aquí con sus puertos. */
@Configuration
public class BeanConfig {

    @Bean
    RegistrarOperacionUseCase registrarOperacionUseCase(OperacionRepositoryPort repo, TipoCambioPort tc,
                                                        ClientePort clientes, EventPublisherPort eventos) {
        return new RegistrarOperacionService(repo, tc, clientes, eventos);
    }

    @Bean
    ConsultarOperacionUseCase consultarOperacionUseCase(OperacionRepositoryPort repo) {
        return new ConsultarOperacionService(repo);
    }

    @Bean
    AnularOperacionUseCase anularOperacionUseCase(OperacionRepositoryPort repo) {
        return new AnularOperacionService(repo);
    }
}
