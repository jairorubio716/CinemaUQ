package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.CategoriaAsiento;
import co.edu.uniquindio.cinemauq.model.Clasificacion;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.CuentaPuntos;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.model.TipoProducto;
import co.edu.uniquindio.cinemauq.model.TipoPromocion;
import co.edu.uniquindio.cinemauq.patrones.decorator.ProductoConPromocion;
import co.edu.uniquindio.cinemauq.patrones.observer.NotificadorObservable;
import co.edu.uniquindio.cinemauq.patrones.strategy.ReembolsoCompleto;
import co.edu.uniquindio.cinemauq.patrones.strategy.ReembolsoParcial;
import co.edu.uniquindio.cinemauq.patrones.strategy.SinReembolso;
import co.edu.uniquindio.cinemauq.repository.DatosCinema;
import co.edu.uniquindio.cinemauq.repository.RepositorioDatos;
import co.edu.uniquindio.cinemauq.service.CatalogoService;
import co.edu.uniquindio.cinemauq.service.ReembolsoService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaseTresCuatroTest {

    @Test
    void persistenciaGuardaYCargaCompraConPolimorfismo(@TempDir Path tempDir) {
        Cliente cliente = new Cliente("Ana", "ana@uq.edu.co", "3001234567",
                "ana", "1234", "CLI-1", LocalDate.now());
        cliente.setTarjetaVirtual(new TarjetaVirtual("TV-CLI-1", 50000, EstadoTarjeta.ACTIVA));
        cliente.setCuentaPuntos(new CuentaPuntos("CP-CLI-1"));

        Pelicula pelicula = new Pelicula("P1", "Matrix", "Sci-Fi", 120,
                Clasificacion.R, "Sinopsis", "", true);
        Sala sala = new Sala(1, 1, 2, 2, "2D");
        Funcion funcion = new Funcion("F1", LocalDateTime.now().plusDays(3), 15000,
                EstadoFuncion.EN_VENTA, pelicula, sala);
        Entrada entrada = new Entrada("E1", "E-00001", 15000, funcion,
                new Asiento(1, "A", 1, 1, CategoriaAsiento.GENERAL));
        Producto producto = new Producto("PR1", "Crispetas", 12000, TipoProducto.CRISPETAS);
        Combo combo = new Combo("CB1", "Combo pareja", 0);
        combo.agregarItem(producto);
        Promocion promocion = new Promocion("PM1", "10%", TipoPromocion.DESCUENTO_PORCENTAJE,
                10, LocalDate.now().minusDays(1), LocalDate.now().plusDays(5));

        Compra compra = new Compra("C1", LocalDateTime.now(), cliente);
        compra.agregarEntrada(entrada);
        compra.agregarItem(new ProductoConPromocion(producto, promocion));
        compra.agregarItem(combo);
        compra.calcularTotal();
        compra.cambiarEstado(EstadoCompra.PAGADA);

        DatosCinema datos = new DatosCinema();
        datos.peliculas.add(pelicula);
        datos.salas.add(sala);
        datos.funciones.add(funcion);
        datos.clientes.add(cliente);
        datos.productos.add(producto);
        datos.combos.add(combo);
        datos.promociones.add(promocion);
        datos.compras.add(compra);

        RepositorioDatos repositorio = new RepositorioDatos(tempDir);
        repositorio.guardar(datos);
        DatosCinema recuperado = repositorio.cargar();

        assertEquals(1, recuperado.compras.size());
        Compra compraRecuperada = recuperado.compras.get(0);
        assertEquals(EstadoCompra.PAGADA, compraRecuperada.getEstado());
        assertEquals(2, compraRecuperada.getConfiteria().size());
        assertInstanceOf(ProductoConPromocion.class, compraRecuperada.getConfiteria().get(0));
        assertInstanceOf(Combo.class, compraRecuperada.getConfiteria().get(1));
        assertEquals(compra.getTotal(), compraRecuperada.getTotal(), 0.01);
        assertEquals(compra.getTotal(), 37800, 0.01);
        assertEquals("TV-CLI-1", compraRecuperada.getCliente().getTarjetaVirtual().getNroTarjeta());
    }

    @Test
    void estrategiasDeReembolsoSegunHoras() {
        ReembolsoService servicio = new ReembolsoService(
                CatalogoService.getInstance(), new NotificadorObservable());

        assertInstanceOf(ReembolsoCompleto.class, servicio.seleccionarEstrategia(48, false));
        assertInstanceOf(ReembolsoCompleto.class, servicio.seleccionarEstrategia(1, true));
        assertInstanceOf(ReembolsoParcial.class, servicio.seleccionarEstrategia(10, false));
        assertInstanceOf(SinReembolso.class, servicio.seleccionarEstrategia(1, false));

        assertEquals(800, servicio.seleccionarEstrategia(10, false).calcularReembolso(1000), 0.01);
        assertTrue(servicio.seleccionarEstrategia(1, false).calcularReembolso(1000) == 0);
    }
}
