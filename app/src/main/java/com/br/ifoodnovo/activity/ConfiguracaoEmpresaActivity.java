package com.br.ifoodnovo.activity;

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
import com.br.ifoodnovo.R;
import com.br.ifoodnovo.helpers.ConfiguracaoFirebase;
import com.br.ifoodnovo.helpers.UsuarioFirebase;
import com.br.ifoodnovo.model.Empresa;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import java.io.ByteArrayOutputStream;
import javax.annotation.Nullable;

public class ConfiguracaoEmpresaActivity extends AppCompatActivity {

    private EditText editEmpresaNome, editEmpresaCategoria, editEmpresaTempo, editEmpresaTaxa;
    private ImageView imagemEmpresaPerfil;
    private String idUsuarioLogado;
    private String urlImagemSelecionada = "";

    private static final int SELECAO_GALERIA = 200;

    private StorageReference storageReference;
    private DatabaseReference firebaseRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configuracao_empresa);

        // Inicalizando os componentes
        inicializaComponentes();

        // Fazendo a chamada e configuração do Firebase
        storageReference = ConfiguracaoFirebase.getReferenciaStorage();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Configurações");
        setSupportActionBar(toolbar);

        // Para mostrar a seta de voltar para home
        // Necessário configurar o AndroidManifests
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configuração a imagem
        imagemEmpresaPerfil.setOnClickListener(new View.OnClickListener(){
            @Override
            // Acessando a galeria
            public void onClick(View view){
                Intent i = new Intent(
                        Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                );

                if (i.resolveActivity(getPackageManager()) != null){
                    startActivityForResult(i, SELECAO_GALERIA);
                }
            }
        });

        // Recuperando dados da empresa
        recuperarDadosEmpresa();
    }

    // Recuperando os dados da empresa
    private void recuperarDadosEmpresa() {

        // Acessando o nó empresas e os dados do usuário pelo id dele
        DatabaseReference empresaRef = firebaseRef
                .child("empresas")
                .child( idUsuarioLogado );

        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                // Inserindo os dados recuperados dentro dos editTexts
                if ( dataSnapshot.getValue() != null ) {
                    Empresa empresa = dataSnapshot.getValue( Empresa.class );
                    editEmpresaNome.setText(empresa.getNome());
                    editEmpresaCategoria.setText(empresa.getCategoria());
                    editEmpresaTempo.setText(empresa.getTempo());
                    editEmpresaTaxa.setText(empresa.getPrecoEntrega().toString());

                    // Recuperando a imagem
                    urlImagemSelecionada = empresa.getUrlImagem();
                        if ( urlImagemSelecionada != "") {
                            Picasso.get()
                                    .load(urlImagemSelecionada)
                                    .into(imagemEmpresaPerfil);
                        }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Validando os dados da empresa
    public void validarDadosEmpresa(View view){

        // Pegando as informações digitadas pelo usuário
        String nome = editEmpresaNome.getText().toString();
        String categoria = editEmpresaCategoria.getText().toString();
        String tempo = editEmpresaTempo.getText().toString();
        String taxa = editEmpresaTaxa.getText().toString();

        // VERIFICANDO SE OS DADOS FORAM INSERIDOS
        if (!nome.isEmpty()){
            if (!categoria.isEmpty()){
                if (!tempo.isEmpty()){
                    if (!taxa.isEmpty()){

                        // Salvando os dados da empresa
                        Empresa empresa = new Empresa();
                        empresa.setIdUsuario(idUsuarioLogado);
                        empresa.setNome(nome);
                        empresa.setCategoria(categoria);
                        empresa.setTempo(tempo);
                        empresa.setPrecoEntrega(Double.parseDouble(taxa)); // cast - conversão de tipos
                        empresa.setUrlImagem( urlImagemSelecionada );
                        empresa.salvar();
                        finish();

                    }else{
                        exibirMensagem("Digite a taxa de entrega!");
                    }
                }else{
                    exibirMensagem("Digite o tempo de entrega!");
                }
            }else{
                exibirMensagem("Digite a categoria da empresa!");
            }
        }else{
            exibirMensagem("Digite o nome da empresa!");
        }
    }

    // Mostrando as mensagens definidas acima
    private void exibirMensagem (String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK){
            Bitmap imagem = null;

            try {
                switch (requestCode){
                    // Selecionando a imagem na galeria
                    case SELECAO_GALERIA:
                        Uri localImagem = data.getData();
                        imagem = MediaStore.Images
                                .Media
                                .getBitmap(
                                        getContentResolver(),
                                        localImagem
                                );
                        break;
                }

                // Verifica se a imagem foi escolhida e já faz upload
                if (imagem != null){
                    imagemEmpresaPerfil.setImageBitmap(imagem);

                    // Preparando as informações da imagem selecionada
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    imagem.compress(Bitmap.CompressFormat.JPEG, 70, baos);
                    byte[] dadosImagem = baos.toByteArray();

                    // Configurando o Storage
                    final StorageReference imagemRef = storageReference
                            .child("imagens")
                            .child("empresas")
                            .child(idUsuarioLogado + "jpeg");

                    // Retorna o objeto que irá controlar o upload
                    UploadTask uploadTask = imagemRef.putBytes(dadosImagem);

                    // Em caso de falha no upload da imagem
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            // Mostrando mensagem de erro para o usuário
                            Toast.makeText(ConfiguracaoEmpresaActivity.this,
                                    "Erro ao fazer o upload da imagem",
                                    Toast.LENGTH_SHORT).show();
                        }
                        // Em caso de sucesso no Upload da imagem
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            // Para versões antigas do Firebase
                            // taskSnapshot.getDownloadUrl();

                            // Como estamos trabalhando com as versões mais novas, precisamos
                            // utilizar o imagemRef e tbm definir como final na linha 180
                            imagemRef.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                               @Override
                               public void onComplete(@NonNull Task<Uri> task) {
                                    Uri url = task.getResult();
                               }
                            });

                            // Mostrando mensagem de sucesso para o usuário
                            Toast.makeText(ConfiguracaoEmpresaActivity.this,
                                "Sucesso ao fazer upload da imagem",
                                 Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            } catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    // Inicializando todos os componentes
    private void inicializaComponentes(){
        editEmpresaNome = findViewById(R.id.editEmpresaNome);
        editEmpresaCategoria = findViewById(R.id.editEmpresaCategoria);
        editEmpresaTempo = findViewById(R.id.editEmpresaTempoEntrega);
        editEmpresaTaxa = findViewById(R.id.editEmpresaTaxaEntrega);
        imagemEmpresaPerfil = findViewById(R.id.image_perfil_empresa);
    }
}