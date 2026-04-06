package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

public class PlaylistRequest {
    @SerializedName("ID")
    public int id;
    @SerializedName("Nome")
    public String nome;
    @SerializedName("UsuarioID")
    public int usuarioID;

    public PlaylistRequest(int id, String nome, int usuarioID) {
        this.id = id;
        this.nome = nome;
        this.usuarioID = usuarioID;
    }
}
