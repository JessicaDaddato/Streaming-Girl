// Usuario.cs
namespace StreamingApi.Models;

public class Usuario
{
    public int ID { get; set; }
    public string Nome { get; set; } = string.Empty;
    public string Email { get; set; } = string.Empty;

    public List<Playlist> Playlists { get; set; } = new();
}
