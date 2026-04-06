package com.pim.streamingmobile;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

public class DetalheConteudoActivity extends AppCompatActivity {
    private String tituloRecebido;
    private String criadorRecebido;
    private TextView txtCurtidas;
    private TextView txtComentarios;
    private TextView txtCriador;
    private EditText edtComentario;
    private LinearLayout layoutComentarios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalhe_conteudo);

        TextView titulo = findViewById(R.id.tituloConteudo);
        TextView categoria = findViewById(R.id.categoriaConteudo);
        TextView descricao = findViewById(R.id.descricaoConteudo);
        txtCurtidas = findViewById(R.id.txtCurtidasConteudo);
        txtComentarios = findViewById(R.id.txtComentariosConteudo);
        txtCriador = findViewById(R.id.txtCriadorConteudo);
        edtComentario = findViewById(R.id.edtComentario);
        layoutComentarios = findViewById(R.id.layoutComentarios);

        tituloRecebido = getIntent().getStringExtra("titulo");
        String categoriaRecebida = getIntent().getStringExtra("categoria");
        String descricaoRecebida = getIntent().getStringExtra("descricao");
        criadorRecebido = getIntent().getStringExtra("criador");

        titulo.setText(tituloRecebido);
        categoria.setText(categoriaRecebida);
        descricao.setText(descricaoRecebida);
        txtCriador.setText("Por " + criadorRecebido);
        atualizarPainelSocial();

        Button btnVoltar = findViewById(R.id.btnVoltar);

        btnVoltar.setOnClickListener(v -> finish());

        Button btnCurtir = findViewById(R.id.btnCurtir);
        Button btnComentar = findViewById(R.id.btnComentar);
        Button btnSeguir = findViewById(R.id.btnSeguir);

        btnCurtir.setOnClickListener(v -> {
            FavoritosManager.adicionarFavorito(tituloRecebido);
            int totalCurtidas = SocialManager.like(tituloRecebido);
            atualizarPainelSocial();
            Toast.makeText(this, "Voce curtiu. Total: " + totalCurtidas, Toast.LENGTH_SHORT).show();
        });

        btnComentar.setOnClickListener(v -> {
            String comentario = edtComentario.getText().toString().trim();
            if (comentario.isEmpty()) {
                Toast.makeText(this, "Escreva um comentario para publicar.", Toast.LENGTH_SHORT).show();
                return;
            }

            SocialManager.adicionarComentario(tituloRecebido, comentario);
            edtComentario.setText("");
            atualizarPainelSocial();
            Toast.makeText(this, "Comentario publicado!", Toast.LENGTH_SHORT).show();
        });

        btnSeguir.setOnClickListener(v -> {
            boolean novoFollow = SocialManager.seguirCriador(criadorRecebido);
            if (novoFollow) {
                Toast.makeText(this, "Voce agora segue " + criadorRecebido + "!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Voce ja segue " + criadorRecebido + ".", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void atualizarPainelSocial() {
        txtCurtidas.setText(SocialManager.getLikes(tituloRecebido) + " curtidas");
        txtComentarios.setText(SocialManager.getComentarios(tituloRecebido).size() + " comentarios");
        renderizarComentarios();
    }

    private void renderizarComentarios() {
        layoutComentarios.removeAllViews();

        for (String comentario : SocialManager.getComentarios(tituloRecebido)) {
            TextView item = new TextView(this);
            item.setText("• " + comentario);
            item.setTextColor(getResources().getColor(R.color.text_light));
            item.setTextSize(15f);
            item.setPadding(0, 0, 0, 18);
            layoutComentarios.addView(item);
        }
    }
}
