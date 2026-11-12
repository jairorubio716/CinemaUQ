package co.edu.uniquindio.cinemauq.repository;

import co.edu.uniquindio.cinemauq.excepciones.CinemaUQException;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RepositorioDatos {

    private final Path archivo;
    private final Gson gson;

    public RepositorioDatos(Path carpeta) {
        this.archivo = carpeta.resolve("cinemauq.json");
        this.gson = new GsonBuilder()
                .setPrettyPrinting()
                .serializeNulls()
                .registerTypeAdapter(LocalDate.class, new AdaptadorLocalDate())
                .registerTypeAdapter(LocalDateTime.class, new AdaptadorLocalDateTime())
                .registerTypeHierarchyAdapter(IItemVendible.class, new AdaptadorItemVendible())
                .create();
    }

    public Path getArchivo() {
        return archivo;
    }

    public DatosCinema cargar() {
        if (!Files.exists(archivo)) {
            return new DatosCinema();
        }
        try {
            String json = Files.readString(archivo, StandardCharsets.UTF_8);
            if (json == null || json.isBlank()) {
                return new DatosCinema();
            }
            DatosCinema datos = gson.fromJson(json, DatosCinema.class);
            return datos != null ? datos : new DatosCinema();
        } catch (IOException | RuntimeException e) {
            throw new CinemaUQException("No se pudo leer el archivo de datos ("
                    + archivo + "): " + e.getMessage(), e);
        }
    }

    public void guardar(DatosCinema datos) {
        try {
            if (archivo.getParent() != null) {
                Files.createDirectories(archivo.getParent());
            }
            Files.writeString(archivo, gson.toJson(datos), StandardCharsets.UTF_8);
        } catch (IOException | RuntimeException e) {
            throw new CinemaUQException("No se pudo guardar el archivo de datos ("
                    + archivo + "): " + e.getMessage(), e);
        }
    }
}
