package com.pim.streamingmobile;

import java.util.ArrayList;
import java.util.List;

public class FavoritosManager {

    private static final List<String> favoritos = new ArrayList<>();

    public static void adicionarFavorito(String titulo) {
        if (!favoritos.contains(titulo)) {
            favoritos.add(titulo);
        }
    }

    public static List<String> getFavoritos() {
        return favoritos;
    }
}