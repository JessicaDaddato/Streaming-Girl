package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlaylistItem {
    @SerializedName(value = "id", alternate = {"ID"})
    public int id;
    @SerializedName(value = "nome", alternate = {"Nome"})
    public String nome;
    @SerializedName(value = "usuarioId", alternate = {"UsuarioID"})
    public int usuarioId;
    @SerializedName(value = "itensPlaylist", alternate = {"ItensPlaylist"})
    public List<ItemPlaylistItem> itensPlaylist;

    public int getQuantidadeItens() {
        return itensPlaylist == null ? 0 : itensPlaylist.size();
    }
}
