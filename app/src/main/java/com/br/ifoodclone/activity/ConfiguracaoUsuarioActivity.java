package com.br.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.br.ifoodclone.R;
import com.br.ifoodclone.helpers.ConfiguracaoFirebase;
import com.br.ifoodclone.helpers.usuarioFirebase;
import com.br.ifoodclone.model.Empresa;
import com.br.ifoodclone.model.Usuario;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ConfiguracaoUsuarioActivity extends AppCompatActivity {

    private EditText editUsuarioNome, editUsuarioEnderenco, editUsuarioCep, editUsuarioCidade;
    private ImageView imagemUsuarioPerfil;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    private static final int SELECAO_GALERIA = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao_usuario);
        inicializacomponentes();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = usuarioFirebase.getIdUsuario();
        // Configurações da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);
        // Para mostrar a seta de voltar para home
        // Necessário configurar no AndroidManifests
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configurando a imagem
        imagemUsuarioPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                if (i.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });
        recuperarDados();


    }
    private void recuperarDados() {
        DatabaseReference usuarioRef = firebaseRef.child("usuario").child(idUsuarioLogado);
        usuarioRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.getValue() != null) {
                    Usuario usuario = datasnapshot.getValue(Usuario.class);
                    editUsuarioNome.setText(usuario.getNome());
                    editUsuarioEnderenco.setText(usuario.getEndereco());
                    editUsuarioCep.setText(usuario.getCep());
                    editUsuarioCidade.setText(usuario.getCidade());
                    // recupera a imagem de perfil
                    urlImagemSelecionada = usuario.getUrlImagem();
                    if(urlImagemSelecionada != ""){
                        Picasso.get().load(urlImagemSelecionada).into(imagemUsuarioPerfil);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            Bitmap imagem = null;

            try {
                switch (requestCode) {
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images.Media.getBitmap(getContentResolver(), localImagem);
                        break;
                }
                if (imagem != null) {
                    imagemUsuarioPerfil.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Configurando o Storage

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("usuario")
                            .child(idUsuarioLogado + ".jpeg");

                    // Tarefa de Upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);



                    // Em caso de falha no upload
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracaoUsuarioActivity.this, "Erro ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    Uri url= task.getResult();
                                    urlImagemSelecionada =url.toString();

                                }
                            });

                            Toast.makeText(ConfiguracaoUsuarioActivity.this,"Sucesso ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void validarDadosUsuario(View View) {
        // Valida se os campos foram preenchidos
        String nome = editUsuarioNome.getText().toString();
        String endereco = editUsuarioEnderenco.getText().toString();
        String cep = editUsuarioCep.getText().toString();
        String cidade = editUsuarioCidade.getText().toString();

        if (!nome.isEmpty()) {
            if (!endereco.isEmpty()) {
                if (!cep.isEmpty()) {
                    if (!cidade.isEmpty()) {
                        // salvando os dados da empresa
                        Usuario usuario = new Usuario();
                        usuario.setIdUsuario(idUsuarioLogado);
                        usuario.setNome(nome);
                        usuario.setEndereco(endereco);
                        usuario.setCep(cep);
                        usuario.setCidade(cidade);
                        usuario.setUrlImagem(urlImagemSelecionada);
                        usuario.salvar();
                        exibirMensagem("Dados salvos com sucesso!");
                        finish();
                        startActivity(new Intent(this, HomeActivity.class));
                    } else {
                        exibirMensagem("Digite a cidade e o estado!");
                    }

                } else {
                    exibirMensagem("Digite o seu CEP!");
                }

            } else {
                exibirMensagem("Digite seu endereço!");
            }
        } else {
            exibirMensagem("Digite seu nome completo!");
        }
    }

    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializacomponentes() {
        editUsuarioNome = findViewById(R.id.editUsuarioNome);
        editUsuarioEnderenco = findViewById(R.id.editUsuarioEndereco);
        editUsuarioCep = findViewById(R.id.editUsuarioCep);
        editUsuarioCidade = findViewById(R.id.editusuarioCidade);
        imagemUsuarioPerfil = findViewById(R.id.imagemPerfilUsuario);
    }
}