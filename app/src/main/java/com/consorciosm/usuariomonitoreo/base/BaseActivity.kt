package com.consorciosm.usuariomonitoreo.base

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import org.jetbrains.anko.contentView

abstract  class BaseActivity : AppCompatActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(getLayout())
    }
    fun hideKeyboard(){
        try{
            val view = this.currentFocus
            view!!.clearFocus()
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }catch (e :Exception){

        }
    }
    @LayoutRes
    protected abstract fun getLayout():Int

    fun showUpdateVersion(){
        val dialogBuilder= AlertDialog.Builder(this)
        dialogBuilder.setMessage("Existe una nueva actualizacion de esta aplicacion, por favor actualizala")
            .setCancelable(false)
            .setPositiveButton("Actualizar ahora",DialogInterface.OnClickListener { dialog, which ->
                openAppInPlayStore("")
                dialog.dismiss()
            })
        val alertDialog= dialogBuilder.create()
        alertDialog.setTitle("Actualizacion Disponible")
        alertDialog.show()
    }
    fun snakBar(mensaje: String){
        try{
            Snackbar.make(contentView!!,mensaje, Snackbar.LENGTH_LONG).also { snackbar ->
                snackbar.setAction("Ok"){
                    snackbar.dismiss()
                }.show()
            }
        }catch (e:Exception){
            Log.e("error","snakbar")
        }
    }
    fun toast(mensaje:String){
        Toast.makeText(this,mensaje,Toast.LENGTH_LONG).show()
    }
    private fun openAppInPlayStore(appPackageName:String ){
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        }catch (e:ActivityNotFoundException){
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

}