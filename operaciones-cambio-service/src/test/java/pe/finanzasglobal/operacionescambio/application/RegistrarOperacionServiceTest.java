package pe.finanzasglobal.operacionescambio.application;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pe.finanzasglobal.operacionescambio.application.exception.RecursoNoEncontradoException;
import pe.finanzasglobal.operacionescambio.application.port.in.RegistrarOperacionUseCase.RegistrarOperacionCommand;
import pe.finanzasglobal.operacionescambio.application.port.out.ClientePort;
import pe.finanzasglobal.operacionescambio.application.port.out.ClientePort.ClienteInfo;
import pe.finanzasglobal.operacionescambio.application.port.out.EventPublisherPort;
import pe.finanzasglobal.operacionescambio.application.port.out.OperacionRepositoryPort;
import pe.finanzasglobal.operacionescambio.application.port.out.TipoCambioPort;
import pe.finanzasglobal.operacionescambio.application.service.AnularOperacionService;
import pe.finanzasglobal.operacionescambio.application.service.ConsultarOperacionService;
import pe.finanzasglobal.operacionescambio.application.service.RegistrarOperacionService;
import pe.finanzasglobal.operacionescambio.domain.event.OperacionRegistradaEvent;
import pe.finanzasglobal.operacionescambio.domain.exception.ReglaNegocioException;
import pe.finanzasglobal.operacionescambio.domain.model.*;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/** Prueba de los casos de uso con puertos simulados: demuestra el aislamiento del núcleo respecto a la tecnología. */
@ExtendWith(MockitoExtension.class)
class RegistrarOperacionServiceTest {

    @Mock OperacionRepositoryPort repositorio;
    @Mock TipoCambioPort tipoCambioPort;
    @Mock ClientePort clientePort;
    @Mock EventPublisherPort eventos;

    RegistrarOperacionService servicio;

    final RegistrarOperacionCommand cmd = new RegistrarOperacionCommand(1L, TipoOperacion.COMPRA, Moneda.USD,
            new BigDecimal("500"), "cajero", "SAN ISIDRO");

    @BeforeEach
    void setUp() {
        servicio = new RegistrarOperacionService(repositorio, tipoCambioPort, clientePort, eventos);
    }

    @Test
    void registraGuardaYPublicaEvento() {
        when(clientePort.buscar(1L)).thenReturn(Optional.of(new ClienteInfo(1L, true, new BigDecimal("10000"))));
        when(tipoCambioPort.vigente(Moneda.USD)).thenReturn(Optional.of(
                new TipoCambio(Moneda.USD, new BigDecimal("3.70"), new BigDecimal("3.75"))));
        when(repositorio.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        OperacionCambio resultado = servicio.registrar(cmd);

        assertThat(resultado.getImporteBruto()).isEqualByComparingTo("1850.00");
        verify(repositorio).guardar(any(OperacionCambio.class));
        ArgumentCaptor<OperacionRegistradaEvent> captor = ArgumentCaptor.forClass(OperacionRegistradaEvent.class);
        verify(eventos).publicar(captor.capture());
        assertThat(captor.getValue().oficina()).isEqualTo("SAN ISIDRO");
        assertThat(captor.getValue().operacionId()).isEqualTo(resultado.getId());
    }

    @Test
    void clienteInexistenteNoGuardaNiPublica() {
        when(clientePort.buscar(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.registrar(cmd)).isInstanceOf(RecursoNoEncontradoException.class);
        verifyNoInteractions(repositorio, eventos);
    }

    @Test
    void clienteInactivoSeRechaza() {
        when(clientePort.buscar(1L)).thenReturn(Optional.of(new ClienteInfo(1L, false, BigDecimal.TEN)));

        assertThatThrownBy(() -> servicio.registrar(cmd)).isInstanceOf(ReglaNegocioException.class);
        verifyNoInteractions(repositorio, eventos);
    }

    @Test
    void sinTipoCambioVigenteSeRechaza() {
        when(clientePort.buscar(1L)).thenReturn(Optional.of(new ClienteInfo(1L, true, BigDecimal.TEN)));
        when(tipoCambioPort.vigente(Moneda.USD)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.registrar(cmd)).isInstanceOf(RecursoNoEncontradoException.class);
        verifyNoInteractions(repositorio, eventos);
    }

    @Test
    void consultarYAnular() {
        UUID id = UUID.randomUUID();
        OperacionCambio op = OperacionCambio.registrar(1L, TipoOperacion.VENTA, Moneda.EUR, BigDecimal.TEN,
                new TipoCambio(Moneda.EUR, new BigDecimal("4.0"), new BigDecimal("4.1")), null, "c", "o");
        when(repositorio.buscarPorId(id)).thenReturn(Optional.of(op));
        when(repositorio.guardar(any())).thenAnswer(inv -> inv.getArgument(0));

        assertThat(new ConsultarOperacionService(repositorio).porId(id)).isSameAs(op);
        assertThat(new AnularOperacionService(repositorio).anular(id, "supervisor").getEstado())
                .isEqualTo(EstadoOperacion.ANULADA);
    }

    @Test
    void consultarYAnularInexistente() {
        UUID id = UUID.randomUUID();
        when(repositorio.buscarPorId(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new ConsultarOperacionService(repositorio).porId(id))
                .isInstanceOf(RecursoNoEncontradoException.class);
        assertThatThrownBy(() -> new AnularOperacionService(repositorio).anular(id, "s"))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
