package com.beatify.service;

import com.beatify.dao.BusquedaDAO;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Busqueda;

import java.util.List;

public class BusquedaService implements IBusquedaService {

    private final BusquedaDAO busquedaDAO;

    public BusquedaService(BusquedaDAO busquedaDAO) {
        this.busquedaDAO = busquedaDAO;
    }

    private void validar(Busqueda busqueda) {
        if (busqueda == null) {
            throw new ValidacionException("La búsqueda no puede ser nula");
        }
        if (busqueda.getTerminoBusqueda() == null || busqueda.getTerminoBusqueda().isBlank()) {
            throw new ValidacionException("El término de búsqueda es obligatorio");
        }
        if (busqueda.getTerminoBusqueda().length() > 255) {
            throw new ValidacionException("El término de búsqueda no puede superar los 255 caracteres");
        }
        if (busqueda.getIdCliente() == null || busqueda.getIdCliente() <= 0) {
            throw new ValidacionException("El id del cliente es obligatorio para la búsqueda");
        }
        if (busqueda.getResultadosObtenidos() != null && busqueda.getResultadosObtenidos() < 0) {
            throw new ValidacionException("El número de resultados no puede ser negativo");
        }
    }

    public Integer registrar(Busqueda busqueda) {
        validar(busqueda);
        return busquedaDAO.insertar(busqueda);
    }

    public List<Busqueda> listar() {
        return busquedaDAO.listar();
    }

    public Busqueda buscarPorId(Integer idBusqueda) {
        if (idBusqueda == null || idBusqueda <= 0) {
            throw new ValidacionException("El id de la búsqueda debe ser un entero positivo");
        }
        return busquedaDAO.buscarPorId(idBusqueda);
    }

    public void actualizar(Busqueda busqueda) {
        if (busqueda.getIdBusqueda() == null || busqueda.getIdBusqueda() <= 0) {
            throw new ValidacionException("El id de la búsqueda es obligatorio para actualizar");
        }
        validar(busqueda);
        busquedaDAO.actualizar(busqueda);
    }

    public void eliminar(Integer idBusqueda) {
        if (idBusqueda == null || idBusqueda <= 0) {
            throw new ValidacionException("El id de la búsqueda debe ser un entero positivo");
        }
        busquedaDAO.eliminar(idBusqueda);
    }
}
