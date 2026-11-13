package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.CategoriaAsiento;
import co.edu.uniquindio.cinemauq.model.Clasificacion;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.CuentaPuntos;
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
import co.edu.uniquindio.cinemauq.patrones.factory.FabricaEntrada;
import co.edu.uniquindio.cinemauq.patrones.observer.NotificadorObservable;
import co.edu.uniquindio.cinemauq.patrones.strategy.PuntosPorValor;
import co.edu.uniquindio.cinemauq.service.CatalogoService;
import co.edu.uniquindio.cinemauq.service.CompraService;
import co.edu.uniquindio.cinemauq.service.CotizacionCompra;
import co.edu.uniquindio.cinemauq.service.PuntoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FaseSeisTest {

    private CatalogoService catalogo;
    private CompraService compraService;
    private PuntoService puntoService;

    @BeforeEach
    void setUp() {
        catalogo = CatalogoService.getInstance();
        catalogo.setPeliculas(new ArrayList<>());
        catalogo.setSalas(new ArrayList<>());
        catalogo.setFunciones(new ArrayList<>());
        catalogo.setClientes(new ArrayList<>());
        catalogo.setAdministradores(new ArrayList<>());
        catalogo.setProductos(new ArrayList<>());
        catalogo.setCombos(new ArrayList<>());
        catalogo.setPromociones(new ArrayList<>());
        catalogo.setCompras(new ArrayList<>());
        catalogo.setSolicitudesRecarga(new ArrayList<>());
        catalogo.setRedenciones(new ArrayList<>());
        catalogo.setSecuenciaMovimiento(1);

        puntoService = new PuntoService(new PuntosPorValor(1000));
        compraService = new CompraService(catalogo, new FabricaEntrada(),
                puntoService, new NotificadorObservable());
    }

    @Test
    void cotizacionAplicaPromocionSoloAConfiteria() {
        Funcion funcion = crearFuncion(10000);
        Producto producto = new Producto("PRO-1", "Crispetas", 12000, TipoProducto.CRISPETAS);
        catalogo.agregarProducto(producto);
        Promocion promo = crearPromocion("PRM-1", TipoPromocion.DESCUENTO_PORCENTAJE, 20);

        CotizacionCompra cotizacion = compraService.cotizar(
                funcion, List.of(funcion.getSala().getAsientos().get(0)),
                List.of(producto), List.of(promo));

        assertEquals(22000, cotizacion.subtotal(), 0.01);
        assertEquals(2400, cotizacion.descuento(), 0.01);
        assertEquals(19600, cotizacion.total(), 0.01);
        assertEquals(9600, cotizacion.confiteria().get(0).precioUnitarioFinal(), 0.01);
    }

    @Test
    void compraCobraElMismoTotalQueLaCotizacion() {
        Funcion funcion = crearFuncion(10000);
        Producto producto = new Producto("PRO-2", "Gaseosa", 7000, TipoProducto.BEBIDA);
        catalogo.agregarProducto(producto);
        Promocion promo = crearPromocion("PRM-2", TipoPromocion.DESCUENTO_MONTO, 3000);
        Cliente cliente = crearCliente(50000);

        CotizacionCompra cotizacion = compraService.cotizar(
                funcion, List.of(funcion.getSala().getAsientos().get(0)),
                List.of(producto), List.of(promo));
        Compra compra = compraService.comprar(cliente, funcion,
                List.of(funcion.getSala().getAsientos().get(0)),
                List.of(producto), List.of(promo));

        assertEquals(cotizacion.total(), compra.getTotal(), 0.01);
        assertEquals(14000, compra.getTotal(), 0.01);
        assertEquals(36000, cliente.getTarjetaVirtual().getSaldo(), 0.01);
        assertEquals(4000, compra.getConfiteria().get(0).obtenerPrecio(), 0.01);
    }

    @Test
    void noAcumulaDosPromocionesSobreElMismoItem() {
        Funcion funcion = crearFuncion(10000);
        Producto producto = new Producto("PRO-3", "Combo personal", 12000, TipoProducto.CRISPETAS);
        catalogo.agregarProducto(producto);
        Promocion porcentaje = crearPromocion("PRM-3", TipoPromocion.DESCUENTO_PORCENTAJE, 20);
        Promocion monto = crearPromocion("PRM-4", TipoPromocion.DESCUENTO_MONTO, 3000);

        CotizacionCompra cotizacion = compraService.cotizar(
                funcion, List.of(funcion.getSala().getAsientos().get(0)),
                List.of(producto), List.of(porcentaje, monto));

        assertEquals(9600, cotizacion.confiteria().get(0).precioUnitarioFinal(), 0.01);
        assertEquals(2400, cotizacion.descuento(), 0.01);
        assertEquals(19600, cotizacion.total(), 0.01);
    }

    @Test
    void cotizacionAplicaDosPorUno() {
        Funcion funcion = crearFuncion(10000);
        Producto producto = new Producto("PRO-5", "Nachos", 13000, TipoProducto.NACHOS);
        catalogo.agregarProducto(producto);
        Promocion promo = crearPromocion("PRM-5", TipoPromocion.DOS_POR_UNO, 0);

        CotizacionCompra cotizacion = compraService.cotizar(
                funcion, List.of(funcion.getSala().getAsientos().get(0)),
                List.of(producto), List.of(promo));

        assertEquals(16500, cotizacion.total(), 0.01);
        assertEquals(6500, cotizacion.descuento(), 0.01);
        assertEquals(6500, cotizacion.confiteria().get(0).precioUnitarioFinal(), 0.01);
    }

    @Test
    void calculaPuntosConElTotalReal() {
        assertEquals(19, puntoService.calcularPuntos(19600));
    }

    private Promocion crearPromocion(String id, TipoPromocion tipo, double valor) {
        Promocion promocion = new Promocion(id, "Promocion " + id, tipo, valor,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(1));
        catalogo.agregarPromocion(promocion);
        return promocion;
    }

    private Cliente crearCliente(double saldo) {
        Cliente cliente = new Cliente("Cliente", "cliente@uq.edu.co", "3000000000",
                "cliente", "1234", "CLI-1", LocalDate.now());
        cliente.setTarjetaVirtual(new TarjetaVirtual("TV-1", saldo, EstadoTarjeta.ACTIVA));
        cliente.setCuentaPuntos(new CuentaPuntos("CP-1"));
        catalogo.agregarCliente(cliente);
        return cliente;
    }

    private Funcion crearFuncion(double precio) {
        Pelicula pelicula = new Pelicula("PEL-1", "Pelicula de prueba", "Accion", 110,
                Clasificacion.R, "Sinopsis", "", true);
        catalogo.agregarPelicula(pelicula);
        Sala sala = new Sala(1, 1, 2, 2, "2D");
        int id = 1;
        for (int fila = 0; fila < 2; fila++) {
            for (int columna = 1; columna <= 2; columna++) {
                sala.agregarAsiento(new Asiento(id++, String.valueOf((char) ('A' + fila)),
                        columna, columna, CategoriaAsiento.GENERAL));
            }
        }
        catalogo.agregarSala(sala);
        Funcion funcion = new Funcion("FUN-1", LocalDateTime.now().plusDays(3),
                precio, EstadoFuncion.EN_VENTA, pelicula, sala);
        catalogo.agregarFuncion(funcion);
        return funcion;
    }
}
