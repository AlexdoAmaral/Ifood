package com.br.ifoodclone.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.br.ifoodclone.R;
import com.br.ifoodclone.helpers.usuarioFirebase;
import com.br.ifoodclone.model.Empresa;
import com.br.ifoodclone.model.Produto;


public class NovoProdutoEmpresaActivity extends AppCompatActivity {
    private EditText editProdutoNome,editProdutoDescricao, editProdutopreco;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_novo_produto_empresa);

        inicializaComponentes();
        idUsuarioLogado= usuarioFirebase.getIdUsuario();

        // Configurações da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Novo Produto");
        setSupportActionBar(toolbar);
        // Para mostrar a seta de voltar para home(empresaActivity)
        // Necessário configurar no AndroidManisfest
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }
    public void validarDadosProduto(View view){
        // Valida se os campos foram preenchidos
        String nome = editProdutoNome.getText().toString();
        String descricao = editProdutoDescricao.getText().toString();
        String preco = editProdutopreco.getText().toString();


        if (!nome.isEmpty()) {
            if (!descricao.isEmpty()) {
                if (!preco.isEmpty()) {
                        Produto produto = new Produto();
                        produto.setIdUsuario(idUsuarioLogado);
                        produto.setNome(nome);
                        produto.setDescricao(descricao);
                        produto.setPreco(Double.parseDouble(preco)); // cast conversão de tipos
                        produto.salvar();
                        exibirMensagem("Dados salvos com sucesso!");
                        finish();

                } else {
                    exibirMensagem("Digite o valor do produto!");
                }

            } else {
                exibirMensagem("Digite a descrição do produto!");
            }
        } else {
            exibirMensagem("Digite um nome do produto!");
        }
    }


    private void exibirMensagem(String texto){
        Toast.makeText(this,texto,Toast.LENGTH_SHORT).show();
    }

    private void inicializaComponentes(){
        editProdutoNome = findViewById(R.id.editProdutoNome);
        editProdutoDescricao = findViewById(R.id.editProdutoDescricao);
        editProdutopreco = findViewById(R.id.editProdutoPreco);
    }
}