package com.pim.streamingmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ConteudosActivity extends AppCompatActivity {

    Button btnVoltar;
    Button btnDetalhe1, btnDetalhe2, btnDetalhe3;
    TextView txtTitulo1, txtCategoria1, txtStats1;
    TextView txtTitulo2, txtCategoria2, txtStats2;
    TextView txtTitulo3, txtCategoria3, txtStats3;
    private final List<ConteudoItem> conteudos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteudos);

        btnDetalhe1 = findViewById(R.id.btnDetalhe1);
        btnDetalhe2 = findViewById(R.id.btnDetalhe2);
        btnDetalhe3 = findViewById(R.id.btnDetalhe3);
        txtTitulo1 = findViewById(R.id.txtTituloConteudo1);
        txtCategoria1 = findViewById(R.id.txtCategoriaConteudo1);
        txtStats1 = findViewById(R.id.txtStatsConteudo1);
        txtTitulo2 = findViewById(R.id.txtTituloConteudo2);
        txtCategoria2 = findViewById(R.id.txtCategoriaConteudo2);
        txtStats2 = findViewById(R.id.txtStatsConteudo2);
        txtTitulo3 = findViewById(R.id.txtTituloConteudo3);
        txtCategoria3 = findViewById(R.id.txtCategoriaConteudo3);
        txtStats3 = findViewById(R.id.txtStatsConteudo3);

        configurarCliqueConteudo(btnDetalhe1, 0);
        configurarCliqueConteudo(btnDetalhe2, 1);
        configurarCliqueConteudo(btnDetalhe3, 2);

        btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> finish());

        carregarConteudos();
    }

    private void configurarCliqueConteudo(Button button, int index) {
        button.setOnClickListener(v -> abrirDetalhe(index));
    }

    private void carregarConteudos() {
        definirEstadoCarregando();

        ApiClient.getConteudos(new ApiClient.ApiCallback<List<ConteudoItem>>() {
            @Override
            public void onSuccess(List<ConteudoItem> result) {
                runOnUiThread(() -> atualizarConteudos(result));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(ConteudosActivity.this,
                            "Nao foi possivel carregar os conteudos.",
                            Toast.LENGTH_LONG).show();
                    definirEstadoErro();
                });
            }
        });
    }

    private void atualizarConteudos(List<ConteudoItem> result) {
        conteudos.clear();
        if (result != null) {
            conteudos.addAll(result);
        }

        atualizarCard(0, txtTitulo1, txtCategoria1, txtStats1, btnDetalhe1);
        atualizarCard(1, txtTitulo2, txtCategoria2, txtStats2, btnDetalhe2);
        atualizarCard(2, txtTitulo3, txtCategoria3, txtStats3, btnDetalhe3);
    }

    private void atualizarCard(int index, TextView titulo, TextView categoria,
                               TextView stats, Button button) {
        if (index < conteudos.size()) {
            ConteudoItem conteudo = conteudos.get(index);
            button.setEnabled(true);
            titulo.setText(conteudo.titulo);
            categoria.setText(conteudo.getCategoria());
            stats.setText(SocialManager.getLikes(conteudo.titulo) + " curtidas • "
                    + SocialManager.getComentarios(conteudo.titulo).size() + " comentarios");
            button.setText("Assistir agora");
        } else {
            button.setEnabled(false);
            titulo.setText("Conteudo indisponivel");
            categoria.setText("API offline");
            stats.setText("Sem interacoes");
            button.setText("Indisponivel");
        }
    }

    private void definirEstadoCarregando() {
        btnDetalhe1.setEnabled(false);
        btnDetalhe2.setEnabled(false);
        btnDetalhe3.setEnabled(false);
        btnDetalhe1.setText("Carregando...");
        btnDetalhe2.setText("Carregando...");
        btnDetalhe3.setText("Carregando...");
        txtTitulo1.setText("Carregando...");
        txtTitulo2.setText("Carregando...");
        txtTitulo3.setText("Carregando...");
        txtCategoria1.setText("Aguarde");
        txtCategoria2.setText("Aguarde");
        txtCategoria3.setText("Aguarde");
        txtStats1.setText("Buscando interacoes");
        txtStats2.setText("Buscando interacoes");
        txtStats3.setText("Buscando interacoes");
    }

    private void definirEstadoErro() {
        atualizarFalha(txtTitulo1, txtCategoria1, txtStats1, btnDetalhe1);
        atualizarFalha(txtTitulo2, txtCategoria2, txtStats2, btnDetalhe2);
        atualizarFalha(txtTitulo3, txtCategoria3, txtStats3, btnDetalhe3);
    }

    private void atualizarFalha(TextView titulo, TextView categoria, TextView stats, Button botao) {
        titulo.setText("Falha ao carregar");
        categoria.setText("Verifique o endereco da API");
        stats.setText("Sem dados");
        botao.setText("Tentar depois");
    }

    private void abrirDetalhe(int index) {
        if (index >= conteudos.size()) {
            Toast.makeText(this, "Conteudo ainda nao disponivel.", Toast.LENGTH_SHORT).show();
            return;
        }

        ConteudoItem conteudo = conteudos.get(index);
        Intent intent = new Intent(ConteudosActivity.this, DetalheConteudoActivity.class);
        intent.putExtra("titulo", conteudo.titulo);
        intent.putExtra("categoria", conteudo.getCategoria());
        intent.putExtra("descricao", conteudo.getDescricao());
        intent.putExtra("criador", conteudo.getNomeCriador());
        startActivity(intent);
    }
}
