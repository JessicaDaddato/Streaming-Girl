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
        var playlists = _playlistRepository.GetAllPlaylists();
        return Ok(playlists);
    }

    [HttpGet("{id}")]
    [AllowAnonymous]
    public ActionResult<Playlist> GetById(int id)
    {
        var playlist = _playlistRepository.GetPlaylistByID(id);

        if (playlist == null)
        {
            return NotFound("Playlist não encontrada.");
        }

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
