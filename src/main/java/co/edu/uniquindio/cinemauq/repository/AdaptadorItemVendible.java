package co.edu.uniquindio.cinemauq.repository;

import co.edu.uniquindio.cinemauq.model.Combo;
import co.edu.uniquindio.cinemauq.model.IItemVendible;
import co.edu.uniquindio.cinemauq.model.Producto;
import co.edu.uniquindio.cinemauq.model.Promocion;
import co.edu.uniquindio.cinemauq.model.TipoProducto;
import co.edu.uniquindio.cinemauq.patrones.decorator.ProductoConPromocion;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

/**
 * Adaptador polimórfico para IItemVendible (Producto, Combo y ProductoConPromocion),
 * necesario porque Gson no puede deserializar interfaces por sí solo.
 */
public class AdaptadorItemVendible implements JsonSerializer<IItemVendible>, JsonDeserializer<IItemVendible> {

    private static final String TIPO = "_tipo";

    @Override
    public JsonElement serialize(IItemVendible src, Type type, JsonSerializationContext context) {
        if (src == null) {
            return JsonNull.INSTANCE;
        }
        if (src instanceof ProductoConPromocion decorado) {
            JsonObject obj = new JsonObject();
            obj.addProperty(TIPO, "decorado");
            obj.add("item", serialize(decorado.getItem(), null, context));
            obj.add("promocion", context.serialize(decorado.getPromocion(), Promocion.class));
            return obj;
        }
        if (src instanceof Combo combo) {
            JsonObject obj = new JsonObject();
            obj.addProperty(TIPO, "combo");
            obj.addProperty("idCombo", combo.getIdCombo());
            obj.addProperty("nombre", combo.getNombre());
            obj.addProperty("precio", combo.getPrecio());
            JsonArray items = new JsonArray();
            for (IItemVendible item : combo.getItems()) {
                items.add(serialize(item, null, context));
            }
            obj.add("items", items);
            return obj;
        }
        if (src instanceof Producto producto) {
            JsonObject obj = new JsonObject();
            obj.addProperty(TIPO, "producto");
            obj.addProperty("idProducto", producto.getIdProducto());
            obj.addProperty("nombre", producto.getNombre());
            obj.addProperty("precio", producto.getPrecio());
            obj.addProperty("tipo", producto.getTipo() != null ? producto.getTipo().name() : null);
            return obj;
        }
        return JsonNull.INSTANCE;
    }

    @Override
    public IItemVendible deserialize(JsonElement json, Type type, JsonDeserializationContext context) {
        if (json == null || json.isJsonNull()) {
            return null;
        }
        JsonObject obj = json.getAsJsonObject();
        String tipo = obj.has(TIPO) ? obj.get(TIPO).getAsString() : "producto";
        return switch (tipo) {
            case "combo" -> {
                Combo combo = new Combo(
                        texto(obj, "idCombo"),
                        texto(obj, "nombre"),
                        numero(obj, "precio"));
                if (obj.has("items") && obj.get("items").isJsonArray()) {
                    for (JsonElement elemento : obj.getAsJsonArray("items")) {
                        combo.agregarItem(deserialize(elemento, null, context));
                    }
                }
                yield combo;
            }
            case "decorado" -> {
                IItemVendible item = deserialize(obj.get("item"), null, context);
                Promocion promocion = context.deserialize(obj.get("promocion"), Promocion.class);
                yield new ProductoConPromocion(item, promocion);
            }
            default -> new Producto(
                    texto(obj, "idProducto"),
                    texto(obj, "nombre"),
                    numero(obj, "precio"),
                    tipoProducto(texto(obj, "tipo")));
        };
    }

    private String texto(JsonObject obj, String campo) {
        return obj.has(campo) && !obj.get(campo).isJsonNull() ? obj.get(campo).getAsString() : null;
    }

    private double numero(JsonObject obj, String campo) {
        return obj.has(campo) && !obj.get(campo).isJsonNull() ? obj.get(campo).getAsDouble() : 0;
    }

    private TipoProducto tipoProducto(String valor) {
        if (valor == null) {
            return null;
        }
        try {
            return TipoProducto.valueOf(valor);
        } catch (IllegalArgumentException e) {
            return null;
        }
    }
}
