Detalhe importante: como README do GitHub, o ideal é usar caminhos relativos e não absolutos para links internos. Se você quiser, eu posso fazer uma segunda passada rapidinha para deixá-lo 100% otimizado para publicação no GitHub e já também commitar isso.


1 arquivo alterado
Desfazer
README.md
# PinkStream

Projeto acadêmico desenvolvido para o PIM com foco em um sistema de streaming de conteúdo multimídia.

A solução está organizada em duas partes principais:

- `backend-api/StreamingApi`: Web API em C# com ASP.NET Core, Entity Framework Core, JWT, Swagger e banco SQLite
- `mobile-android`: aplicativo Android em Java para o usuário final, com navegação, consumo de conteúdo, playlists, perfil e interações sociais simuladas

## Objetivo do projeto

O projeto representa uma plataforma de streaming com:

- cadastro e consulta de usuários, playlists, conteúdos e criadores
- autenticação com JWT
- operações CRUD na API
- documentação da API com Swagger
- aplicativo Android voltado ao usuário final

## Tecnologias utilizadas

### Backend

- C#
- ASP.NET Core Web API
- Entity Framework Core
- SQLite
- Swagger / OpenAPI
- JWT Bearer Authentication

### Frontend mobile

- Java
- Android Studio
- Gradle

## Estrutura do repositório

```text
PIM-Streaming/
├── backend-api/
│   └── StreamingApi/
├── mobile-android/
├── maui-criador/
├── docs/
├── imagens/
└── Orientações do PIM/
```

## Observação sobre o .NET MAUI

O enunciado do trabalho prevê uma interface complementar em `.NET MAUI`, voltada ao módulo de criadores de conteúdo.

Nesta versão do projeto, a parte efetivamente implementada foi:

- o backend em ASP.NET Core + Entity Framework
- o aplicativo Android/Java para o usuário final

A pasta `maui-criador` existe no repositório como espaço reservado para evolução futura, mas o protótipo MAUI não foi desenvolvido nesta entrega.

Para apresentação acadêmica:

> A solução entregue contempla integralmente a Web API em ASP.NET Core com Entity Framework e a aplicação mobile Android em Java. 
O protótipo complementar em .NET MAUI, previsto como interface para criadores de conteúdo, foi tratado como expansão futura do projeto devido aos erros apresentados.

## Entidades principais

O trecho do banco de dados trabalha com as seguintes entidades:

- `Usuario`
- `Playlist`
- `Conteudo`
- `Criador`
- `ItemPlaylist`

## Funcionalidades implementadas

### API

- autenticação por JWT
- CRUD de playlists
- CRUD de conteúdos
- CRUD de usuários
- CRUD de criadores
- acesso ao banco via Entity Framework Core
- documentação e testes via Swagger

### App Android

- tela de login separada da home
- home com destaques e sugestões
- navegação para conteúdos, playlists e perfil
- detalhe de conteúdo com curtidas, comentários e seguir criador
- gerenciamento de playlists
- envio simples de arquivo apenas em nível visual
- resumo de perfil com:
  - conteúdos curtidos
  - playlists disponíveis
  - criadores seguidos
  - conteúdos enviados

## Banco de dados

O banco utilizado é SQLite e fica em:

[streaming.db](/Users/jessicadaddato/Library/Mobile%20Documents/com~apple~CloudDocs/PIM-Streaming/backend-api/StreamingApi/streaming.db)

### Como abrir o banco

#### Opção 1: terminal

```bash
sqlite3 "/Users/jessicadaddato/Library/Mobile Documents/com~apple~CloudDocs/PIM-Streaming/backend-api/StreamingApi/streaming.db"
```

Comandos úteis:

```sql
.tables
select * from Usuarios;
select * from Playlists;
select * from Conteudos;
select * from ItensPlaylist;
.quit
```

#### Opção 2: interface gráfica

É possível abrir o arquivo no:

- SQLite Viewer no VS Code
- DB Browser for SQLite
- TablePlus

## Como executar o backend

No terminal:

```bash
cd "/Users/jessicadaddato/Library/Mobile Documents/com~apple~CloudDocs/PIM-Streaming/backend-api/StreamingApi"
dotnet run --urls http://0.0.0.0:5106
```

### Swagger

Abra no navegador:

- [http://localhost:5106/swagger](http://localhost:5106/swagger)

## Como executar o app Android

1. Abra a pasta `mobile-android` no Android Studio
2. Aguarde o carregamento do Gradle
3. Abra um emulador no `Device Manager`
4. Clique em `Run`

### URL usada pelo app no emulador

O app está configurado para consumir a API local via:

```text
http://10.0.2.2:5106/api/
```

## Credenciais de teste

Use estas credenciais para login:

```text
E-mail: admin@streaming.com
Senha: 123456
```

## Como testar a aplicação

### Backend

1. subir a API com `dotnet run`
2. abrir o Swagger
3. testar o endpoint de login
4. testar os endpoints de playlists e conteúdos

### App Android

1. abrir a tela de login
2. entrar com as credenciais de teste
3. acessar a home
4. abrir conteúdos
5. abrir detalhe de conteúdo
6. testar curtidas, comentários e seguir criador
7. acessar playlists
8. criar, editar e excluir playlists
9. selecionar um arquivo no botão de envio
10. abrir o perfil e conferir o resumo

## Requisitos atendidos

### Atendidos

- Web API em ASP.NET Core
- Entity Framework Core para acesso ao banco
- autenticação e autorização com JWT
- Swagger para documentação da API
- app Android em Java para o usuário final
- integração app + backend
- banco SQLite com dados para demonstração

### Parcialmente atendidos no escopo geral do enunciado

- O módulo em `.NET MAUI` para criadores de conteúdo não foi implementado nesta versão

## Observações finais

- O envio de arquivo no app atualmente é apenas visual e local, sem persistência no banco
- O foco principal desta entrega está no backend funcional e no aplicativo Android para o usuário final
- O projeto pode ser expandido futuramente com:
  - upload real de mídia
  - persistência de interações sociais
  - módulo MAUI para criadores de conteúdo

## Autora

- Jessica Daddato
