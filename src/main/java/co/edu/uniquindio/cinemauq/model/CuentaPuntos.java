package co.edu.uniquindio.cinemauq.model;

public class CuentaPuntos {

    private String idCuenta;

    public CuentaPuntos(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void setIdCuenta(String idCuenta) {
        this.idCuenta = idCuenta;
    }

    @Override
    public String toString() {
        return "CuentaPuntos{" +
                "idCuenta='" + idCuenta + '\'' +
                '}';
    }
}