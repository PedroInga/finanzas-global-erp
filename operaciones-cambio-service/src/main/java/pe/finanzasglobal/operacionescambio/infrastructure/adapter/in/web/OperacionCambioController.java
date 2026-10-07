package pe.finanzasglobal.operacionescambio.infrastructure.adapter.in.web;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import pe.finanzasglobal.operacionescambio.application.port.in.AnularOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.in.ConsultarOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.in.RegistrarOperacionUseCase;
import pe.finanzasglobal.operacionescambio.application.port.in.RegistrarOperacionUseCase.RegistrarOperacionCommand;
import pe.finanzasglobal.operacionescambio.domain.model.OperacionCambio;
import pe.finanzasglobal.operacionescambio.infrastructure.adapter.in.web.dto.OperacionResponse;
import pe.finanzasglobal.operacionescambio.infrastructure.adapter.in.web.dto.RegistrarOperacionRequest;

import java.net.URI;
import java.util.UUID;

/**
 * Adaptador de entrada REST. Solo traduce HTTP <-> casos de uso y aplica la autorización por rol.
 * La identidad del usuario proviene del token (no del cuerpo del request) para garantizar trazabilidad.
 */
@RestController
@RequestMapping("/api/v1/operaciones")
public class OperacionCambioController {

    private final RegistrarOperacionUseCase registrar;
    private final ConsultarOperacionUseCase consultar;
    private final AnularOperacionUseCase anular;

    public OperacionCambioController(RegistrarOperacionUseCase registrar, ConsultarOperacionUseCase consultar,
                                     AnularOperacionUseCase anular) {
        this.registrar = registrar;
        this.consultar = consultar;
        this.anular = anular;
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('CAJERO','SUPERVISOR')")
    public ResponseEntity<OperacionResponse> registrar(@Valid @RequestBody RegistrarOperacionRequest request,
                                                       Authentication auth,
                                                       @RequestHeader(value = "X-Office", defaultValue = "PRINCIPAL")
                                                       String oficina) {
        OperacionCambio op = registrar.registrar(new RegistrarOperacionCommand(request.clienteId(), request.tipo(),
                request.moneda(), request.monto(), auth.getName(), oficina));
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").buildAndExpand(op.getId())
                .toUri();
        return ResponseEntity.created(location).body(OperacionResponse.desde(op));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CAJERO','SUPERVISOR','AUDITOR')")
    public OperacionResponse obtener(@PathVariable UUID id) {
        return OperacionResponse.desde(consultar.porId(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SUPERVISOR')")
    public OperacionResponse anular(@PathVariable UUID id, Authentication auth) {
        return OperacionResponse.desde(anular.anular(id, auth.getName()));
    }
}
