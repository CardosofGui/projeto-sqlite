package com.example.listadeprodutos.features.helpers

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.listadeprodutos.features.listaProdutos.model.Produto

class HelperBD(
    mContext: Context
) : SQLiteOpenHelper(mContext, NOME_BANCO, null, VERSAO_ATUAL) {

    companion object {
        private val NOME_BANCO = "produto.db"
        private val VERSAO_ATUAL = 3
    }

    val NOME_TABELA = "produto"
    val ID_PRODUTO = "idProduto"
    val NOME_PRODUTO = "tituloProduto"
    val DESCRICAO_PRODUTO = "textoProduto"
    val PRECO_PRODUTO = "precoProduto"
    val QUANTIDADE_PRODUTO = "quantidadeProduto"
    val LUCRO_PRODUTO = "lucroProduto"
    val IMAGEM_PRODUTO = "imagemProduto"

    val CRIAR_TABELA = "CREATE TABLE $NOME_TABELA(" +
            "$ID_PRODUTO INTEGER NOT NULL," +
            "$NOME_PRODUTO TEXT NOT NULL," +
            "$DESCRICAO_PRODUTO TEXT NOT NULL," +
            "$PRECO_PRODUTO DOUBLE NOT NULL," +
            "$QUANTIDADE_PRODUTO INT NOT NULL," +
            "$LUCRO_PRODUTO DOUBLE," +
            "$IMAGEM_PRODUTO BLOB NOT NULL," +
            "PRIMARY KEY($ID_PRODUTO AUTOINCREMENT)" +
            ");"

    val DROP_TABELA = "DROP TABLE IF EXISTS $NOME_TABELA"

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(CRIAR_TABELA)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        if (oldVersion != newVersion) {
            db?.execSQL(DROP_TABELA)
        }
        onCreate(db)
    }

    fun criarProduto(itemProduto: Produto) {
        val db = writableDatabase ?: return
        val sql = "INSERT INTO $NOME_TABELA ($NOME_PRODUTO, $DESCRICAO_PRODUTO, $PRECO_PRODUTO, $QUANTIDADE_PRODUTO, $LUCRO_PRODUTO, $IMAGEM_PRODUTO) VALUES(?, ?, ?, ?, 0.0, ?)"
        val args = arrayOf(itemProduto.nomeProduto, itemProduto.descricaoProduto, itemProduto.precoProduto, itemProduto.quantidadeProduto, itemProduto.imgProduto)

        db.execSQL(sql, args)

        db.close()
    }

    fun buscarProduto(busca: String, buscaID: Boolean = false): List<Produto> {
        val db = readableDatabase ?: return mutableListOf()
        val lista = mutableListOf<Produto>()
        val sql: String

        if (buscaID) {
            sql = "SELECT * FROM $NOME_TABELA WHERE $ID_PRODUTO = '$busca'"
        } else {
            sql = "SELECT * FROM $NOME_TABELA WHERE $NOME_PRODUTO LIKE '%$busca%'"
        }

        var cursor = db.rawQuery(sql, arrayOf(), null) ?: return mutableListOf<Produto>()

        if (cursor == null) {
            db.close()
            return lista
        }

        while (cursor.moveToNext()) {
            var produto = Produto(
                cursor.getInt(cursor.getColumnIndex(ID_PRODUTO)),
                cursor.getString(cursor.getColumnIndex(NOME_PRODUTO)),
                cursor.getString(cursor.getColumnIndex(DESCRICAO_PRODUTO)),
                cursor.getDouble(cursor.getColumnIndex(PRECO_PRODUTO)),
                cursor.getInt(cursor.getColumnIndex(QUANTIDADE_PRODUTO)),
                cursor.getDouble(cursor.getColumnIndex(LUCRO_PRODUTO)),
                cursor.getBlob(cursor.getColumnIndex(IMAGEM_PRODUTO))
            )

            lista.add(produto)
        }

        return lista
    }

    fun updateProduto(itemProduto: Produto) {
        val db = writableDatabase ?: return
        val sql =
            "UPDATE $NOME_TABELA SET $NOME_PRODUTO = ?, $DESCRICAO_PRODUTO = ?, $PRECO_PRODUTO = ?, $QUANTIDADE_PRODUTO = ?, $LUCRO_PRODUTO = ?, $IMAGEM_PRODUTO = ? WHERE $ID_PRODUTO = ?"
        val args = arrayOf(
            itemProduto.nomeProduto,
            itemProduto.descricaoProduto,
            itemProduto.precoProduto,
            itemProduto.quantidadeProduto,
            itemProduto.lucroObtido,
            itemProduto.imgProduto,
            itemProduto.idProduto
        )

        db.execSQL(sql, args)
        db.close()
    }

    fun excluirProduto(id: Int) {
        val db = writableDatabase ?: return
        val sql = "DELETE FROM $NOME_TABELA WHERE $ID_PRODUTO = ?"
        val args = arrayOf(id)

        db.execSQL(sql, args)

        db.close()
    }

    fun atualizarLucro(novoLucro : Double, novoEstoque: Int, idProduto: Int){
        val db = writableDatabase ?: return
        val sql = "UPDATE $NOME_TABELA SET $LUCRO_PRODUTO = ?, $QUANTIDADE_PRODUTO = ? WHERE $ID_PRODUTO = ?"
        val args = arrayOf(novoLucro, novoEstoque, idProduto)

        db.execSQL(sql, args)
        db.close()
    }
}