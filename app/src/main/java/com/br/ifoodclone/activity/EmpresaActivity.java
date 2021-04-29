package com.br.ifoodclone.activity;

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

import com.br.ifoodclone.R;
import com.br.ifoodclone.adapters.AdapterProduto;
import com.br.ifoodclone.helpers.ConfiguracaoFirebase;
import com.br.ifoodclone.helpers.usuarioFirebase;
import com.br.ifoodclone.listener.RecyclerItemClickListener;
import com.br.ifoodclone.model.Produto;
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

        inicializaComponentes();
        autenticacao= ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idUsuarioLogado = usuarioFirebase.getIdUsuario();

        // Configurações da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);
        // Configura recyclerview
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
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_empresa, menu);

        return super.onCreateOptionsMenu(menu);
    }
    // Verificando a opção selecionada no menu pelo usuário
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:
                abrirCOnfiguracoes();
                break;
            case R.id.menuNovoProduto:
                abrirNovoProduto();
                break;
            case R.id.menuPedidos:
                abrirPedidos();
                break;
        }

        return super.onOptionsItemSelected(item);
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

    private void abrirCOnfiguracoes() {
        startActivity(new Intent(this,ConfiguracoesEmpresaActivity.class));
    }
    private void abrirNovoProduto() {
        startActivity(new Intent(this,NovoProdutoEmpresaActivity.class));
    }
    private void abrirPedidos() {
        startActivity(new Intent(this,PedidoActivity.class));
    }

    private void deslogarUsuario() {
            try{
                autenticacao.signOut();
                startActivity(new Intent(this,AutenticacaoActivity.class));
            }catch(Exception e){
                e.printStackTrace();
            }
    }
    private void inicializaComponentes() {
        recyclerProdutos = findViewById(R.id.recyclerEmpresas);
    }
}