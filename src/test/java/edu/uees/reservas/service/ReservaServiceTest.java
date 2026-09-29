package edu.uees.reservas.service;

import edu.uees.reservas.domain.EstadoReserva;
import edu.uees.reservas.domain.Reserva;
import edu.uees.reservas.repository.ReservaRepository;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ReservaServiceTest {

    private final ReservaRepository repository = mock(ReservaRepository.class);
    private final ReservaService service = new ReservaService(repository);

    @Test
    void dosHorasPermitenCancelar() {
        assertTrue(service.puedeCancelar(2));
    }

    @Test
    void unaHoraNoPermiteCancelar() {
        assertFalse(service.puedeCancelar(1));
    }

    @Test
    void crearGuardaReservaPendiente() {
        when(repository.guardar(any(Reserva.class))).thenAnswer(inv -> inv.getArgument(0));

        Reserva reserva = service.crear("R-001", "NORMAL");

        assertEquals("R-001", reserva.getId());
        assertEquals("NORMAL", reserva.getTipo());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    @Test
    void buscarDevuelveReservaExistente() {
        Reserva existente = new Reserva("R-001", "NORMAL");
        when(repository.buscarPorId("R-001")).thenReturn(Optional.of(existente));

        assertSame(existente, service.buscar("R-001"));
    }

    @Test
    void buscarReservaInexistenteLanzaExcepcion() {
        when(repository.buscarPorId("NO-EXISTE")).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> service.buscar("NO-EXISTE"));
    }
}
