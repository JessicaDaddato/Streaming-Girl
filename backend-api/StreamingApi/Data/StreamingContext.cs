using Microsoft.EntityFrameworkCore;
using StreamingApi.Models;

namespace StreamingApi.Data;

public class StreamingContext : DbContext
{
    public StreamingContext(DbContextOptions<StreamingContext> options) : base(options)
    {
    }

    public DbSet<Usuario> Usuarios { get; set; }
    public DbSet<Playlist> Playlists { get; set; }
    public DbSet<Conteudo> Conteudos { get; set; }
    public DbSet<Criador> Criadores { get; set; }
    public DbSet<ItemPlaylist> ItensPlaylist { get; set; }

    protected override void OnModelCreating(ModelBuilder modelBuilder)
    {
        base.OnModelCreating(modelBuilder);

        modelBuilder.Entity<ItemPlaylist>()
            .HasKey(ip => new { ip.PlaylistID, ip.ConteudoID });

        modelBuilder.Entity<Playlist>()
            .HasOne(p => p.Usuario)
            .WithMany(u => u.Playlists)
            .HasForeignKey(p => p.UsuarioID)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<Conteudo>()
            .HasOne(c => c.Criador)
            .WithMany(cr => cr.Conteudos)
            .HasForeignKey(c => c.CriadorID)
            .OnDelete(DeleteBehavior.Cascade);

        modelBuilder.Entity<ItemPlaylist>()
            .HasOne(ip => ip.Playlist)
            .WithMany(p => p.ItensPlaylist)
            .HasForeignKey(ip => ip.PlaylistID);

        modelBuilder.Entity<ItemPlaylist>()
            .HasOne(ip => ip.Conteudo)
            .WithMany(c => c.ItensPlaylist)
            .HasForeignKey(ip => ip.ConteudoID);
    }
}
