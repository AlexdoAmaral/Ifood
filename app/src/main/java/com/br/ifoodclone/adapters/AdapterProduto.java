package com.br.ifoodclone.adapters;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.br.ifoodclone.R;
import com.br.ifoodclone.model.Produto;
import java.util.List;

public class AdapterProduto extends RecyclerView.Adapter<AdapterProduto.MyViewHolder>{

    // Criando os atributos
    private List<Produto> produtos;
    private Context context;
    // Criando a lista e context
    public AdapterProduto(List<Produto> produtos, Context context) {
        this.produtos = produtos;
        this.context = context;
    }

    // Criando a View e preenchendo a lista
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        View itemLista = LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_produto, parent, false);
        return new MyViewHolder(itemLista);
    }

    // Recuperando os dados
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int i) {
        Produto produto = produtos.get(i);
        holder.nome.setText(produto.getNome());
        holder.descricao.setText(produto.getDescricao());
        holder.valor.setText("R$ " + produto.getPreco());
    }

    // Fazendo a contagem de produtos
    @Override
    public int getItemCount() {
        return produtos.size();
    }

    // Linkando os atributos aos elementos gráficos
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView nome,descricao,valor;
        public MyViewHolder(View itemView) {
            super(itemView);
            nome = itemView.findViewById(R.id.textNomerefeicao);
            descricao = itemView.findViewById(R.id.textDescricaoRefeicao);
            valor = itemView.findViewById(R.id.textPreco);
        }
    }
}
