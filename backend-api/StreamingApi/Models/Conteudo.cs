// Conteudo.cs
namespace StreamingApi.Models;

public class Conteudo
{
    public int ID { get; set; }
    public string Titulo { get; set; } = string.Empty;
    public string Tipo { get; set; } = string.Empty;

    public int CriadorID { get; set; }
    public Criador? Criador { get; set; }

    public List<ItemPlaylist> ItensPlaylist { get; set; } = new();
}
