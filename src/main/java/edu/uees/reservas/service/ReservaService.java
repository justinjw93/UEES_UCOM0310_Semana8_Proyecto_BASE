package edu.uees.reservas.service;

import edu.uees.reservas.domain.Reserva;
import edu.uees.reservas.repository.ReservaRepository;
import org.springframework.stereotype.Service;

@Service
public class ReservaService {

    static final int HORAS_MINIMAS_CANCELACION = 2;

    private final ReservaRepository repository;

    public ReservaService(ReservaRepository repository) {
        this.repository = repository;
    }

    public boolean puedeCancelar(int horasAnticipacion) {
        return horasAnticipacion >= HORAS_MINIMAS_CANCELACION;
    }

    public Reserva crear(String id, String tipo) {
        Reserva reserva = new Reserva(id, tipo);
        if (!repository.guardarSiNoExiste(reserva)) {
            throw new ReservaDuplicadaException(id);
        }
        return reserva;
    }

    public Reserva buscar(String id) {
        return repository.buscarPorId(id)
                .orElseThrow(() -> new ReservaNoEncontradaException(id));
    }

    public Reserva confirmar(String id) {
        Reserva reserva = buscar(id);
        reserva.confirmar();
        return repository.guardar(reserva);
    }
}
