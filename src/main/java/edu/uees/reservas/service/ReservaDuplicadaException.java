package edu.uees.reservas.service;

public class ReservaDuplicadaException extends RuntimeException {

    public ReservaDuplicadaException(String id) {
        super("Ya existe una reserva con id: " + id);
    }
}
