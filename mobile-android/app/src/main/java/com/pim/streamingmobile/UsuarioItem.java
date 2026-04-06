package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

public class UsuarioItem {
    @SerializedName(value = "id", alternate = {"ID"})
    public int id;
    @SerializedName(value = "nome", alternate = {"Nome"})
    public String nome;
    @SerializedName(value = "email", alternate = {"Email"})
    public String email;
}
