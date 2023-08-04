package br.com.alura.orgs.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import br.com.alura.orgs.database.AppDatabase
import br.com.alura.orgs.extensions.vaiPara
import br.com.alura.orgs.model.Usuario
import br.com.alura.orgs.preferences.dataStore
import br.com.alura.orgs.preferences.usuarioLogadoPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch

abstract class UsuarioBaseActivity : AppCompatActivity() {

    private val usuarioDao by lazy {
        AppDatabase.instancia(this).usuarioDao()
    }

    private val _usuario: MutableStateFlow<Usuario?> = MutableStateFlow(null)
    protected val usuario: StateFlow<Usuario?> = _usuario

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        verificaUsuario()
    }

    private fun verificaUsuario() {
        lifecycleScope.launch {
            dataStore.data.collect { preferences ->
                preferences[usuarioLogadoPrefs]?.let { usuarioId ->
                    buscaUsuario(usuarioId)
                } ?: vaiParaLogin()
            }
        }
    }

    private suspend fun buscaUsuario(usuarioId: String) : Usuario? {
        return usuarioDao.buscaPorId(usuarioId).firstOrNull()
            .also {
                _usuario.value = it
            }
    }

    private fun vaiParaLogin() {
        vaiPara(LoginActivity::class.java) {
            // isto é para que todas as telas anteriores sejam removidas,
            // e apertando o botão de voltar do celular o app seja realmente
            // encerrado
            addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        finish()
    }

    protected suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.remove(usuarioLogadoPrefs)
        }
    }
}