package mx.NovaCore.ia;

import java.util.LinkedHashMap;
import java.util.Map;

public class NodoArbol {

    private String atributo;
    private String clase;
    private Map<String, Integer> conteo = new LinkedHashMap<>();
    private Map<String, NodoArbol> hijos = new LinkedHashMap<>();

    public String clasificar(Map<String, String> registro) {
        if (atributo == null) {
            return clase;
        }

        NodoArbol hijo = hijos.get(registro.get(atributo));

        if (hijo == null) {
            return clase;
        }

        return hijo.clasificar(registro);
    }

    public String getAtributo() {
        return atributo;
    }

    public void setAtributo(String atributo) {
        this.atributo = atributo;
    }

    public String getClase() {
        return clase;
    }

    public void setClase(String clase) {
        this.clase = clase;
    }

    public Map<String, Integer> getConteo() {
        return conteo;
    }

    public void setConteo(Map<String, Integer> conteo) {
        this.conteo = conteo;
    }

    public Map<String, NodoArbol> getHijos() {
        return hijos;
    }

    public void setHijos(Map<String, NodoArbol> hijos) {
        this.hijos = hijos;
    }
}