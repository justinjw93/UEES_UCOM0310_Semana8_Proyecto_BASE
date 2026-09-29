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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ReservaServiceTest {

    private final ReservaRepository repository = mock(ReservaRepository.class);
    private final ReservaService service = new ReservaService(repository);

    @Test
    void dosHorasPermitenCancelar() {
        assertTrue(service.puedeCancelar(ReservaService.HORAS_MINIMAS_CANCELACION));
    }

    @Test
    void unaHoraNoPermiteCancelar() {
        assertFalse(service.puedeCancelar(ReservaService.HORAS_MINIMAS_CANCELACION - 1));
    }

    @Test
    void ceroHorasNoPermiteCancelar() {
        assertFalse(service.puedeCancelar(0));
    }

    @Test
    void masDeDosHorasPermitenCancelar() {
        assertTrue(service.puedeCancelar(3));
    }

    @Test
    void crearGuardaReservaPendiente() {
        when(repository.guardarSiNoExiste(any(Reserva.class))).thenReturn(true);

        Reserva reserva = service.crear("R-001", "NORMAL");

        assertEquals("R-001", reserva.getId());
        assertEquals("NORMAL", reserva.getTipo());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    @Test
    void crearConIdExistenteLanzaExcepcionYNoSobrescribe() {
        when(repository.guardarSiNoExiste(any(Reserva.class))).thenReturn(false);

        assertThrows(ReservaDuplicadaException.class, () -> service.crear("R-001", "VIP"));
        verify(repository, never()).guardar(any(Reserva.class));
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

        assertThrows(ReservaNoEncontradaException.class, () -> service.buscar("NO-EXISTE"));
    }

    @Test
    void confirmarCambiaEstadoYGuarda() {
        Reserva existente = new Reserva("R-001", "NORMAL");
        when(repository.buscarPorId("R-001")).thenReturn(Optional.of(existente));
        when(repository.guardar(existente)).thenReturn(existente);

        assertEquals(EstadoReserva.CONFIRMADA, service.confirmar("R-001").getEstado());
    }
}
