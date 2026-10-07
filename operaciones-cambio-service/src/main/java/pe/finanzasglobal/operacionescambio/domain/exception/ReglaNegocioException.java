package pe.finanzasglobal.operacionescambio.domain.exception;

/** Se lanza cuando se viola una regla de negocio del dominio. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String message) {
        super(message);
    }
}
