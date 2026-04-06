package com.pim.streamingmobile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.widget.EditText;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

public class PlaylistsActivity extends AppCompatActivity {

    Button btnVoltarPlaylists, btnPlaylist1, btnPlaylist2, btnPlaylist3;
    Button btnCriarPlaylist, btnAtualizarPlaylist, btnExcluirPlaylist, btnEnviarArquivo;
    EditText edtNomePlaylist;
    LinearLayout layoutFavoritos, layoutPlaylistsApi;
    TextView txtPlaylistSelecionada, txtStatusCrud, txtArquivoSelecionado;
    private final List<PlaylistItem> playlists = new ArrayList<>();
    private PlaylistItem playlistSelecionada;
    private static final int USUARIO_PADRAO_ID = 1;
    private final ActivityResultLauncher<String> seletorArquivo =
            registerForActivityResult(new ActivityResultContracts.GetContent(), this::processarArquivoSelecionado);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlists);

        btnVoltarPlaylists = findViewById(R.id.btnVoltarPlaylists);
        btnPlaylist1 = findViewById(R.id.btnPlaylist1);
        btnPlaylist2 = findViewById(R.id.btnPlaylist2);
        btnPlaylist3 = findViewById(R.id.btnPlaylist3);
        btnCriarPlaylist = findViewById(R.id.btnCriarPlaylist);
        btnAtualizarPlaylist = findViewById(R.id.btnAtualizarPlaylist);
        btnExcluirPlaylist = findViewById(R.id.btnExcluirPlaylist);
        btnEnviarArquivo = findViewById(R.id.btnEnviarArquivo);
        edtNomePlaylist = findViewById(R.id.edtNomePlaylist);
        txtPlaylistSelecionada = findViewById(R.id.txtPlaylistSelecionada);
        txtStatusCrud = findViewById(R.id.txtStatusCrud);
        txtArquivoSelecionado = findViewById(R.id.txtArquivoSelecionado);
        layoutFavoritos = findViewById(R.id.layoutFavoritos);
        layoutPlaylistsApi = findViewById(R.id.layoutPlaylistsApi);

        btnVoltarPlaylists.setOnClickListener(v -> finish());
        btnCriarPlaylist.setOnClickListener(v -> criarPlaylist());
        btnAtualizarPlaylist.setOnClickListener(v -> atualizarPlaylist());
        btnExcluirPlaylist.setOnClickListener(v -> excluirPlaylist());
        btnEnviarArquivo.setOnClickListener(v -> abrirSeletorArquivo());

        carregarPlaylists();
        renderizarFavoritos();
        atualizarEstadoCrud();
        txtArquivoSelecionado.setText(UploadsManager.getUltimoUpload(this));
    }

    private void carregarPlaylists() {
        definirBotoesComoCarregando();

        ApiClient.getPlaylists(new ApiClient.ApiCallback<List<PlaylistItem>>() {
            @Override
            public void onSuccess(List<PlaylistItem> result) {
                runOnUiThread(() -> atualizarPlaylists(result));
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    Toast.makeText(PlaylistsActivity.this,
                            "Nao foi possivel carregar as playlists.",
                            Toast.LENGTH_LONG).show();
                    btnPlaylist1.setText("Falha ao carregar");
                    btnPlaylist2.setText("Falha ao carregar");
                    btnPlaylist3.setText("Falha ao carregar");
                });
            }
        });
    }

    private void definirBotoesComoCarregando() {
        btnPlaylist1.setEnabled(false);
        btnPlaylist2.setEnabled(false);
        btnPlaylist3.setEnabled(false);
        btnPlaylist1.setText("Carregando...");
        btnPlaylist2.setText("Carregando...");
        btnPlaylist3.setText("Carregando...");
    }

    private void atualizarPlaylists(List<PlaylistItem> playlists) {
        this.playlists.clear();
        if (playlists != null) {
            this.playlists.addAll(playlists);
        }

        configurarBotaoPlaylist(btnPlaylist1, playlists, 0);
        configurarBotaoPlaylist(btnPlaylist2, playlists, 1);
        configurarBotaoPlaylist(btnPlaylist3, playlists, 2);

        if (playlistSelecionada != null) {
            playlistSelecionada = localizarPlaylistPorId(playlistSelecionada.id);
        }

        renderizarPlaylistsApi();
        atualizarEstadoCrud();
    }

    private void configurarBotaoPlaylist(Button botao, List<PlaylistItem> playlists, int index) {
        if (playlists != null && index < playlists.size()) {
            PlaylistItem playlist = playlists.get(index);
            botao.setEnabled(true);
            botao.setText(playlist.nome);
            botao.setOnClickListener(v -> selecionarPlaylist(playlist));
            return;
        }

        botao.setEnabled(false);
        botao.setText("Playlist indisponivel");
    }

    private void selecionarPlaylist(PlaylistItem playlist) {
        playlistSelecionada = playlist;
        edtNomePlaylist.setText(playlist.nome);
        txtPlaylistSelecionada.setText("Selecionada: " + playlist.nome);
        txtStatusCrud.setText("Playlist pronta para editar ou excluir.");
    }

    private PlaylistItem localizarPlaylistPorId(int playlistId) {
        for (PlaylistItem item : playlists) {
            if (item.id == playlistId) {
                return item;
            }
        }
        return null;
    }

    private void criarPlaylist() {
        String token = AuthManager.getToken(this);
        String nome = edtNomePlaylist.getText().toString().trim();

        if (token.isBlank()) {
            Toast.makeText(this, "Faça login antes de criar uma playlist.", Toast.LENGTH_LONG).show();
            return;
        }

        if (nome.isEmpty()) {
            Toast.makeText(this, "Informe o nome da playlist.", Toast.LENGTH_SHORT).show();
            return;
        }

        txtStatusCrud.setText("Criando playlist...");
        ApiClient.createPlaylist(token, nome, USUARIO_PADRAO_ID, new ApiClient.ApiCallback<PlaylistItem>() {
                    @Override
                    public void onSuccess(PlaylistItem result) {
                        runOnUiThread(() -> {
                    txtStatusCrud.setText("Playlist criada com sucesso.");
                    edtNomePlaylist.setText("");
                    carregarPlaylists();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    txtStatusCrud.setText("Falha ao criar playlist.");
                    Toast.makeText(PlaylistsActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void atualizarPlaylist() {
        String token = AuthManager.getToken(this);
        String nome = edtNomePlaylist.getText().toString().trim();

        if (token.isBlank()) {
            Toast.makeText(this, "Faça login antes de atualizar uma playlist.", Toast.LENGTH_LONG).show();
            return;
        }

        if (playlistSelecionada == null) {
            Toast.makeText(this, "Selecione uma playlist para atualizar.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (nome.isEmpty()) {
            Toast.makeText(this, "Informe o novo nome da playlist.", Toast.LENGTH_SHORT).show();
            return;
        }

        txtStatusCrud.setText("Atualizando playlist...");
        ApiClient.updatePlaylist(token, playlistSelecionada.id, nome, USUARIO_PADRAO_ID,
                new ApiClient.ApiCallback<Void>() {
                    @Override
                    public void onSuccess(Void result) {
                        runOnUiThread(() -> {
                            txtStatusCrud.setText("Playlist atualizada com sucesso.");
                            carregarPlaylists();
                        });
                    }

                    @Override
                    public void onError(String message) {
                        runOnUiThread(() -> {
                            txtStatusCrud.setText("Falha ao atualizar playlist.");
                            Toast.makeText(PlaylistsActivity.this, message, Toast.LENGTH_LONG).show();
                        });
                    }
                });
    }

    private void excluirPlaylist() {
        String token = AuthManager.getToken(this);

        if (token.isBlank()) {
            Toast.makeText(this, "Faça login antes de excluir uma playlist.", Toast.LENGTH_LONG).show();
            return;
        }

        if (playlistSelecionada == null) {
            Toast.makeText(this, "Selecione uma playlist para excluir.", Toast.LENGTH_SHORT).show();
            return;
        }

        txtStatusCrud.setText("Excluindo playlist...");
        ApiClient.deletePlaylist(token, playlistSelecionada.id, new ApiClient.ApiCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                runOnUiThread(() -> {
                    playlistSelecionada = null;
                    edtNomePlaylist.setText("");
                    txtPlaylistSelecionada.setText("Nenhuma playlist selecionada.");
                    txtStatusCrud.setText("Playlist excluida com sucesso.");
                    carregarPlaylists();
                });
            }

            @Override
            public void onError(String message) {
                runOnUiThread(() -> {
                    txtStatusCrud.setText("Falha ao excluir playlist.");
                    Toast.makeText(PlaylistsActivity.this, message, Toast.LENGTH_LONG).show();
                });
            }
        });
    }

    private void atualizarEstadoCrud() {
        boolean logado = AuthManager.isLoggedIn(this);
        btnCriarPlaylist.setEnabled(logado);
        btnAtualizarPlaylist.setEnabled(logado);
        btnExcluirPlaylist.setEnabled(logado);

        if (!logado) {
            txtStatusCrud.setText("Entre para criar, editar ou excluir playlists.");
            txtPlaylistSelecionada.setText("Nenhuma playlist selecionada.");
            return;
        }

        if (playlistSelecionada == null) {
            txtPlaylistSelecionada.setText("Nenhuma playlist selecionada.");
        }
    }

    private void renderizarPlaylistsApi() {
        layoutPlaylistsApi.removeAllViews();

        if (playlists.isEmpty()) {
            TextView vazio = new TextView(this);
            vazio.setText("Nenhuma playlist encontrada.");
            vazio.setTextColor(getResources().getColor(R.color.text_soft));
            layoutPlaylistsApi.addView(vazio);
            return;
        }

        for (PlaylistItem playlist : playlists) {
            Button botao = new Button(this);
            String quantidade = playlist.getQuantidadeItens() == 1
                    ? "1 conteudo"
                    : playlist.getQuantidadeItens() + " conteudos";
            botao.setText(playlist.nome + "  •  " + quantidade + "  •  ID " + playlist.id);
            botao.setTextColor(getResources().getColor(R.color.brown_dark));
            botao.setBackgroundResource(R.drawable.bg_botao_rosa);
            botao.setAllCaps(false);
            botao.setOnClickListener(v -> selecionarPlaylist(playlist));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(0, 0, 0, 16);
            botao.setLayoutParams(params);
            botao.setPadding(24, 24, 24, 24);

            layoutPlaylistsApi.addView(botao);
        }
    }

    private void abrirSeletorArquivo() {
        seletorArquivo.launch("*/*");
    }

    private void processarArquivoSelecionado(Uri uri) {
        if (uri == null) {
            txtArquivoSelecionado.setText("Nenhum arquivo selecionado.");
            return;
        }

        String nomeArquivo = resolverNomeArquivo(uri);
        UploadsManager.registrarUpload(this, nomeArquivo);
        txtArquivoSelecionado.setText("Arquivo selecionado: " + nomeArquivo);
        Toast.makeText(this, "Arquivo pronto para envio.", Toast.LENGTH_SHORT).show();
    }

    private String resolverNomeArquivo(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null);
        if (cursor != null) {
            int columnIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (columnIndex >= 0) {
                try {
                    if (cursor.moveToFirst()) {
                        return cursor.getString(columnIndex);
                    }
                } finally {
                    cursor.close();
                }
            }
            cursor.close();
        }

        String path = uri.getLastPathSegment();
        return path == null || path.isBlank() ? "Arquivo selecionado" : path;
    }

    private void renderizarFavoritos() {
        if (FavoritosManager.getFavoritos().isEmpty()) {
            Button aviso = new Button(this);
            aviso.setText("Voce ainda nao curtiu nenhum conteudo");
            aviso.setEnabled(false);
            aviso.setTextColor(getResources().getColor(R.color.brown_dark));
            aviso.setBackgroundTintList(getColorStateList(R.color.pink_soft));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );

            params.setMargins(0, 0, 0, 16);
            aviso.setLayoutParams(params);

            layoutFavoritos.addView(aviso);
        } else {
            for (String favorito : FavoritosManager.getFavoritos()) {
                Button botaoFavorito = new Button(this);
                botaoFavorito.setText(favorito);
                botaoFavorito.setTextColor(getResources().getColor(R.color.brown_dark));
                botaoFavorito.setBackgroundResource(R.drawable.bg_botao_rosa);

                botaoFavorito.setTextAlignment(Button.TEXT_ALIGNMENT_CENTER);
                botaoFavorito.setAllCaps(false);

                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                );

                params.setMargins(0, 0, 0, 16);
                botaoFavorito.setLayoutParams(params);

                botaoFavorito.setPadding(24, 24, 24, 24);

                botaoFavorito.setOnClickListener(v -> {
                    Intent intent = new Intent(PlaylistsActivity.this, DetalheConteudoActivity.class);

                    if (favorito.equals("Code e Pink")) {
                        intent.putExtra("titulo", "Code e Pink");
                        intent.putExtra("categoria", "Tecnologia • Lifestyle");
                        intent.putExtra("descricao", "Conteudo perfeito para quem ama tecnologia com estilo.");
                    } else if (favorito.equals("Soft Life Diaries")) {
                        intent.putExtra("titulo", "Soft Life Diaries");
                        intent.putExtra("categoria", "Vlog • Rotina");
                        intent.putExtra("descricao", "Rotina leve, estetica e inspiradora do dia a dia.");
                    } else if (favorito.equals("Late Night Playlist")) {
                        intent.putExtra("titulo", "Late Night Playlist");
                        intent.putExtra("categoria", "Musica • Relax");
                        intent.putExtra("descricao", "Playlist perfeita para relaxar a noite.");
                    }

                    startActivity(intent);
                });

                layoutFavoritos.addView(botaoFavorito);
            }
        }
    }
}
