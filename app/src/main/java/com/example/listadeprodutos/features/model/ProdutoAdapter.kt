package com.example.listadeprodutos.features.model

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.listadeprodutos.R
import com.example.listadeprodutos.features.helpers.HelperBD
import com.example.listadeprodutos.features.produtoCRUD.DbBitmapUtility
import kotlinx.android.synthetic.main.resumo_item.view.*
import kotlin.math.pow
import kotlin.math.roundToInt

class ResumoAdapter (
    val context : Context,
    val listaProduto : List<Produto>,
    val acaoClick : ((Int) -> Unit)

): RecyclerView.Adapter<viewHolderResumo>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolderResumo {
        val view = LayoutInflater.from(context).inflate(R.layout.resumo_item, parent, false)
        return viewHolderResumo(view)
    }

    override fun getItemCount(): Int = listaProduto.size

    override fun onBindViewHolder(holder: viewHolderResumo, position: Int) {
        val itemProduto = listaProduto[position]
        val view = holder.itemView

        var novoLucro = itemProduto.lucroObtido
        var novoEstoque = itemProduto.quantidadeProduto

        val bd = HelperBD(context)

        // Setando textos e Imagem
        view.txtProduto.text = itemProduto.nomeProduto
        view.txtPrecoProduto.text = "R$ ${itemProduto.precoProduto}"
        view.txtQuantidadeProduto.text = "${itemProduto.quantidadeProduto}"
        view.txtLucroProduto.text = "R$ ${itemProduto.lucroObtido.roundTo(2)}"
        view.imgProduto.setImageBitmap(DbBitmapUtility.getImage(itemProduto.imgProduto))

        view.btnEditar.setOnClickListener { acaoClick(itemProduto.idProduto) }

        view.btnVendido.setOnClickListener {
            // Faz a verificação se é possivel realizar uma venda
            if(itemProduto.quantidadeProduto > 0){
                novoLucro += (itemProduto.precoProduto).roundTo(2)
                novoEstoque -= 1

                view.txtLucroProduto.text = "R$ ${novoLucro.roundTo(2)}"
                view.txtQuantidadeProduto.text = "${novoEstoque}"

                bd.atualizarLucro(novoLucro, novoEstoque, itemProduto.idProduto)

                itemProduto.lucroObtido = novoLucro
                itemProduto.quantidadeProduto = novoEstoque
            }else{
                Toast.makeText(context, "Sem estoque!", Toast.LENGTH_LONG).show()
            }
        }
    }

}


fun Double.roundTo(numFractionDigits: Int): Double {
    val factor = 10.0.pow(numFractionDigits.toDouble())
    return (this * factor).roundToInt() / factor
}

class viewHolderResumo(itemView : View) : RecyclerView.ViewHolder(itemView)