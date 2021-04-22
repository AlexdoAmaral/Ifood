package com.br.ifoodnovo.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.br.ifoodnovo.R;
import com.br.ifoodnovo.adapters.AdapterProduto;
import com.br.ifoodnovo.helpers.ConfiguracaoFirebase;
import com.br.ifoodnovo.helpers.UsuarioFirebase;
import com.br.ifoodnovo.listener.RecyclerItemClickListener;
import com.br.ifoodnovo.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class EmpresaActivity extends AppCompatActivity {

    private FirebaseAuth autenticacao;
    private RecyclerView recyclerProdutos;
    private AdapterProduto adapterProduto;
    private List<Produto> produtos = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private String idUsuarioLogado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empresa);

        // Inicializando componentes
        inicializaComponentes();
        autenticacao = ConfiguracaoFirebase.getReferenciaAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = UsuarioFirebase.getIdUsuario();

        //Configurações da Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood - Empresa");
        setSupportActionBar(toolbar);

        // Configurações RecyclerView
        recyclerProdutos.setLayoutManager( new LinearLayoutManager(this ) );
        recyclerProdutos.setHasFixedSize( true );
        adapterProduto = new AdapterProduto( produtos, this );
        recyclerProdutos.setAdapter( adapterProduto );

        // Recuperando os produtos do Firebase
        recuperarProdutos();

        // Adicionando evento de clique ao recyclerview
        recyclerProdutos.addOnItemTouchListener(
                new RecyclerItemClickListener(
                        this,
                        recyclerProdutos,
                        new RecyclerItemClickListener.OnItemClickListener() {
                            @Override
                            public void onItemClick(View view, int position) {}

                            @Override
                            public void onLongItemClick(View view, int position) {
                                Produto produtoSelecionado = produtos.get( position );
                                produtoSelecionado.remover();

                                Toast.makeText(
                                        EmpresaActivity.this,
                                        "Produto removido com sucesso!", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {}
                        }
                )
        );
    }

    // Criando as opções do menu
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }

    //Verificando a opção selecionada pelo usuário
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId()){
            case R.id.menuSair :
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes :
                abrirConfiguracoes();
                break;
            case R.id.menuNovoProduto :
                abrirNovoProduto();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    // Método para deslogar o usuário
    private void deslogarUsuario(){
        try {
            autenticacao.signOut();
            startActivity(new Intent(EmpresaActivity.this, AutenticacaoActivity.class));
            finish();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // Método responsável por abrir as configurações
    private void abrirConfiguracoes(){
        startActivity(new Intent(EmpresaActivity.this, ConfiguracaoEmpresaActivity.class));
    }

    // Método responsável por abrir um Novo Produto
    private void abrirNovoProduto(){
        startActivity(new Intent(EmpresaActivity.this, NovoProdutoEmpresaActivity.class));
    }

    // Método responsável por recuperar os Produtos
    private void recuperarProdutos() {
        DatabaseReference produtosRef = firebaseRef
                .child("produtos")
                .child( idUsuarioLogado );

        produtosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                produtos.clear(); // Limpando a lista antes de começarmos

                // Laço responsável por trazer todos os produtos da empresa
                for (DataSnapshot ds: snapshot.getChildren()) {
                    produtos.add( ds.getValue( Produto.class ) );
                }

                adapterProduto.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    // Método responsável por iniciar os componentes
    private void inicializaComponentes() {
        recyclerProdutos = findViewById(R.id.recyclerProdutos);
    }
}