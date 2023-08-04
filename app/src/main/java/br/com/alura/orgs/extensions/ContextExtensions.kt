package br.com.alura.orgs.extensions

import android.content.Context
import android.content.Intent
import android.widget.Toast

fun Context.vaiPara(clazz: Class<*>, intent: Intent.() -> Unit = {}){
    Intent(this, clazz)
        .apply {
            intent()
            startActivity(this)
        }
}

fun Context.toast(msg: String) {
    Toast.makeText(
        this,
        msg,
        Toast.LENGTH_SHORT
    ).show()
}