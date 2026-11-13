# CinemaUQ

Sistema integral de gestión de cine desarrollado como proyecto final de **Programación II** en la Universidad del Quindío.

CinemaUQ permite a los clientes consultar la cartelera, seleccionar funciones y asientos, comprar entradas y confitería, pagar con una tarjeta virtual, acumular y redimir puntos, y cancelar compras según las reglas de negocio. El administrador gestiona el catálogo, las funciones, las recargas, los clientes, las compras y los reportes de operación.

## Tecnologías

- Java 21
- JavaFX 21 (FXML + CSS)
- Maven Wrapper
- Gson para persistencia en JSON
- OpenPDF para exportación de reportes
- JUnit 5 para pruebas de servicios y reportes

## Requisitos

- JDK 21 instalado y configurado en `JAVA_HOME`.
- Windows, Linux o macOS con soporte para JavaFX.
- No es necesario instalar Maven: el proyecto incluye `mvnw` y `mvnw.cmd`.

## Ejecución

En Windows:

```powershell
.\mvnw.cmd clean javafx:run
```

En Linux o macOS:

```bash
./mvnw clean javafx:run
```

La aplicación crea y actualiza la base de datos local en `datos/cinemauq.json`. Si el archivo no existe, se cargan datos de ejemplo automáticamente.

## Usuarios de prueba

| Rol | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `admin123` |
| Cliente | `demo` | `demo123` |

El cliente `demo` inicia con saldo disponible en su tarjeta virtual para probar el flujo de compra.

## Funcionalidades del cliente

- Registro e inicio de sesión.
- Consulta de cartelera, películas y funciones disponibles.
- Selección gráfica de asientos.
- Compra de entradas y productos o combos de confitería.
- Cotización con promociones vigentes.
- Pago con tarjeta virtual y validación de saldo.
- Consulta de saldo y movimientos de la tarjeta.
- Solicitud de recargas.
- Historial de compras y cancelación según el tiempo restante.
- Acumulación, consulta y redención de puntos con política FEFO.

## Funcionalidades del administrador

- Dashboard con ingresos, entradas vendidas, funciones activas, clientes y recargas pendientes.
- Gestión de películas, salas, productos, combos y promociones.
- Gestión de funciones, activación de venta, cancelación con reembolso y eliminación.
- Aprobación y rechazo de solicitudes de recarga.
- Consulta de clientes registrados e historial completo de compras.
- Generación de reportes RPT-001 a RPT-007 con tablas, métricas y gráficos.
- Exportación de reportes a TXT y PDF.

## Reportes

| Código | Reporte |
| --- | --- |
| RPT-001 | Ventas por periodo |
| RPT-002 | Entradas vendidas por película o función |
| RPT-003 | Ocupación de las salas |
| RPT-004 | Productos y combos vendidos |
| RPT-005 | Recargas realizadas |
| RPT-006 | Cancelaciones y reembolsos |
| RPT-007 | Puntos acumulados y redimidos por cliente |

Los reportes incluyen periodo consultado, fecha de generación, métricas, registros y totales. La vista administrativa permite exportarlos con un selector de archivos.

## Arquitectura

El proyecto sigue una separación MVC:

- `model`: entidades y enumeraciones del dominio.
- `service`: reglas de negocio y servicios de aplicación.
- `repository`: persistencia con Gson.
- `controller`: controladores de las vistas JavaFX.
- `view`: archivos FXML.
- `app`: contexto de aplicación, navegación, sesión y utilidades de interfaz.
- `patrones`: implementaciones de patrones de diseño.
- `excepciones`: excepciones propias del dominio.

Flujo general:

```text
Vista FXML -> Controlador -> CinemaFacade -> Servicios -> Repositorio JSON
```

## Patrones de diseño

- Factory Method: `IFabricaEntrada` / `FabricaEntrada`.
- Builder: `CompraBuilder`.
- Singleton: `CatalogoService`.
- Composite: `IItemVendible` con `Producto` y `Combo`.
- Adapter: `PagoAdapter` sobre `PasarelaExterna`.
- Decorator: `ProductoConPromocion`.
- Facade: `CinemaFacade`.
- Strategy: `IReembolso` e `IStrategyPuntos`.
- State: `IEstadoCompra` y sus estados concretos.
- Observer: `NotificadorObservable` e `INotificable`.

## Estructura del repositorio

```text
CinemaUQ/
|-- documento/                  Documento final y diagrama del proyecto
|-- src/main/java/              Código fuente Java
|-- src/main/resources/         FXML, CSS y recursos
|-- src/test/java/              Pruebas JUnit 5
|-- datos/                      Datos locales generados en tiempo de ejecución
|-- mvnw, mvnw.cmd              Maven Wrapper
`-- pom.xml                     Configuración de Maven
```

## Documentación

- `documento/Proyecto-Final-CinemaUQ.docx`: documento final del proyecto.
- `documento/diagrama-completo.puml`: fuente PlantUML del diagrama completo.
- `documento/diagrama-completo.png`: diagrama renderizado.
- `documento/diagrama-completo.pdf`: diagrama en PDF para entrega.

## Notas

- La carpeta `datos/` está excluida del control de versiones porque contiene datos locales de ejecución.
- La aplicación guarda automáticamente los cambios al cerrar la ventana.
- Las credenciales y datos de ejemplo son únicamente para demostración académica.
