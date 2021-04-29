package com.br.ifoodclone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;

import com.br.ifoodclone.R;
import com.br.ifoodclone.adapters.AdapterPedido;
import com.br.ifoodclone.adapters.AdapterProduto;
import com.br.ifoodclone.helpers.ConfiguracaoFirebase;
import com.br.ifoodclone.helpers.usuarioFirebase;
import com.br.ifoodclone.listener.RecyclerItemClickListener;
import com.br.ifoodclone.model.Pedido;
import com.br.ifoodclone.model.Produto;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class PedidoActivity extends AppCompatActivity {
    private RecyclerView recyclerPedido;
    private AdapterPedido adapterPedido;
    private List<Pedido> pedidos = new ArrayList<>();
    private AlertDialog dialog;
    private DatabaseReference firebaseRef;
    private String idEmpresa;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedido);
        inicializaComponentes();
        firebaseRef = ConfiguracaoFirebase.getFirebase();
        idEmpresa = usuarioFirebase.getIdUsuario();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Pedidos");
        setSupportActionBar(toolbar);
        // Para mostrar a seta de voltar para home(empresaActivity)
        // Necessário configurar no AndroidManisfest
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Configura recyclerview
        recyclerPedido.setLayoutManager( new LinearLayoutManager(this ) );
        recyclerPedido.setHasFixedSize( true );
        adapterPedido = new AdapterPedido(pedidos);
        recyclerPedido.setAdapter( adapterPedido );
        
        recuperarPedidos();

        recyclerPedido.addOnItemTouchListener(new RecyclerItemClickListener(this, recyclerPedido, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) { }

            @Override
            public void onLongItemClick(View view, int position) {
                    Pedido pedido = pedidos.get(position);
                    pedido.setStatus("Finalizado");
                    pedido.atualizarStatus();
            }

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        }));
    }

    private void recuperarPedidos() {
        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Carregando dados")
                .setCancelable(false).build();
        dialog.show();

        DatabaseReference pedidoRef = firebaseRef.child("pedidos").child(idEmpresa);

        Query pedidoPesquisa = pedidoRef.orderByChild("status")
                .equalTo("Confirmado");

        pedidoPesquisa.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pedidos.clear();
                if( dataSnapshot.getValue()!= null){
                    for(DataSnapshot ds: dataSnapshot.getChildren()){
                        Pedido pedido = ds.getValue(Pedido.class);
                        pedidos.add(pedido);
                    }
                    adapterPedido.notifyDataSetChanged();
                    dialog.dismiss();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void inicializaComponentes() {
        recyclerPedido = findViewById(R.id.recyclerPedidos);

    }
}