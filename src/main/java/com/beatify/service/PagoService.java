package com.beatify.service;

import com.beatify.dao.PagoDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Pago;

import java.util.List;
import java.util.Set;

public class PagoService implements IPagoService {

    // Deben coincidir con los CHECK de PAGO en 01_schema_beatify.sql
    // (PAGO_METODO_PAGO_CK y PAGO_ESTADO_PAGO_CK).
    private static final Set<String> METODOS_VALIDOS =
            Set.of("DAVIPLATA", "NEQUI", "PAYPAL", "PSE", "TARJETA");
    private static final Set<String> ESTADOS_VALIDOS =
            Set.of("EXITOSO", "FALLIDO", "PENDIENTE", "REEMBOLSADO");

    private final PagoDAO pagoDAO;

    public PagoService(PagoDAO pagoDAO) {
        this.pagoDAO = pagoDAO;
    }



    private void validar(Pago pago) {
        if (pago == null) {
            throw new ValidacionException("El pago no puede ser nulo");
        }
        if (pago.getMonto() == null || pago.getMonto() <= 0) {
            throw new ValidacionException("El monto del pago debe ser mayor a 0");
        }
        if (pago.getFechaPago() == null) {
            throw new ValidacionException("La fecha del pago es obligatoria");
        }
        if (pago.getMetodoPago() == null || pago.getMetodoPago().isBlank()) {
            throw new ValidacionException("El método de pago es obligatorio");
        }
        if (!METODOS_VALIDOS.contains(pago.getMetodoPago().toUpperCase())) {
            throw new ValidacionException("El método de pago debe ser uno de: " + METODOS_VALIDOS);
        }
        if (pago.getEstadoPago() == null || pago.getEstadoPago().isBlank()) {
            throw new ValidacionException("El estado del pago es obligatorio");
        }
        if (!ESTADOS_VALIDOS.contains(pago.getEstadoPago().toUpperCase())) {
            throw new ValidacionException("El estado del pago debe ser uno de: " + ESTADOS_VALIDOS);
        }
        if (pago.getIdSuscripcion() == null || pago.getIdSuscripcion() <= 0) {
            throw new ValidacionException("El id de la suscripción es obligatorio para el pago");
        }
    }



    public Integer registrar(Pago pago) {
        validar(pago);
        return pagoDAO.insertar(pago);
    }

    public List<Pago> listar() {
        return pagoDAO.listar();
    }

    public Pago buscarPorId(Integer idPago) {
        if (idPago == null || idPago <= 0) {
            throw new ValidacionException("El id del pago debe ser un entero positivo");
        }
        return pagoDAO.buscarPorId(idPago);
    }

    public void actualizar(Pago pago) {
        if (pago.getIdPago() == null || pago.getIdPago() <= 0) {
            throw new ValidacionException("El id del pago es obligatorio para actualizar");
        }
        validar(pago);
        pagoDAO.actualizar(pago);
    }

    public void eliminar(Integer idPago) {
        if (idPago == null || idPago <= 0) {
            throw new ValidacionException("El id del pago debe ser un entero positivo");
        }
        pagoDAO.eliminar(idPago);
    }
}
