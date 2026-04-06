using Microsoft.AspNetCore.Mvc;
using StreamingApi.Models;
using StreamingApi.Repositories;
using Microsoft.AspNetCore.Authorization;



namespace StreamingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]

public class PlaylistsController : ControllerBase
{
    private readonly PlaylistRepository _playlistRepository;

    public PlaylistsController(PlaylistRepository playlistRepository)
    {
        _playlistRepository = playlistRepository;
    }

    [HttpGet]
    [AllowAnonymous]
    public ActionResult<IEnumerable<Playlist>> GetAll()
    {
        var playlists = _playlistRepository.GetAllPlaylists()
            .Select(p => new
            {
                p.ID,
                p.Nome,
                p.UsuarioID,
                Usuario = p.Usuario == null
                    ? null
                    : new
                    {
                        p.Usuario.ID,
                        p.Usuario.Nome,
                        p.Usuario.Email
                    },
                ItensPlaylist = p.ItensPlaylist.Select(ip => new
                {
                    ip.PlaylistID,
                    ip.ConteudoID
                }).ToList()
            })
            .ToList();
        return Ok(playlists);
    }

    [HttpGet("{id}")]
    [AllowAnonymous]
    public ActionResult<Playlist> GetById(int id)
    {
        var playlistBase = _playlistRepository.GetPlaylistByID(id);

        if (playlistBase == null)
        {
            return NotFound("Playlist não encontrada.");
        }

        var playlist = new
        {
            playlistBase.ID,
            playlistBase.Nome,
            playlistBase.UsuarioID,
            Usuario = playlistBase.Usuario == null
                ? null
                : new
                {
                    playlistBase.Usuario.ID,
                    playlistBase.Usuario.Nome,
                    playlistBase.Usuario.Email
                },
            ItensPlaylist = playlistBase.ItensPlaylist.Select(ip => new
            {
                ip.PlaylistID,
                ip.ConteudoID
            }).ToList()
        };

        return Ok(playlist);
    }

    [HttpPost]
    public ActionResult Create(Playlist playlist)
    {
        _playlistRepository.AddPlaylist(playlist);
        return CreatedAtAction(nameof(GetById), new { id = playlist.ID }, playlist);
    }

    [HttpPut("{id}")]
    public ActionResult Update(int id, Playlist playlist)
    {
        if (id != playlist.ID)
        {
            return BadRequest("O ID da URL é diferente do ID da playlist.");
        }

        var playlistExistente = _playlistRepository.GetPlaylistByID(id);

        if (playlistExistente == null)
        {
            return NotFound("Playlist não encontrada.");
        }

        _playlistRepository.UpdatePlaylist(playlist);
        return NoContent();
    }

    [HttpDelete("{id}")]
    public ActionResult Delete(int id)
    {
        var playlistExistente = _playlistRepository.GetPlaylistByID(id);

        if (playlistExistente == null)
        {
            return NotFound("Playlist não encontrada.");
        }

        _playlistRepository.DeletePlaylist(id);
        return NoContent();
    }
}
