package co.edu.uniquindio.cinemauq.patrones.decorator;

import co.edu.uniquindio.cinemauq.excepciones.PromocionNoAplicableException;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Promocion;

public class ProductoConPromocion implements IItemVendible {

    private IItemVendible item;
    private Promocion promocion;

    public ProductoConPromocion(IItemVendible item, Promocion promocion) {
        if (item == null || promocion == null) {
            throw new PromocionNoAplicableException("Se requiere un ítem y una promoción válidos.");
        }
        this.item = item;
        this.promocion = promocion;
    }

    public IItemVendible getItem() {
        return item;
    }

    public void setItem(IItemVendible item) {
        this.item = item;
    }

    public Promocion getPromocion() {
        return promocion;
    }

    public void setPromocion(Promocion promocion) {
        this.promocion = promocion;
    }

    @Override
    public String getId() {
        return item.getId();
    }

    @Override
    public double obtenerPrecio() {
        double base = item.obtenerPrecio();
        double precioFinal = base - promocion.calcularDescuento(base);
        return Math.max(0, precioFinal);
    }

    @Override
    public String descripcion() {
        return item.descripcion() + " (promo: " + promocion.getNombre() + ")";
    }
}
