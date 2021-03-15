package com.example.listadeprodutos.features

import android.app.Activity
import android.app.Application
import android.view.View
import android.widget.ProgressBar
import com.example.listadeprodutos.features.helpers.HelperBD

class ApplicationActivity : Application() {

    var dbProduto : HelperBD? = null
        private set

    companion object{
        // Atraves do Instance sua ApplicationActivity é instanciada automaticamente sem ter necessidade de instanciar manualmente
        // Com isso vc poderá acessar o dbProduto (ou outras funções) pela variavel instance
        // Exemplo: ApplicationActivity.instance.dbProduto

        lateinit var instance : ApplicationActivity
    }


    override fun onCreate() {
        super.onCreate()

        // Referenciando a instance assim que a ApplicationActivity for criada
        instance = this
        dbProduto = HelperBD(this)

    }
}