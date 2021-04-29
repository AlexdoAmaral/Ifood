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

import com.br.ifoodclone.R;
import com.br.ifoodclone.adapters.AdapterEmpresa;
import com.br.ifoodclone.adapters.AdapterProduto;
import com.br.ifoodclone.helpers.ConfiguracaoFirebase;
import com.br.ifoodclone.listener.RecyclerItemClickListener;
import com.br.ifoodclone.model.Empresa;
import com.br.ifoodclone.model.Produto;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.miguelcatalan.materialsearchview.MaterialSearchView;

import java.security.cert.PolicyNode;
import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    private FirebaseAuth autenticacao;
    private MaterialSearchView searchView;
    private RecyclerView recyclerEmpresa;
    private List<Empresa> empresas = new ArrayList<>();
    private DatabaseReference firebaseRef;
    private AdapterEmpresa adapterEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        inicializaComponente();
        autenticacao= ConfiguracaoFirebase.getFirebaseAutenticacao();
        firebaseRef = ConfiguracaoFirebase.getFirebase();


        // Configurações da toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Ifood");
        setSupportActionBar(toolbar);

        recyclerEmpresa.setLayoutManager( new LinearLayoutManager(this ) );
        recyclerEmpresa.setHasFixedSize( true );
        adapterEmpresa = new AdapterEmpresa( empresas);
        recyclerEmpresa.setAdapter( adapterEmpresa );

        recuperarEmpresas();
        searchView.setHint("Pesquisar restaurantes...");
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
           public boolean onQueryTextSubmit(String query) {
                return false;
            }

           @Override
            public boolean onQueryTextChange(String newText) {
                pesquisarEmpresas(newText);
                return true;
            }
       });

        // Configurar evento de clique
        recyclerEmpresa.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerEmpresa, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        Empresa empresaSelecionada = empresas.get(position);
                        Intent i = new Intent(HomeActivity.this,CardapioActivity.class);
                        i.putExtra("empresa",empresaSelecionada);
                        startActivity(i);
                    }

                    @Override
                    public void onLongItemClick(View view, int position) { }

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) { }
                })
        );

    }

   private void pesquisarEmpresas(String minhaPesquisa){
        DatabaseReference empresaRef = firebaseRef.child("empresas");

       Query query = empresaRef.orderByChild("nome")
               .startAt(minhaPesquisa + "\uf8ff");

       query.addListenerForSingleValueEvent(new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot datasnapshot) {
               empresas.clear();
               for (DataSnapshot ds: datasnapshot.getChildren()){

               }
           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       });
   }



    private void recuperarEmpresas(){
        DatabaseReference empresaRef = firebaseRef.child("empresas");
        empresaRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                empresas.clear(); // Limpando a lista antes de começarmos

                // Laço responsável por trazer todos os produtos da empresa
                for (DataSnapshot ds: snapshot.getChildren()) {
                    empresas.add( ds.getValue( Empresa.class ) );

                }

                adapterEmpresa.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_usuario, menu);

        // Configurando a pesquisa
        MenuItem item = menu.findItem(R.id.menuPesquisa);
        searchView.setMenuItem(item);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuSair:
                deslogarUsuario();
                break;
            case R.id.menuConfiguracoes:
                abrirConfiguracoes();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void abrirConfiguracoes() {
        startActivity(new Intent(this,ConfiguracaoUsuarioActivity.class));
    }


    private void deslogarUsuario() {
        try{
            autenticacao.signOut();
            finish();
            startActivity(new Intent(this,AutenticacaoActivity.class));
        }catch(Exception e){
            e.printStackTrace();
        }

    }

    private void inicializaComponente(){
        recyclerEmpresa = findViewById(R.id.ReciclerEmpresa);
        searchView = findViewById(R.id.materialsearchview);
    }


}