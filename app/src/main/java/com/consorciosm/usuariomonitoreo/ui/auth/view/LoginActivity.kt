package com.consorciosm.usuariomonitoreo.ui.auth.view



import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseActivity
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.clearAllManagerShared
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.ui.auth.AuthViewModel
import com.consorciosm.usuariomonitoreo.ui.auth.AuthViewModelFactory
import com.consorciosm.usuariomonitoreo.ui.main.MainActivity
import kotlinx.android.synthetic.main.activity_login.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : BaseActivity(), KodeinAware {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel:AuthViewModel
    private val factory: AuthViewModelFactory by instance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel= run {
            ViewModelProvider(this,factory).get(AuthViewModel::class.java)
        }
        al_btn_signin.setOnClickListener {
            val email= al_edtxt_gmail.text.toString().trim()
            val pass= al_edtxt_pass.text.toString().trim()
            userLogin(email, pass)
        }
        al_txt_register.setOnClickListener {
            snakBar("Esta opcion no esta disponible")
        }
        viewModel.getLoggetUser.observe(this, Observer {
            if (it!=null){
                Log.e("datos",it.toString())
                if(it.accountActive){
                    snakBar("Te logeaste correctamente")
                    navMainAdmin()
                }else{
                    snakBar("No tienes permisos para seguir, contactate con un supervisor")
                    logout()

                }

            }
        })
    }
    private fun logout(){
        viewModel.deleteUser()
        clearAllManagerShared()
    }
    private fun navMainAdmin() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun userLogin(email:String,pass:String) {
        if (email.isEmpty()){
            al_edtxt_gmail.error="Tu dni esta vacio"
            return
        }
        if (pass.isEmpty()){
            al_edtxt_pass.error="Tu contraseña esta vacio"
            return
        }
        if (!viewModel.IsValidNumberDoc(email)){
            al_edtxt_gmail.error="El dni no es valido"
            return
        }
        viewModel.SignIn(email,pass).observe(this, Observer {
            when(it){
                is Resource.Loading->{
                    hideKeyboard()
                    showProgressBar()
                }
                is Resource.Success->{
                    hideProgressBar()
                }
                is Resource.Failure->{
                    snakBar(" ${it.exception.message}")
                    Log.e("error",it.exception.message)
                    hideProgressBar()
                }
            }
        })
    }

    fun showProgressBar(){
        login_progressbar.visibility= View.VISIBLE
        al_btn_signin.visibility= View.GONE
    }
    fun hideProgressBar(){
        login_progressbar.visibility= View.GONE
        al_btn_signin.visibility= View.VISIBLE
    }
    override fun getLayout(): Int = R.layout.activity_login
}
