package com.pim.streamingmobile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class SocialManager {

    private static final Map<String, Integer> likesPorConteudo = new HashMap<>();
    private static final Map<String, List<String>> comentariosPorConteudo = new HashMap<>();
    private static final Set<String> criadoresSeguidos = new HashSet<>();

    private SocialManager() {
    }

    private static void garantirSeed(String titulo) {
        if (likesPorConteudo.containsKey(titulo)) {
            return;
        }

        likesPorConteudo.put(titulo, 18 + titulo.length());

        List<String> comentarios = new ArrayList<>();
        comentarios.add("Amei esse conteudo, muito leve de acompanhar.");
        comentarios.add("Entrou para minha lista de favoritos.");
        comentariosPorConteudo.put(titulo, comentarios);
    }

    public static int getLikes(String titulo) {
        garantirSeed(titulo);
        return likesPorConteudo.get(titulo);
    }

    public static int like(String titulo) {
        garantirSeed(titulo);
        int total = likesPorConteudo.get(titulo) + 1;
        likesPorConteudo.put(titulo, total);
        return total;
    }

    public static List<String> getComentarios(String titulo) {
        garantirSeed(titulo);
        return new ArrayList<>(comentariosPorConteudo.get(titulo));
    }

    public static void adicionarComentario(String titulo, String comentario) {
        garantirSeed(titulo);
        comentariosPorConteudo.get(titulo).add(0, comentario);
    }

    public static boolean seguirCriador(String nomeCriador) {
        return criadoresSeguidos.add(nomeCriador);
    }

    public static boolean isCriadorSeguido(String nomeCriador) {
        return criadoresSeguidos.contains(nomeCriador);
    }

    public static int getQuantidadeCriadoresSeguidos() {
        return criadoresSeguidos.size();
    }
}
