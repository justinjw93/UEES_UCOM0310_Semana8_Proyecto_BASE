package edu.uees.reservas.service;

public class ReservaNoEncontradaException extends RuntimeException {

    public ReservaNoEncontradaException(String id) {
        super("Reserva no encontrada: " + id);
    }
}
