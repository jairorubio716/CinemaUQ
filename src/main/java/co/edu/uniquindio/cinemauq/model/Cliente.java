package co.edu.uniquindio.cinemauq.model;
import java.time.LocalDate;

public class Cliente extends Persona{

    private String idCliente;
    private LocalDate fechaRegistro;

    public Cliente(String nombre, String email, String celular, String usuario, String contrasena, String idCliente, LocalDate fechaRegistro) {
        super(nombre, email, celular, usuario, contrasena);
        this.idCliente = idCliente;
        this.fechaRegistro = fechaRegistro;
    }

    public String getIdCliente() {
        return idCliente;
    }
    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }
    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    @Override
    public String toString() {
        return "Cliente{" + super.toString() +
                ", idCliente='" + idCliente + '\'' +
                ", fechaRegistro=" + fechaRegistro +
                '}';
    }
}
