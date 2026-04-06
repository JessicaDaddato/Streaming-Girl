// Criador.cs
namespace StreamingApi.Models;

public class Criador
{
    public int ID { get; set; }
    public string Nome { get; set; } = string.Empty;

    public List<Conteudo> Conteudos { get; set; } = new();
}
