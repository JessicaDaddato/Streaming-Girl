using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using StreamingApi.Data;
using StreamingApi.Models;
using Microsoft.AspNetCore.Authorization;


namespace StreamingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]

public class UsuariosController : ControllerBase
{
    private readonly StreamingContext _context;

    public UsuariosController(StreamingContext context)
    {
        _context = context;
    }

    [HttpGet]
    [AllowAnonymous]
    public ActionResult<IEnumerable<Usuario>> GetAll()
    {
        var usuarios = _context.Usuarios
            .Include(u => u.Playlists)
            .ToList();

        return Ok(usuarios);
    }

    [HttpGet("{id}")]
    [AllowAnonymous]
    public ActionResult<Usuario> GetById(int id)
    {
        var usuario = _context.Usuarios
            .Include(u => u.Playlists)
            .FirstOrDefault(u => u.ID == id);

        if (usuario == null)
        {
            return NotFound("Usuário não encontrado.");
        }

        return Ok(usuario);
    }

    [HttpPost]
    public ActionResult Create(Usuario usuario)
    {
        _context.Usuarios.Add(usuario);
        _context.SaveChanges();

        return CreatedAtAction(nameof(GetById), new { id = usuario.ID }, usuario);
    }

    [HttpPut("{id}")]
    public ActionResult Update(int id, Usuario usuario)
    {
        if (id != usuario.ID)
        {
            return BadRequest("O ID da URL é diferente do ID do usuário.");
        }

        var usuarioExistente = _context.Usuarios.Find(id);

        if (usuarioExistente == null)
        {
            return NotFound("Usuário não encontrado.");
        }

        _context.Entry(usuarioExistente).CurrentValues.SetValues(usuario);
        _context.SaveChanges();

        return NoContent();
    }

    [HttpDelete("{id}")]
    public ActionResult Delete(int id)
    {
        var usuario = _context.Usuarios.Find(id);

        if (usuario == null)
        {
            return NotFound("Usuário não encontrado.");
        }

        _context.Usuarios.Remove(usuario);
        _context.SaveChanges();

        return NoContent();
    }
}
