package com.pim.streamingmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    Button btnConteudos, btnPlaylists, btnPerfil;
    Button btnLogin, btnLogout;
    Button btnSalvarApi;
    Button btnAbrirDestaque, btnAbrirSugestao1, btnAbrirSugestao2;
    EditText edtEmail, edtSenha, edtApiBaseUrl;
    TextView txtStatusLogin, txtApiAtual;
    TextView txtTituloDestaque, txtDescricaoDestaque;
    TextView txtSugestao1Titulo, txtSugestao1Categoria, txtSugestao2Titulo, txtSugestao2Categoria;
    private final java.util.List<ConteudoItem> conteudosHome = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnConteudos = findViewById(R.id.btnConteudos);
        btnPlaylists = findViewById(R.id.btnPlaylists);
        btnPerfil = findViewById(R.id.btnPerfil);
        btnLogin = findViewById(R.id.btnLogin);
        btnLogout = findViewById(R.id.btnLogout);
        btnSalvarApi = findViewById(R.id.btnSalvarApi);
        btnAbrirDestaque = findViewById(R.id.btnAbrirDestaque);
        btnAbrirSugestao1 = findViewById(R.id.btnAbrirSugestao1);
        btnAbrirSugestao2 = findViewById(R.id.btnAbrirSugestao2);
        edtEmail = findViewById(R.id.edtEmail);
        edtSenha = findViewById(R.id.edtSenha);
        edtApiBaseUrl = findViewById(R.id.edtApiBaseUrl);
        txtStatusLogin = findViewById(R.id.txtStatusLogin);
        txtApiAtual = findViewById(R.id.txtApiAtual);
        txtTituloDestaque = findViewById(R.id.txtTituloDestaque);
        txtDescricaoDestaque = findViewById(R.id.txtDescricaoDestaque);
        txtSugestao1Titulo = findViewById(R.id.txtSugestao1Titulo);
        txtSugestao1Categoria = findViewById(R.id.txtSugestao1Categoria);
        txtSugestao2Titulo = findViewById(R.id.txtSugestao2Titulo);
        txtSugestao2Categoria = findViewById(R.id.txtSugestao2Categoria);

        edtEmail.setText("admin@streaming.com");
        edtSenha.setText("123456");
        edtApiBaseUrl.setText(ApiSettingsManager.getBaseUrl(this));

        btnConteudos.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ConteudosActivity.class);
            startActivity(intent);
        });

        btnPlaylists.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PlaylistsActivity.class);
            startActivity(intent);
        });

        btnPerfil.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, PerfilActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> realizarLogin());
        btnSalvarApi.setOnClickListener(v -> salvarApiBaseUrl());
        btnAbrirDestaque.setOnClickListener(v -> abrirDetalhe(0));
        btnAbrirSugestao1.setOnClickListener(v -> abrirDetalhe(1));
        btnAbrirSugestao2.setOnClickListener(v -> abrirDetalhe(2));
        btnLogout.setOnClickListener(v -> {
            AuthManager.clearToken(this);
            atualizarEstadoAutenticacao();
            Toast.makeText(this, "Sessao encerrada.", Toast.LENGTH_SHORT).show();
        });

        atualizarEstadoAutenticacao();
        carregarHome();
    }

    private void realizarLogin() {
        String email = edtEmail.getText().toString().trim();
        String senha = edtSenha.getText().toString().trim();

        if (email.isEmpty() || senha.isEmpty()) {
            Toast.makeText(this, "Informe e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        txtStatusLogin.setText("Fazendo login...");
        btnLogin.setEnabled(false);

        ApiClient.login(email, senha, new ApiClient.ApiCallback<LoginResponse>() {
            @Override
            public void onSuccess(LoginResponse result) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);

                    if (result == null || result.token == null || result.token.isBlank()) {
                        txtStatusLogin.setText("Login falhou.");
                        Toast.makeText(MainActivity.this,
                                "Token nao recebido do backend.",
                                Toast.LENGTH_LONG).show();
                        return;
                    }

                    AuthManager.saveToken(MainActivity.this, result.token);
                    atualizarEstadoAutenticacao();
                    Toast.makeText(MainActivity.this, "Login realizado com sucesso.", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    btnLogin.setEnabled(true);
                    txtStatusLogin.setText("Falha no login.");
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void atualizarEstadoAutenticacao() {
        boolean logado = AuthManager.isLoggedIn(this);
        txtStatusLogin.setText(logado
                ? "JWT ativo. CRUD autenticado liberado."
                : "Faça login para liberar POST, PUT e DELETE.");
        txtApiAtual.setText("API atual: " + ApiSettingsManager.getBaseUrl(this));
        btnLogout.setEnabled(logado);
    }

    private void salvarApiBaseUrl() {
        String baseUrl = edtApiBaseUrl.getText().toString();
        String normalized = ApiSettingsManager.normalize(baseUrl);
        ApiSettingsManager.saveBaseUrl(this, normalized);
        txtApiAtual.setText("API atual: " + normalized);
        Toast.makeText(this, "Endereco da API salvo.", Toast.LENGTH_SHORT).show();
        carregarHome();
    }

    private void carregarHome() {
        definirHomeCarregando();

        ApiClient.getConteudos(new ApiClient.ApiCallback<java.util.List<ConteudoItem>>() {
            @Override
            public void onSuccess(java.util.List<ConteudoItem> result) {
                runOnUiThread(() -> atualizarHome(result));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    txtTituloDestaque.setText("Nao foi possivel carregar o destaque");
                    txtDescricaoDestaque.setText("Confira o endereco da API e tente novamente.");
                });
            }
        });
    }

    private void definirHomeCarregando() {
        txtTituloDestaque.setText("Carregando destaques...");
        txtDescricaoDestaque.setText("Buscando os melhores conteudos para sua home.");
        txtSugestao1Titulo.setText("Carregando...");
        txtSugestao1Categoria.setText("Aguarde");
        txtSugestao2Titulo.setText("Carregando...");
        txtSugestao2Categoria.setText("Aguarde");
    }

    private void atualizarHome(java.util.List<ConteudoItem> result) {
        conteudosHome.clear();
        if (result != null) {
            conteudosHome.addAll(result);
        }

        preencherDestaque(0, txtTituloDestaque, txtDescricaoDestaque, btnAbrirDestaque, true);
        preencherSugestao(1, txtSugestao1Titulo, txtSugestao1Categoria, btnAbrirSugestao1);
        preencherSugestao(2, txtSugestao2Titulo, txtSugestao2Categoria, btnAbrirSugestao2);
    }

    private void preencherDestaque(int index, TextView titulo, TextView descricao, Button botao, boolean destaque) {
        if (index < conteudosHome.size()) {
            ConteudoItem conteudo = conteudosHome.get(index);
            titulo.setText(conteudo.titulo);
            descricao.setText(conteudo.getDescricao());
            botao.setEnabled(true);
            return;
        }

        titulo.setText("Conteudo indisponivel");
        descricao.setText("Suba a API para visualizar os destaques.");
        botao.setEnabled(false);
    }

    private void preencherSugestao(int index, TextView titulo, TextView categoria, Button botao) {
        if (index < conteudosHome.size()) {
            ConteudoItem conteudo = conteudosHome.get(index);
            titulo.setText(conteudo.titulo);
            categoria.setText(conteudo.getCategoria());
            botao.setEnabled(true);
            return;
        }

        titulo.setText("Conteudo indisponivel");
        categoria.setText("API offline");
        botao.setEnabled(false);
    }

    private void abrirDetalhe(int index) {
        if (index >= conteudosHome.size()) {
            Toast.makeText(this, "Conteudo ainda nao carregado.", Toast.LENGTH_SHORT).show();
            return;
        }

        ConteudoItem conteudo = conteudosHome.get(index);
        Intent intent = new Intent(MainActivity.this, DetalheConteudoActivity.class);
        intent.putExtra("titulo", conteudo.titulo);
        intent.putExtra("categoria", conteudo.getCategoria());
        intent.putExtra("descricao", conteudo.getDescricao());
        intent.putExtra("criador", conteudo.getNomeCriador());
        startActivity(intent);
    }
}
