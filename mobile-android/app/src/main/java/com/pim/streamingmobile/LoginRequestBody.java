package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

public class LoginRequestBody {
    @SerializedName("Email")
    public String email;
    @SerializedName("Senha")
    public String senha;

    public LoginRequestBody(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
