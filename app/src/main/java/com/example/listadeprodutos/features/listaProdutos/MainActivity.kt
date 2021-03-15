package com.example.listadeprodutos.features.listaProdutos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadeprodutos.R
import com.example.listadeprodutos.features.ApplicationActivity
import com.example.listadeprodutos.features.produtoCRUD.ResumoCrud
import com.example.listadeprodutos.features.listaProdutos.model.Produto
import com.example.listadeprodutos.features.listaProdutos.model.ResumoAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        gerarRecyclerView()
        acoesClickActivity()
        setupActionBar("Lista de Produtos")
    }

    private fun setupActionBar(title : String, backNavigation : Boolean = false) {
        if(!backNavigation){
            toBack.visibility = View.GONE
        }

        toBack.setOnClickListener { onBackPressed() }
        txtTitle.text = title
    }

    private fun acoesClickActivity() {
        btnAddProduto.setOnClickListener {
            val intent = Intent(baseContext, ResumoCrud::class.java)
            startActivity(intent)
        }

        btnBusca.setOnClickListener { buscarProduto() }
    }

    private fun buscarProduto() {
        var listaProdutoFiltro : List<Produto>? = null

        loadingProduto.visibility = View.VISIBLE

        Thread(Runnable {
            Thread.sleep(1500)

            listaProdutoFiltro = ApplicationActivity.instance.dbProduto!!.buscarProduto(edtBusca.text.toString())

            runOnUiThread{
                val adapter = ResumoAdapter(this, listaProdutoFiltro!!) { acaoClickProduto(it) }
                recyclerViewProduto.adapter = adapter
                loadingProduto.visibility = View.GONE
            }
        }).start()
    }

    private fun gerarRecyclerView() {
        recyclerViewProduto.layoutManager = LinearLayoutManager(baseContext)
    }

    private fun acaoClickProduto(it : Int){
        val intent = Intent(baseContext, ResumoCrud::class.java)
        intent.putExtra("index", it)
        startActivity(intent)
    }

    override fun onResume() {
        buscarProduto()
        super.onResume()
    }


}