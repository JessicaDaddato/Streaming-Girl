package com.pim.streamingmobile;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

public class DetalheConteudoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_conteudo);

        TextView titulo = findViewById(R.id.tituloConteudo);
        TextView categoria = findViewById(R.id.categoriaConteudo);
        TextView descricao = findViewById(R.id.descricaoConteudo);

        String tituloRecebido = getIntent().getStringExtra("titulo");
        String categoriaRecebida = getIntent().getStringExtra("categoria");
        String descricaoRecebida = getIntent().getStringExtra("descricao");

        titulo.setText(tituloRecebido);
        categoria.setText(categoriaRecebida);
        descricao.setText(descricaoRecebida);

        Button btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> {
            finish();
        });

        Button btnCurtir = findViewById(R.id.btnCurtir);
        Button btnComentar = findViewById(R.id.btnComentar);
        Button btnSeguir = findViewById(R.id.btnSeguir);

        btnCurtir.setOnClickListener(v -> {
            FavoritosManager.adicionarFavorito(tituloRecebido);
            Toast.makeText(this, "Adicionado aos favoritos!", Toast.LENGTH_SHORT).show();
        });

        btnComentar.setOnClickListener(v -> {
            Toast.makeText(this, "Abrir comentarios...", Toast.LENGTH_SHORT).show();
        });

        btnSeguir.setOnClickListener(v -> {
            Toast.makeText(this, "Voce esta seguindo!", Toast.LENGTH_SHORT).show();
        });


    }
}