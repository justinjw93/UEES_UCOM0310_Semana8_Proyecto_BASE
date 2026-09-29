package edu.uees.reservas.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReservaTest {

    @Test
    void nuevaReservaQuedaPendiente() {
        Reserva reserva = new Reserva("R-001", "VIP");

        assertEquals("VIP", reserva.getTipo());
        assertEquals(EstadoReserva.PENDIENTE, reserva.getEstado());
    }

    @Test
    void idNuloNoEsPermitido() {
        assertThrows(IllegalArgumentException.class, () -> new Reserva(null, "NORMAL"));
    }

    @Test
    void idVacioNoEsPermitido() {
        assertThrows(IllegalArgumentException.class, () -> new Reserva("   ", "NORMAL"));
    }

    @Test
    void tipoNuloSeAsumeNormal() {
        assertEquals("NORMAL", new Reserva("R-001", null).getTipo());
    }

    @Test
    void confirmarCambiaEstado() {
        Reserva reserva = new Reserva("R-001", "NORMAL");
        reserva.confirmar();
        assertEquals(EstadoReserva.CONFIRMADA, reserva.getEstado());
    }

    @Test
    void cancelarCambiaEstado() {
        Reserva reserva = new Reserva("R-001", "NORMAL");
        reserva.cancelar();
        assertEquals(EstadoReserva.CANCELADA, reserva.getEstado());
    }
}
