package edu.uees.reservas.repository;

import edu.uees.reservas.domain.EstadoReserva;
import edu.uees.reservas.domain.Reserva;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ReservaRepositoryMemoriaTest {

    private final ReservaRepositoryMemoria repository = new ReservaRepositoryMemoria();

    @Test
    void guardarActualizaReservaExistente() {
        Reserva reserva = new Reserva("R-001", "NORMAL");
        repository.guardarSiNoExiste(reserva);
        reserva.confirmar();

        assertSame(reserva, repository.guardar(reserva));
        assertEquals(EstadoReserva.CONFIRMADA, repository.buscarPorId("R-001").orElseThrow().getEstado());
    }

    @Test
    void guardarSiNoExisteNoSobrescribe() {
        assertTrue(repository.guardarSiNoExiste(new Reserva("R-001", "NORMAL")));
        assertFalse(repository.guardarSiNoExiste(new Reserva("R-001", "VIP")));

        assertEquals("NORMAL", repository.buscarPorId("R-001").orElseThrow().getTipo());
    }

    @Test
    void creacionesSimultaneasConMismoIdSoloGuardanUna() throws Exception {
        int hilos = 20;
        ExecutorService pool = Executors.newFixedThreadPool(hilos);
        CountDownLatch salida = new CountDownLatch(1);
        AtomicInteger exitos = new AtomicInteger();
        try {
            Future<?>[] tareas = new Future<?>[hilos];
            for (int i = 0; i < hilos; i++) {
                tareas[i] = pool.submit(() -> {
                    salida.await();
                    if (repository.guardarSiNoExiste(new Reserva("R-900", "NORMAL"))) {
                        exitos.incrementAndGet();
                    }
                    return null;
                });
            }
            salida.countDown();
            for (Future<?> tarea : tareas) {
                tarea.get(5, TimeUnit.SECONDS);
            }
        } finally {
            pool.shutdownNow();
        }

        assertEquals(1, exitos.get());
    }
}
