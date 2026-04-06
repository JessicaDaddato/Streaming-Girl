using Microsoft.AspNetCore.Mvc;
using Microsoft.EntityFrameworkCore;
using StreamingApi.Data;
using StreamingApi.Models;
using Microsoft.AspNetCore.Authorization;


namespace StreamingApi.Controllers;

[ApiController]
[Route("api/[controller]")]
[Authorize]

public class CriadoresController : ControllerBase
{
    private readonly StreamingContext _context;

    public CriadoresController(StreamingContext context)
    {
        _context = context;
    }

    [HttpGet]
    [AllowAnonymous]
    public ActionResult<IEnumerable<Criador>> GetAll()
    {
        var criadores = _context.Criadores
            .Include(c => c.Conteudos)
            .ToList();

        return Ok(criadores);
    }

    [HttpGet("{id}")]
    [AllowAnonymous]
    public ActionResult<Criador> GetById(int id)
    {
        var criador = _context.Criadores
            .Include(c => c.Conteudos)
            .FirstOrDefault(c => c.ID == id);

        if (criador == null)
        {
            return NotFound("Criador não encontrado.");
        }

        return Ok(criador);
    }

    [HttpPost]
    public ActionResult Create(Criador criador)
    {
        _context.Criadores.Add(criador);
        _context.SaveChanges();

        return CreatedAtAction(nameof(GetById), new { id = criador.ID }, criador);
    }

    [HttpPut("{id}")]
    public ActionResult Update(int id, Criador criador)
    {
        if (id != criador.ID)
        {
            return BadRequest("O ID da URL é diferente do ID do criador.");
        }

        var criadorExistente = _context.Criadores.Find(id);

        if (criadorExistente == null)
        {
            return NotFound("Criador não encontrado.");
        }

        _context.Entry(criadorExistente).CurrentValues.SetValues(criador);
        _context.SaveChanges();

        return NoContent();
    }

    [HttpDelete("{id}")]
    public ActionResult Delete(int id)
    {
        var criador = _context.Criadores.Find(id);

        if (criador == null)
        {
            return NotFound("Criador não encontrado.");
        }

        _context.Criadores.Remove(criador);
        _context.SaveChanges();

        return NoContent();
    }
}
