package com.example.listadeprodutos.features.listaProdutos.model

class Produto(
    val idProduto: Int,
    val nomeProduto : String,
    val descricaoProduto: String,
    val precoProduto : Double,
    var quantidadeProduto : Int,
    var lucroObtido : Double,
    val imgProduto: ByteArray
)