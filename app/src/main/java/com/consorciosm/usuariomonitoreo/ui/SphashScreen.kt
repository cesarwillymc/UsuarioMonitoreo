package com.consorciosm.usuariomonitoreo.ui

import android.content.Intent
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.consorciosm.usuariomonitoreo.base.BaseActivity
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager
import com.consorciosm.usuariomonitoreo.ui.auth.AuthViewModel
import com.consorciosm.usuariomonitoreo.ui.auth.AuthViewModelFactory
import com.consorciosm.usuariomonitoreo.ui.main.MainActivity
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.ui.auth.view.LoginActivity
import kotlinx.android.synthetic.main.activity_sphash_screen.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class SphashScreen : BaseActivity(),KodeinAware {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel:AuthViewModel
    private val factory: AuthViewModelFactory by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= run {
            ViewModelProvider(this,factory).get(AuthViewModel::class.java)
        }
        val animation = AnimationUtils.loadAnimation(this, R.anim.anim)
        imgLogoSplash.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationRepeat(p0: Animation?) {
            }

            override fun onAnimationEnd(p0: Animation?) {
                funcionObjetc()

            }

            override fun onAnimationStart(p0: Animation?) {
            }

        })
    }

    private fun funcionObjetc() {
        viewModel.getLoggetUser.observe(this, Observer {
            if (it!=null){
                navMainAdmin()
            }else{
                navigationToPrincipal()
            }
        })
    }

    private fun logout(){
        viewModel.deleteUser()
        SharedPreferencsManager.clearAllManagerShared()
    }
    private fun navigationToPrincipal() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
    private fun navMainAdmin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    override fun getLayout(): Int = R.layout.activity_sphash_screen
}