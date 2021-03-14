package com.example.listadeprodutos.features.listaProdutos

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.listadeprodutos.R
import com.example.listadeprodutos.features.helpers.HelperBD
import com.example.listadeprodutos.features.produtoCRUD.ResumoCrud
import com.example.listadeprodutos.features.model.Produto
import com.example.listadeprodutos.features.model.ResumoAdapter
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*

class MainActivity : AppCompatActivity() {

    lateinit var bdResumo : HelperBD

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bdResumo = HelperBD(this)

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
        val listaProdutoFiltro : List<Produto> = bdResumo.buscarProduto(edtBusca.text.toString())

        val adapter = ResumoAdapter(this, listaProdutoFiltro) { acaoClickProduto(it) }
        recyclerViewProduto.adapter = adapter
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