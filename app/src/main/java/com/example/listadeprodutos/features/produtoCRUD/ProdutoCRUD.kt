package com.example.listadeprodutos.features.produtoCRUD

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Bitmap.CompressFormat
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.listadeprodutos.features.ApplicationActivity
import com.example.listadeprodutos.R
import com.example.listadeprodutos.features.listaProdutos.model.Produto
import kotlinx.android.synthetic.main.activity_edit_produto.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.toolbar.*
import java.io.ByteArrayOutputStream


class ResumoCrud : AppCompatActivity() {

    var index : Int = -1
    lateinit var filepath : Uri
    var imgProduto : ByteArray? = null
    lateinit var produto : Produto

    lateinit var loadingProduto: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_produto)

        setupActionBar("Editando Produto", true)

        loadingProduto = findViewById(R.id.loadingProduto)

        index = intent.getIntExtra("index", -1)

        // Configurando a tela para ter ou não ter botão de exclusão
        if(index == -1){
            btnExcluirProduto.visibility = View.GONE
        }else{
            adicionarTextos()
        }

        acoesClickActivity()
    }

    // Configurando ActionBar
    private fun setupActionBar(title : String, backNavigation : Boolean = false) {
        if(!backNavigation){
            toBack.visibility = View.GONE
        }else{
            toBack.setOnClickListener { onBackPressed() }
        }

        txtTitle.text = title
    }

    private fun adicionarTextos() {
        var lista : List<Produto>? = ApplicationActivity.instance.dbProduto!!.buscarProduto("$index", true)
        produto = lista!!.getOrNull(0) ?: return

        loadingProduto.visibility = View.VISIBLE
        Thread(Runnable {
            Thread.sleep(1500)

            lista = ApplicationActivity.instance.dbProduto!!.buscarProduto("$index", true)
            produto = lista!!.getOrNull(0) ?: return@Runnable

            runOnUiThread {
                edtNomeProduto.setText(produto.nomeProduto)
                edtDescProduto.setText(produto.descricaoProduto)
                edtPrecoProduto.setText(produto.precoProduto.toString())
                edtQuantidade.setText(produto.quantidadeProduto.toString())

                imgChoose.setImageBitmap(DbBitmapUtility.getImage(produto.imgProduto))

                loadingProduto.visibility = View.GONE
            }
        }).start()

        imgProduto = produto.imgProduto
    }

    private fun acoesClickActivity() {
        btnSalvarProduto.setOnClickListener { salvarProduto() }
        btnExcluirProduto.setOnClickListener { deletarProduto() }
        imgChoose.setOnClickListener { escolherImagem() }
    }

    private fun escolherImagem() {
        var i = Intent()
        i.setType("image/*")
        i.setAction(Intent.ACTION_GET_CONTENT)
        startActivityForResult(Intent.createChooser(i, "Choose Image"), 111)
    }


    private fun deletarProduto() {
        loadingProduto.visibility = View.VISIBLE
        Thread(Runnable {
            Thread.sleep(1500)
            ApplicationActivity.instance.dbProduto!!.excluirProduto(index)

            runOnUiThread {
                finish()
                loadingProduto.visibility = View.GONE
            }
        }).start()
    }

    private fun salvarProduto() {

        if(edtNomeProduto.text.isNullOrEmpty() ||
            edtDescProduto.text.isNullOrEmpty() ||
            edtPrecoProduto.text.isNullOrEmpty() ||
            edtQuantidade.text.isNullOrEmpty() ||
            imgProduto == null) {

            return Toast.makeText(baseContext, "Preencha todos os campos", Toast.LENGTH_LONG).show()

        }


        val nomeProduto = edtNomeProduto.text.toString()
        val descProduto = edtDescProduto.text.toString()
        val precoProduto = edtPrecoProduto.text.toString().toDouble()
        val estoqueProduto = edtQuantidade.text.toString().toInt()

        loadingProduto.visibility = View.VISIBLE
        Thread(Runnable {
            Thread.sleep(1500)

            if(index == -1){
                // Criando um novo produto
                val Produto = Produto(
                    index,
                    nomeProduto,
                    descProduto,
                    precoProduto,
                    estoqueProduto,
                    0.0,
                    imgProduto!!
                )

                ApplicationActivity.instance.dbProduto!!.criarProduto(Produto)
            }else{
                // Editando um produto existente
                val Produto = Produto(
                    index,
                    nomeProduto,
                    descProduto,
                    precoProduto,
                    estoqueProduto,
                    produto.lucroObtido,
                    imgProduto!!
                )

                ApplicationActivity.instance.dbProduto!!.updateProduto(Produto)
            }

            runOnUiThread {
                finish()
                loadingProduto.visibility = View.GONE
            }
        }).start()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 111 && resultCode == Activity.RESULT_OK && data != null){
            filepath = data.data!!
            var bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filepath)
            imgChoose.setImageBitmap(bitmap)

            imgProduto = DbBitmapUtility.getBytes(bitmap)
        }
    }
}

object DbBitmapUtility {
    // Converte Bitmap para Bytes
    fun getBytes(bitmap: Bitmap): ByteArray {
        val stream = ByteArrayOutputStream()
        bitmap.compress(CompressFormat.PNG, 0, stream)
        return stream.toByteArray()
    }

    // Converte Bytes para Bitmap
    fun getImage(image: ByteArray): Bitmap {
        return BitmapFactory.decodeByteArray(image, 0, image.size)
    }
}