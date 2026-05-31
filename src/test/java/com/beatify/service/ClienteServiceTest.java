package com.beatify.service;

import com.beatify.dao.ClienteDAO;
import com.beatify.exceptions.NotFoundException;
import com.beatify.exceptions.ValidacionException;
import com.beatify.model.Cliente;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Prueba la lógica de validación de ClienteService sin tocar la base de datos.
 * Usa un stub manual de ClienteDAO que no abre conexiones.
 */
class ClienteServiceTest {

    // ---- Stub de ClienteDAO ----
    private static class ClienteDAOStub extends ClienteDAO {
        private final List<Cliente> almacen = new ArrayList<>();

        @Override
        public Integer insertar(final Cliente cliente) {
            cliente.setIdCliente(almacen.size() + 1);
            almacen.add(cliente);
            return cliente.getIdCliente();
        }

        @Override
        public Cliente buscarPorCorreo(final String correo) {
            return almacen.stream()
                    .filter(c -> correo.equalsIgnoreCase(c.getCorreo()))
                    .findFirst()
                    .orElseThrow(() -> new NotFoundException("no encontrado"));
        }

        @Override
        public List<Cliente> listar() {
            return new ArrayList<>(almacen);
        }
    }

    private ClienteService service;
    private ClienteDAOStub stub;

    @BeforeEach
    void setUp() {
        stub = new ClienteDAOStub();
        service = new ClienteService(stub);
    }

    // -----------------------------------------------------------------
    // registrar — validaciones
    // -----------------------------------------------------------------

    @Test
    void registrarClienteValido() {
        final Cliente c = cliente("Ana", "García", "ana@mail.com", "Password1!");
        final Integer id = service.registrar(c);
        assertNotNull(id);
        assertTrue(id > 0);
    }

    @Test
    void registrarFallaSinNombre() {
        final Cliente c = cliente("", "García", "x@mail.com", "Password1!");
        assertThrows(ValidacionException.class, () -> service.registrar(c));
    }

    @Test
    void registrarFallaSinApellido() {
        final Cliente c = cliente("Ana", "", "x@mail.com", "Password1!");
        assertThrows(ValidacionException.class, () -> service.registrar(c));
    }

    @Test
    void registrarFallaSinCorreo() {
        final Cliente c = cliente("Ana", "García", "", "Password1!");
        assertThrows(ValidacionException.class, () -> service.registrar(c));
    }

    @Test
    void registrarFallaCorreoMalFormado() {
        final Cliente c = cliente("Ana", "García", "no-es-correo", "Password1!");
        assertThrows(ValidacionException.class, () -> service.registrar(c));
    }

    @Test
    void registrarFallaPasswordCorta() {
        final Cliente c = cliente("Ana", "García", "ana@mail.com", "abc");
        assertThrows(ValidacionException.class, () -> service.registrar(c));
    }

    @Test
    void registrarFallaCorreoDuplicado() {
        service.registrar(cliente("Ana", "García", "dup@mail.com", "Password1!"));
        final Cliente c2 = cliente("Luis", "Martínez", "dup@mail.com", "Password2!");
        assertThrows(ValidacionException.class, () -> service.registrar(c2));
    }

    @Test
    void registrarHasheaPassword() {
        final String pwd = "MiClave123!";
        final Cliente c = cliente("Ana", "García", "ana2@mail.com", pwd);
        service.registrar(c);
        // El hash no debe ser igual al texto plano
        assertNotEquals(pwd, c.getPasswordHash());
    }

    // -----------------------------------------------------------------
    // buscarPorId — validación de id
    // -----------------------------------------------------------------

    @Test
    void buscarPorIdNuloLanzaValidacion() {
        assertThrows(ValidacionException.class, () -> service.buscarPorId(null));
    }

    @Test
    void buscarPorIdNegativoLanzaValidacion() {
        assertThrows(ValidacionException.class, () -> service.buscarPorId(-1));
    }

    // -----------------------------------------------------------------
    // actualizar — validaciones
    // -----------------------------------------------------------------

    @Test
    void actualizarSinIdLanzaValidacion() {
        final Cliente c = cliente("Ana", "García", "a@b.com", "Pass1!");
        c.setIdCliente(null);
        assertThrows(ValidacionException.class, () -> service.actualizar(c));
    }

    // -----------------------------------------------------------------
    // Helper
    // -----------------------------------------------------------------
    private static Cliente cliente(final String nombre, final String apellido,
                                   final String correo, final String pwd) {
        final Cliente c = new Cliente();
        c.setNombre(nombre);
        c.setApellido(apellido);
        c.setCorreo(correo);
        c.setPasswordHash(pwd);
        c.setActivo(true);
        return c;
    }
}
