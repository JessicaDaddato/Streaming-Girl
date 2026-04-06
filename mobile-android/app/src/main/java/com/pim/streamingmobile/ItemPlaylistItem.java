package com.pim.streamingmobile;

import com.google.gson.annotations.SerializedName;

public class ItemPlaylistItem {
    @SerializedName(value = "playlistId", alternate = {"PlaylistID"})
    public int playlistId;

    @SerializedName(value = "conteudoId", alternate = {"ConteudoID"})
    public int conteudoId;
}
