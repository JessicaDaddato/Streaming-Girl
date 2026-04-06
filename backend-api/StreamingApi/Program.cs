using System.Text;
using System.Text.Json.Serialization;
using Microsoft.AspNetCore.Authentication.JwtBearer;
using Microsoft.EntityFrameworkCore;
using Microsoft.IdentityModel.Tokens;
using StreamingApi.Data;
using StreamingApi.Models;
using StreamingApi.Repositories;
using Microsoft.OpenApi.Models;
using Microsoft.AspNetCore.Authorization;



var builder = WebApplication.CreateBuilder(args);

builder.Services.AddControllers().AddJsonOptions(options =>
{
    options.JsonSerializerOptions.ReferenceHandler = ReferenceHandler.IgnoreCycles;
});

builder.Services.AddEndpointsApiExplorer();
builder.Services.AddSwaggerGen(options =>
{
    options.SwaggerDoc("v1", new OpenApiInfo
    {
        Title = "StreamingApi",
        Version = "v1"
    });

    options.AddSecurityDefinition("Bearer", new OpenApiSecurityScheme
    {
        Name = "Authorization",
        Type = SecuritySchemeType.Http,
        Scheme = "bearer",
        BearerFormat = "JWT",
        In = ParameterLocation.Header,
        Description = "Informe o token JWT no campo abaixo. Exemplo: Bearer seu_token"
    });

    options.AddSecurityRequirement(new OpenApiSecurityRequirement
    {
        {
            new OpenApiSecurityScheme
            {
                Reference = new OpenApiReference
                {
                    Type = ReferenceType.SecurityScheme,
                    Id = "Bearer"
                }
            },
            Array.Empty<string>()
        }
    });
});


builder.Services.AddDbContext<StreamingContext>(options =>
    options.UseSqlite("Data Source=streaming.db"));

builder.Services.AddScoped<PlaylistRepository>();

builder.Services.AddAuthentication(JwtBearerDefaults.AuthenticationScheme)
    .AddJwtBearer(options =>
    {
        options.TokenValidationParameters = new TokenValidationParameters
        {
            ValidateIssuer = true,
            ValidateAudience = true,
            ValidateLifetime = true,
            ValidateIssuerSigningKey = true,
            ValidIssuer = builder.Configuration["Jwt:Issuer"],
            ValidAudience = builder.Configuration["Jwt:Audience"],
            IssuerSigningKey = new SymmetricSecurityKey(
                Encoding.UTF8.GetBytes(builder.Configuration["Jwt:Key"]!)
            )
        };
    });

builder.Services.AddAuthorization();

var app = builder.Build();

using (var scope = app.Services.CreateScope())
{
    var context = scope.ServiceProvider.GetRequiredService<StreamingContext>();
    context.Database.Migrate();
    SeedDatabase(context);
}

app.UseSwagger();
app.UseSwaggerUI(options =>
{
    options.SwaggerEndpoint("/swagger/v1/swagger.json", "StreamingApi v1");
    options.RoutePrefix = "swagger";
});

app.MapGet("/", () => Results.Redirect("/swagger"))
    .AllowAnonymous();
app.MapGet("/swegger", () => Results.Redirect("/swagger"))
    .AllowAnonymous();
app.MapGet("/swagger/index.html", () => Results.Redirect("/swagger"))
    .AllowAnonymous();

app.UseAuthentication();
app.UseAuthorization();

app.MapControllers();

app.Run();

static void SeedDatabase(StreamingContext context)
{
    var usuario = context.Usuarios.FirstOrDefault();
    if (usuario == null)
    {
        usuario = new Usuario
        {
            Nome = "Maria Silva",
            Email = "maria@email.com"
        };
        context.Usuarios.Add(usuario);
        context.SaveChanges();
    }

    var criador = context.Criadores.FirstOrDefault(c => c.Nome == "Canal Cultura Tech");
    if (criador == null)
    {
        criador = new Criador
        {
            Nome = "Canal Cultura Tech"
        };
        context.Criadores.Add(criador);
        context.SaveChanges();
    }

    var conteudosPadrao = new[]
    {
        new { Titulo = "Code e Pink", Tipo = "Tecnologia" },
        new { Titulo = "Soft Life Diaries", Tipo = "Vlog" },
        new { Titulo = "Late Night Playlist", Tipo = "Musica" }
    };

    foreach (var conteudoPadrao in conteudosPadrao)
    {
        var existeConteudo = context.Conteudos.Any(c => c.Titulo == conteudoPadrao.Titulo);
        if (existeConteudo)
        {
            continue;
        }

        context.Conteudos.Add(new Conteudo
        {
            Titulo = conteudoPadrao.Titulo,
            Tipo = conteudoPadrao.Tipo,
            CriadorID = criador.ID
        });
    }

    context.SaveChanges();

    var playlistsPadrao = new[]
    {
        "Girl Coding Night",
        "Soft Life Mood",
        "Late Night Relax"
    };

    foreach (var nomePlaylist in playlistsPadrao)
    {
        var existePlaylist = context.Playlists.Any(p => p.Nome == nomePlaylist);
        if (existePlaylist)
        {
            continue;
        }

        context.Playlists.Add(new Playlist
        {
            Nome = nomePlaylist,
            UsuarioID = usuario.ID
        });
    }

    context.SaveChanges();
}
