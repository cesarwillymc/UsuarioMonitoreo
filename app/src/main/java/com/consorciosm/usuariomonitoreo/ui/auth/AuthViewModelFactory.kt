package com.consorciosm.usuariomonitoreo.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.consorciosm.usuariomonitoreo.data.network.repository.AuthRepository

@Suppress("UNCHECKED_CAST")
class AuthViewModelFactory(private val repo: AuthRepository) :ViewModelProvider.NewInstanceFactory(){
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AuthViewModel(repo) as T
    }
}