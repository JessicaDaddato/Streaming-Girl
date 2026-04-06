package com.pim.streamingmobile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class ConteudosActivity extends AppCompatActivity {

    Button btnVoltar;
    Button btnDetalhe1, btnDetalhe2, btnDetalhe3;
    private final List<ConteudoItem> conteudos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conteudos);

        btnDetalhe1 = findViewById(R.id.btnDetalhe1);
        btnDetalhe2 = findViewById(R.id.btnDetalhe2);
        btnDetalhe3 = findViewById(R.id.btnDetalhe3);

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

        atualizarBotao(btnDetalhe1, 0);
        atualizarBotao(btnDetalhe2, 1);
        atualizarBotao(btnDetalhe3, 2);
    }

    private void atualizarBotao(Button button, int index) {
        if (index < conteudos.size()) {
            button.setEnabled(true);
            button.setText(conteudos.get(index).titulo);
        } else {
            button.setEnabled(false);
            button.setText("Conteudo indisponivel");
        }
    }

    private void definirEstadoCarregando() {
        btnDetalhe1.setEnabled(false);
        btnDetalhe2.setEnabled(false);
        btnDetalhe3.setEnabled(false);
        btnDetalhe1.setText("Carregando...");
        btnDetalhe2.setText("Carregando...");
        btnDetalhe3.setText("Carregando...");
    }

    private void definirEstadoErro() {
        btnDetalhe1.setText("Falha ao carregar");
        btnDetalhe2.setText("Falha ao carregar");
        btnDetalhe3.setText("Falha ao carregar");
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
        startActivity(intent);
    }
}
