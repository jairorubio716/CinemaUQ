package co.edu.uniquindio.cinemauq.seed;

import co.edu.uniquindio.cinemauq.model.Administrador;
import co.edu.uniquindio.cinemauq.model.Clasificacion;
import co.edu.uniquindio.cinemauq.model.Cliente;
import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.EstadoFuncion;
import co.edu.uniquindio.cinemauq.model.Funcion;
import co.edu.uniquindio.cinemauq.model.Movimiento;
import co.edu.uniquindio.cinemauq.model.Pelicula;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.PuntoAcumulado;
import co.edu.uniquindio.cinemauq.model.Sala;
import co.edu.uniquindio.cinemauq.model.TipoMovimiento;
import co.edu.uniquindio.cinemauq.model.TipoProducto;
import co.edu.uniquindio.cinemauq.model.TipoPromocion;
import co.edu.uniquindio.cinemauq.service.AutenticacionService;
import co.edu.uniquindio.cinemauq.service.CatalogoAdminService;
import co.edu.uniquindio.cinemauq.service.CatalogoService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Datos de demostración para que la aplicación tenga contenido desde el primer arranque.
 */
public final class SeedData {

    private SeedData() {
    }

    public static void inicializar(CatalogoService catalogo,
                                   CatalogoAdminService adminService,
                                   AutenticacionService autenticacionService) {
        boolean cambios = false;

        if (catalogo.getAdministradores().isEmpty()) {
            catalogo.agregarAdministrador(new Administrador(
                    "Ana María Restrepo", "admin@cinemauq.co", "300 123 4567",
                    "admin", "admin123", "ADM-001"));
            cambios = true;
        }

        if (catalogo.getClientes().isEmpty()) {
            Cliente demo = autenticacionService.registrarCliente(
                    "Cliente Demo", "demo@cinemauq.co", "300 987 6543", "demo", "demo123");
            demo.getTarjetaVirtual().setSaldo(180000);
            demo.getTarjetaVirtual().registrarMovimiento(new Movimiento(
                    catalogo.siguienteIdMovimiento(), LocalDateTime.now().minusDays(5),
                    TipoMovimiento.RECARGA, 180000, 180000));
            demo.getCuentaPuntos().agregarPunto(new PuntoAcumulado(
                    1, 650, LocalDate.now().minusMonths(2), LocalDate.now().plusMonths(10)));
            cambios = true;
        }

        Administrador admin = catalogo.getAdministradores().get(0);

        if (catalogo.getSalas().isEmpty()) {
            adminService.crearSala(admin, 1, 10, 12, "2D");
            adminService.crearSala(admin, 2, 8, 10, "3D");
            adminService.crearSala(admin, 3, 6, 8, "VIP");
            cambios = true;
        }

        if (catalogo.getPeliculas().isEmpty()) {
            catalogo.agregarPelicula(new Pelicula("PEL-001", "Horizonte Rojo", "Acción",
                    128, Clasificacion.PG_13,
                    "Un piloto de rescate debe atravesar una tormenta solar para salvar a su tripulación.",
                    "poster-1", true));
            catalogo.agregarPelicula(new Pelicula("PEL-002", "La Última Función", "Drama",
                    112, Clasificacion.PG,
                    "El último día de un cine de barrio se convierte en el reencuentro de toda una ciudad.",
                    "poster-2", true));
            catalogo.agregarPelicula(new Pelicula("PEL-003", "Neón Nocturno", "Ciencia ficción",
                    135, Clasificacion.PG_13,
                    "En una ciudad que nunca duerme, una detective holográfica investiga su propio origen.",
                    "poster-3", true));
            catalogo.agregarPelicula(new Pelicula("PEL-004", "Risa en Cadena", "Comedia",
                    98, Clasificacion.G,
                    "Una cadena de mensajes equivocados desata la boda más caótica del año.",
                    "poster-4", true));
            catalogo.agregarPelicula(new Pelicula("PEL-005", "Sombras de Medianoche", "Terror",
                    105, Clasificacion.R,
                    "Cada medianoche, la vieja estación de tren abre una puerta que no debería existir.",
                    "poster-5", true));
            catalogo.agregarPelicula(new Pelicula("PEL-006", "Planeta Azul", "Documental",
                    90, Clasificacion.G,
                    "Un viaje visual por los océanos y las criaturas que sostienen la vida en la Tierra.",
                    "poster-6", true));
            catalogo.agregarPelicula(new Pelicula("PEL-007", "El Viaje de Luna", "Animación",
                    95, Clasificacion.G,
                    "Una pequeña luciérnaga cruza el bosque para devolverle la noche a su aldea.",
                    "poster-7", false));
            catalogo.agregarPelicula(new Pelicula("PEL-008", "Código Fantasma", "Suspenso",
                    118, Clasificacion.PG_13,
                    "Un analista encuentra un mensaje oculto que anticipa cada crimen de la ciudad.",
                    "poster-8", false));
            cambios = true;
        }

        if (catalogo.getFunciones().isEmpty()) {
            List<Pelicula> cartelera = new ArrayList<>();
            for (Pelicula pelicula : catalogo.getPeliculas()) {
                if (pelicula.isEnCartelera()) {
                    cartelera.add(pelicula);
                }
            }
            List<Sala> salas = catalogo.getSalas();
            LocalTime[] horarios = {LocalTime.of(15, 0), LocalTime.of(19, 30)};
            int consecutivo = 1;
            for (int dia = 1; dia <= 4; dia++) {
                LocalDate fecha = LocalDate.now().plusDays(dia);
                for (int i = 0; i < cartelera.size(); i++) {
                    for (int h = 0; h < horarios.length; h++) {
                        Sala sala = salas.get((i + dia + h) % salas.size());
                        double precio = 18000 + (i % 3) * 2500;
                        Funcion funcion = new Funcion(
                                String.format("FUN-%03d", consecutivo++),
                                LocalDateTime.of(fecha, horarios[h]),
                                precio, EstadoFuncion.EN_VENTA, cartelera.get(i), sala);
                        catalogo.agregarFuncion(funcion);
                    }
                }
            }
            cambios = true;
        }

        if (catalogo.getProductos().isEmpty()) {
            Producto crispetasMedias = new Producto("PRO-001", "Crispetas medianas", 12000, TipoProducto.CRISPETAS);
            Producto crispetasGrandes = new Producto("PRO-002", "Crispetas grandes", 16000, TipoProducto.CRISPETAS);
            Producto gaseosa = new Producto("PRO-003", "Gaseosa 500 ml", 7000, TipoProducto.BEBIDA);
            Producto agua = new Producto("PRO-004", "Agua 600 ml", 5000, TipoProducto.BEBIDA);
            Producto chocolate = new Producto("PRO-005", "Chocolatina", 4500, TipoProducto.DULCE);
            Producto gomitas = new Producto("PRO-006", "Gomitas ácidas", 5500, TipoProducto.DULCE);
            Producto hotdog = new Producto("PRO-007", "Hotdog clásico", 11000, TipoProducto.HOTDOG);
            Producto nachos = new Producto("PRO-008", "Nachos con queso", 13000, TipoProducto.NACHOS);
            catalogo.agregarProducto(crispetasMedias);
            catalogo.agregarProducto(crispetasGrandes);
            catalogo.agregarProducto(gaseosa);
            catalogo.agregarProducto(agua);
            catalogo.agregarProducto(chocolate);
            catalogo.agregarProducto(gomitas);
            catalogo.agregarProducto(hotdog);
            catalogo.agregarProducto(nachos);

            Combo personal = new Combo("COM-001", "Combo personal", 0);
            personal.agregarItem(crispetasMedias);
            personal.agregarItem(gaseosa);
            catalogo.agregarCombo(personal);

            Combo pareja = new Combo("COM-002", "Combo pareja", 0);
            pareja.agregarItem(crispetasGrandes);
            pareja.agregarItem(gaseosa);
            pareja.agregarItem(gaseosa);
            pareja.agregarItem(chocolate);
            catalogo.agregarCombo(pareja);

            Combo familiar = new Combo("COM-003", "Combo familiar", 0);
            familiar.agregarItem(crispetasGrandes);
            familiar.agregarItem(crispetasGrandes);
            familiar.agregarItem(gaseosa);
            familiar.agregarItem(gaseosa);
            familiar.agregarItem(nachos);
            catalogo.agregarCombo(familiar);
            cambios = true;
        }

        if (catalogo.getPromociones().isEmpty()) {
            catalogo.agregarPromocion(new Promocion("PRM-001", "Martes de cine",
                    TipoPromocion.DESCUENTO_PORCENTAJE, 20,
                    LocalDate.now().minusDays(30), LocalDate.now().plusDays(60)));
            catalogo.agregarPromocion(new Promocion("PRM-002", "Noche de combos",
                    TipoPromocion.DESCUENTO_MONTO, 3000,
                    LocalDate.now().minusDays(10), LocalDate.now().plusDays(30)));
            cambios = true;
        }

        if (cambios) {
            catalogo.guardar();
        }
    }
}
