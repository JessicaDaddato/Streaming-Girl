package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

public class ConteudoItem {
    @SerializedName(value = "id", alternate = {"ID"})
    public int id;
    @SerializedName(value = "titulo", alternate = {"Titulo"})
    public String titulo;
    @SerializedName(value = "tipo", alternate = {"Tipo"})
    public String tipo;
    @SerializedName(value = "criador", alternate = {"Criador"})
    public CriadorItem criador;

    public String getCategoria() {
        String tipoTexto = tipo == null || tipo.isBlank() ? "Conteudo" : tipo;
        String criadorNome = criador != null && criador.nome != null && !criador.nome.isBlank()
                ? criador.nome
                : "Criador nao informado";
        return tipoTexto + " • " + criadorNome;
    }

    public String getDescricao() {
        String tituloTexto = titulo == null || titulo.isBlank() ? "Conteudo" : titulo;
        String criadorNome = criador != null && criador.nome != null && !criador.nome.isBlank()
                ? criador.nome
                : "um criador da plataforma";
        return tituloTexto + " disponivel no streaming, publicado por " + criadorNome + ".";
    }

    public String getNomeCriador() {
        return criador != null && criador.nome != null && !criador.nome.isBlank()
                ? criador.nome
                : "Criador nao informado";
    }
}
