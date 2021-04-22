package com.br.ifoodnovo.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.br.ifoodnovo.R;
import com.br.ifoodnovo.helpers.UsuarioFirebase;
import com.br.ifoodnovo.model.Empresa;
import com.br.ifoodnovo.model.Produto;
import com.google.firebase.auth.FirebaseAuth;

public class NovoProdutoEmpresaActivity extends AppCompatActivity {

    private EditText editProdutoNome, editProdutoDescricao, editProdutoPreco;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        // Configurações Iniciais
        inicializaComponentes();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        // Configuração da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);

        // Para mostrar a seta de voltar para home
        // Necessário configurar o AndroidManifest
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void validarDadosProduto(View view){

        // Recuperar as informações do que foi digitado
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutoPreco.getText().toString();

        // VERIFICANDO SE OS DADOS FORAM INSERIDOS
        if (!nome.isEmpty()){
            if (!descricao.isEmpty()){
                if (!preco.isEmpty()){
                    
                    Produto produto = new Produto();
                    produto.setIdUsuario( idUsuarioLogado );
                    produto.setNome( nome );
                    produto.setDescricao( descricao );
                    produto.setPreco( Double.parseDouble( preco ));
                    produto.salvar();
                    finish();

                    exibirMensagem("Produto salvo com sucesso!");

                }else{
                    exibirMensagem("Digite um preço para o produto.");
                }
            }else{
                exibirMensagem("Digite uma descrição para o produto.");
            }
        }else{
            exibirMensagem("Digite um nome para o produto.");
        }
    }

    private void exibirMensagem (String texto){
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    private void inicializaComponentes(){
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutoPreco = findViewById(R.id.editProdutoPreco);
    }
}