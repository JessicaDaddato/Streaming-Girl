package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

public class UsuarioItem {
    @SerializedName("ID")
    public int id;
    @SerializedName("Nome")
    public String nome;
    @SerializedName("Email")
    public String email;
}
