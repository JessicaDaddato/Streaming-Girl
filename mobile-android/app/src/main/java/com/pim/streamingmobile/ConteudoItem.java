package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

public class ConteudoItem {
    @SerializedName("ID")
    public int id;
    @SerializedName("Titulo")
    public String titulo;
    @SerializedName("Tipo")
    public String tipo;
    @SerializedName("Criador")
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
