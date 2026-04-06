package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistItem {
    @SerializedName("ID")
    public int id;
    @SerializedName("Nome")
    public String nome;
    @SerializedName("UsuarioID")
    public int usuarioId;
    @SerializedName("ItensPlaylist")
    public List<ItemPlaylistItem> itensPlaylist;

    public int getQuantidadeItens() {
        return itensPlaylist == null ? 0 : itensPlaylist.size();
    }
}
