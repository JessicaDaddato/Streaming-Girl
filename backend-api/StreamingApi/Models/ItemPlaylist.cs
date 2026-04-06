// ItemPlaylist.cs
namespace StreamingApi.Models;

public class ItemPlaylist
{
    public int PlaylistID { get; set; }
    public Playlist? Playlist { get; set; }

    public int ConteudoID { get; set; }
    public Conteudo? Conteudo { get; set; }
}
