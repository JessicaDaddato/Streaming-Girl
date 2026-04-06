using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using StreamingApi.Data;
using StreamingApi.Models;
using Microsoft.AspNetCore.Authorization;


namespace StreamingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]

public class ConteudosController : ControllerBase
{
    private readonly StreamingContext _context;

    public ConteudosController(StreamingContext context)
    {
        _context = context;
    }

    [HttpGet]
    [AllowAnonymous]
    public ActionResult<IEnumerable<Conteudo>> GetAll()
    {
        var conteudos = _context.Conteudos
            .Include(c => c.Criador)
            .Include(c => c.ItensPlaylist)
            .Select(c => new
            {
                c.ID,
                c.Titulo,
                c.Tipo,
                c.CriadorID,
                Criador = c.Criador == null
                    ? null
                    : new
                    {
                        c.Criador.ID,
                        c.Criador.Nome
                    },
                ItensPlaylist = c.ItensPlaylist.Select(ip => new
                {
                    ip.PlaylistID,
                    ip.ConteudoID
                }).ToList()
            })
            .ToList();

        return Ok(conteudos);
    }

    [HttpGet("{id}")]
    [AllowAnonymous]
    public ActionResult<Conteudo> GetById(int id)
    {
        var conteudo = _context.Conteudos
            .Include(c => c.Criador)
            .Include(c => c.ItensPlaylist)
            .Where(c => c.ID == id)
            .Select(c => new
            {
                c.ID,
                c.Titulo,
                c.Tipo,
                c.CriadorID,
                Criador = c.Criador == null
                    ? null
                    : new
                    {
                        c.Criador.ID,
                        c.Criador.Nome
                    },
                ItensPlaylist = c.ItensPlaylist.Select(ip => new
                {
                    ip.PlaylistID,
                    ip.ConteudoID
                }).ToList()
            })
            .FirstOrDefault();

        if (conteudo == null)
        {
            return NotFound("Conteúdo não encontrado.");
        }

        return Ok(conteudo);
    }

    [HttpPost]
    public ActionResult Create(Conteudo conteudo)
    {
        _context.Conteudos.Add(conteudo);
        _context.SaveChanges();

        return CreatedAtAction(nameof(GetById), new { id = conteudo.ID }, conteudo);
    }

    [HttpPut("{id}")]
    public ActionResult Update(int id, Conteudo conteudo)
    {
        if (id != conteudo.ID)
        {
            return BadRequest("O ID da URL é diferente do ID do conteúdo.");
        }

        var conteudoExistente = _context.Conteudos.Find(id);

        if (conteudoExistente == null)
        {
            return NotFound("Conteúdo não encontrado.");
        }

        _context.Entry(conteudoExistente).CurrentValues.SetValues(conteudo);
        _context.SaveChanges();

        return NoContent();
    }

    [HttpDelete("{id}")]
    public ActionResult Delete(int id)
    {
        var conteudo = _context.Conteudos.Find(id);

        if (conteudo == null)
        {
            return NotFound("Conteúdo não encontrado.");
        }

        _context.Conteudos.Remove(conteudo);
        _context.SaveChanges();

        return NoContent();
    }
}
