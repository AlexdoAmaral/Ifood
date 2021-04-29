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

import java.awt.font.NumericShaper;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.UUID;

public class ConfiguracoesEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo, editEmpresaTaxa;
    private ImageView imagemEmpresaPerfil;
    private StorageReference storageReference;
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";
    private static final int SELECAO_GALERIA = 2000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracoes_empresa);

        inicializacomponentes();

        storageReference = ConfiguracaoFirebase.getFirebaseStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = usuarioFirebase.getIdUsuario();


        // Configurações da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações empresa");
        setSupportActionBar(toolbar);
        // Para mostrar a seta de voltar para home
        // Necessário configurar no AndroidManifests
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        // Configurando a imagem
        imagemEmpresaPerfil.setOnClickListener(new View.OnClickListener() {
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
        DatabaseReference empresaRef = firebaseRef.child("empresas").child(idUsuarioLogado);
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot datasnapshot) {
                if (datasnapshot.getValue() != null) {
                    Empresa empresa = datasnapshot.getValue(Empresa.class);
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaCategoria.setText(empresa.getCategoria());
                    editEmpresaTempo.setText(empresa.getTempo());
                    editEmpresaTaxa.setText(empresa.getPrecoEntrega().toString());
                    // recupera a imagem de perfil
                    urlImagemSelecionada = empresa.getUrlImagem();
                    if(urlImagemSelecionada != ""){
                        Picasso.get().load(urlImagemSelecionada).into(imagemEmpresaPerfil);
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
                    imagemEmpresaPerfil.setImageBitmap(imagem);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Configurando o Storage

                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + ".jpeg");
                    //String nomeArquivo = UUID.randomUUID().toString();
                    // final StorageReference imagemRef = imagens.child(nomeArquivo + ".jpeg");

                    // Tarefa de Upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);



                    // Em caso de falha no upload
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ConfiguracoesEmpresaActivity.this, "Erro ao fazer o upload da imagem", Toast.LENGTH_SHORT).show();
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

                            Toast.makeText(ConfiguracoesEmpresaActivity.this,"Sucesso ao fazer upload da imagem",Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void validarDadosEmpresa(View View) {
        // Valida se os campos foram preenchidos
        String nome = editEmpresaNome.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();
        String taxa = editEmpresaTaxa.getText().toString();

        if (!nome.isEmpty()) {
            if (!categoria.isEmpty()) {
                if (!tempo.isEmpty()) {
                    if (!taxa.isEmpty()) {
                        // salvando os dados da empresa
                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idUsuarioLogado);
                        empresa.setNome(nome);
                        empresa.setCategoria(categoria);
                        empresa.setTempo(tempo);
                        empresa.setPrecoEntrega(Double.parseDouble(taxa)); // cast conversão de tipos
                        empresa.setUrlImagem(urlImagemSelecionada);
                        empresa.salvar();
                        exibirMensagem("Dados salvos com sucesso!");
                        finish();
                        startActivity(new Intent(this, EmpresaActivity.class));
                    } else {
                        exibirMensagem("Digite uma taxa de entrega!");
                    }

                } else {
                    exibirMensagem("Digite um tempo de entrega!");
                }

            } else {
                exibirMensagem("Digite a categoria da empresa!");
            }
        } else {
            exibirMensagem("Digite um nome para empresa!");
        }
    }


    private void exibirMensagem(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializacomponentes() {
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaCategoria = findViewById(R.id.editEmpresaCategoria);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempoEntrega);
        editEmpresaTaxa = findViewById(R.id.editEmpresaTaxaEntrega);
        imagemEmpresaPerfil = findViewById(R.id.imagemPerfilEmpresa);
    }

    }