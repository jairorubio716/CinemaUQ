package co.edu.uniquindio.cinemauq.service;

import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Promocion;

import java.util.List;

public record CotizacionCompra(double subtotal, double descuento, double total,
                               List<LineaConfiteria> confiteria) {

    public CotizacionCompra {
        confiteria = List.copyOf(confiteria != null ? confiteria : List.of());
    }

    public record LineaConfiteria(IItemVendible item, int cantidad,
                                  double precioUnitarioBase, double precioUnitarioFinal,
                                  Promocion promocion) {

        public double subtotalBase() {
            return precioUnitarioBase * cantidad;
        }

        public double subtotalFinal() {
            return precioUnitarioFinal * cantidad;
        }

        public double descuento() {
            return subtotalBase() - subtotalFinal();
        }
    }
}
