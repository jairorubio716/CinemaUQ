package co.edu.uniquindio.cinemauq;

import co.edu.uniquindio.cinemauq.model.Asiento;
import co.edu.uniquindio.cinemauq.model.CategoriaAsiento;
import co.edu.uniquindio.cinemauq.model.Clasificacion;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Compra;
import co.edu.uniquindio.cinemauq.model.CuentaPuntos;
import co.edu.uniquindio.cinemauq.model.Entrada;
import co.edu.uniquindio.cinemauq.model.EstadoCompra;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.EstadoSolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.EstadoTarjeta;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.PuntoAcumulado;
import co.edu.uniquindio.cinemauq.model.Redencion;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.SolicitudRecarga;
import co.edu.uniquindio.cinemauq.model.TarjetaVirtual;
import co.edu.uniquindio.cinemauq.model.TipoProducto;
import co.edu.uniquindio.cinemauq.service.CatalogoService;
import co.edu.uniquindio.cinemauq.service.ExportadorReporte;
import co.edu.uniquindio.cinemauq.service.Reporte;
import co.edu.uniquindio.cinemauq.service.ReporteService;
import co.edu.uniquindio.cinemauq.service.TipoReporte;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FaseSieteTest {

    @TempDir
    Path tempDir;

    private CatalogoService catalogo;
    private ReporteService reporteService;
    private LocalDate inicio;
    private LocalDate fin;

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

        reporteService = new ReporteService(catalogo);
        inicio = LocalDate.now().minusDays(5);
        fin = LocalDate.now().plusDays(5);
        cargarDatosDePrueba();
    }

    @Test
    void generaTodosLosReportesConLosDatosDelPeriodo() {
        Reporte ventas = reporteService.generar(TipoReporte.RPT_001, inicio, fin);
        assertEquals(1, ventas.filas().size());
        assertEquals("2", ventas.filas().get(0).get(2));
        assertTrue(ventas.textoPlano().contains("38.000"));
        assertTrue(ventas.textoPlano().contains("RPT-001"));

        Reporte entradas = reporteService.generar(TipoReporte.RPT_002, inicio, fin);
        assertEquals(1, entradas.filas().size());
        assertEquals("2", entradas.filas().get(0).get(2));
        assertTrue(entradas.textoPlano().contains("20.000"));

        Reporte ocupacion = reporteService.generar(TipoReporte.RPT_003, inicio, fin);
        assertEquals(1, ocupacion.filas().size());
        assertTrue(ocupacion.textoPlano().contains("50,0%"));

        Reporte confiteria = reporteService.generar(TipoReporte.RPT_004, inicio, fin);
        assertEquals(2, confiteria.filas().size());
        assertTrue(confiteria.textoPlano().contains("Crispetas"));

        Reporte recargas = reporteService.generar(TipoReporte.RPT_005, inicio, fin);
        assertEquals(1, recargas.filas().size());
        assertTrue(recargas.textoPlano().contains("50.000"));

        Reporte reembolsos = reporteService.generar(TipoReporte.RPT_006, inicio, fin);
        assertEquals(2, reembolsos.filas().size());
        assertTrue(reembolsos.textoPlano().contains("15.000"));
        assertTrue(reembolsos.textoPlano().contains("Reembolsos: 1"));
        assertTrue(reembolsos.textoPlano().contains("Cancelaciones: 1"));

        Reporte puntos = reporteService.generar(TipoReporte.RPT_007, inicio, fin);
        assertEquals(1, puntos.filas().size());
        assertTrue(puntos.textoPlano().contains("Redimidos en periodo: 40"));
    }

    @Test
    void exportaReportesEnTxtYPdf() throws Exception {
        Reporte reporte = reporteService.generar(TipoReporte.RPT_001, inicio, fin);

        Path txt = tempDir.resolve("ventas.txt");
        ExportadorReporte.exportarTxt(reporte, txt);
        assertTrue(Files.exists(txt));
        String contenido = Files.readString(txt);
        assertTrue(contenido.contains("CINEMA UQ"));
        assertTrue(contenido.contains("Ventas por periodo"));
        assertTrue(contenido.contains("Registros encontrados: 1"));

        Path pdf = tempDir.resolve("ventas.pdf");
        ExportadorReporte.exportarPdf(reporte, pdf);
        assertTrue(Files.exists(pdf));
        assertTrue(Files.size(pdf) > 500);
    }

    private void cargarDatosDePrueba() {
        Cliente cliente = new Cliente("Ana Perez", "ana@uq.edu.co", "3000000000",
                "ana", "1234", "CLI-1", LocalDate.now().minusDays(20));
        TarjetaVirtual tarjeta = new TarjetaVirtual("TV-1", 100000, EstadoTarjeta.ACTIVA);
        CuentaPuntos cuenta = new CuentaPuntos("CP-1");
        cuenta.agregarPunto(new PuntoAcumulado(1, 100,
                LocalDate.now().minusDays(2), LocalDate.now().plusDays(60)));
        cliente.setTarjetaVirtual(tarjeta);
        cliente.setCuentaPuntos(cuenta);
        catalogo.agregarCliente(cliente);

        Pelicula pelicula = new Pelicula("PEL-1", "Cine de prueba", "Drama", 100,
                Clasificacion.R, "Sinopsis", "", true);
        catalogo.agregarPelicula(pelicula);

        Sala sala = new Sala(1, 1, 2, 2, "2D");
        int idAsiento = 1;
        for (int fila = 0; fila < 2; fila++) {
            for (int columna = 1; columna <= 2; columna++) {
                sala.agregarAsiento(new Asiento(idAsiento++,
                        String.valueOf((char) ('A' + fila)), columna, columna,
                        CategoriaAsiento.GENERAL));
            }
        }
        catalogo.agregarSala(sala);

        Funcion funcion = new Funcion("FUN-1", LocalDateTime.now().plusHours(2),
                10000, EstadoFuncion.EN_VENTA, pelicula, sala);
        catalogo.agregarFuncion(funcion);

        Entrada entrada1 = new Entrada("ENT-1", "COD-1", 10000, funcion,
                sala.getAsientos().get(0));
        Entrada entrada2 = new Entrada("ENT-2", "COD-2", 10000, funcion,
                sala.getAsientos().get(1));
        funcion.agregarEntrada(entrada1);
        funcion.agregarEntrada(entrada2);

        Producto crispetas = new Producto("PRO-1", "Crispetas", 12000, TipoProducto.CRISPETAS);
        Producto gaseosa = new Producto("PRO-2", "Gaseosa", 8000, TipoProducto.BEBIDA);
        catalogo.agregarProducto(crispetas);
        catalogo.agregarProducto(gaseosa);

        Compra compra = new Compra("CMP-1", LocalDateTime.now().minusHours(1), cliente);
        compra.setEstado(EstadoCompra.PAGADA);
        compra.setDescuento(2000);
        compra.setTotal(38000);
        compra.agregarEntrada(entrada1);
        compra.agregarEntrada(entrada2);
        compra.agregarItem(crispetas);
        compra.agregarItem(gaseosa);
        cliente.agregarCompra(compra);
        catalogo.agregarCompra(compra);

        Compra reembolsada = new Compra("CMP-2", LocalDateTime.now().minusDays(1), cliente);
        reembolsada.setEstado(EstadoCompra.REEMBOLSADA);
        reembolsada.setTotal(15000);
        catalogo.agregarCompra(reembolsada);

        Compra cancelada = new Compra("CMP-3", LocalDateTime.now().minusDays(2), cliente);
        cancelada.setEstado(EstadoCompra.CANCELADA);
        cancelada.setTotal(11000);
        catalogo.agregarCompra(cancelada);

        SolicitudRecarga aprobada = new SolicitudRecarga("REC-1",
                LocalDateTime.now().minusHours(3), 50000,
                EstadoSolicitudRecarga.APROBADA, tarjeta);
        SolicitudRecarga pendiente = new SolicitudRecarga("REC-2",
                LocalDateTime.now().minusHours(2), 30000,
                EstadoSolicitudRecarga.PENDIENTE, tarjeta);
        catalogo.agregarSolicitud(aprobada);
        catalogo.agregarSolicitud(pendiente);
        cliente.agregarSolicitudRecarga(aprobada);
        cliente.agregarSolicitudRecarga(pendiente);

        catalogo.agregarRedencion(new Redencion("RED-1", "PREMIO-1",
                "Combo gratis", 40, LocalDateTime.now().minusHours(4), cliente.getIdCliente()));
    }
}
