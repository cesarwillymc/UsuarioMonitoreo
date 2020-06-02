package com.consorciosm.usuariomonitoreo.ui.main.ui.notifications

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseFragment
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.clearAllManagerShared
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.data.model.NotificacionesList
import com.consorciosm.usuariomonitoreo.ui.auth.view.LoginActivity
import com.consorciosm.usuariomonitoreo.ui.main.MainViewModelFactory
import com.consorciosm.usuariomonitoreo.ui.main.ViewModelMain
import com.squareup.okhttp.internal.Internal.instance
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance
import java.util.*

class NotificationsFragment : BaseFragment(),KodeinAware ,NotificacionesListener {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel: ViewModelMain
    private val factory: MainViewModelFactory by instance()
    var paginaActual=0
    lateinit var notificacionAdapter: NotificacionAdapter
    override fun getLayout(): Int =R.layout.fragment_notifications

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= requireActivity().run {
            ViewModelProvider(this,factory).get(ViewModelMain::class.java)
        }
        notificacionAdapter = NotificacionAdapter(this)
        rv_fragment_ordenes.apply {
            //, LinearLayoutManager.VERTICAL,false
            layoutManager = LinearLayoutManager(view.context)
            setHasFixedSize(true)
            isNestedScrollingEnabled=true
            adapter = notificacionAdapter
        }
        LoadList()
        Search.setOnClickListener {
            paginaActual++
            LoadList()
        }
    }
    fun LoadList(){

        viewModel.getListNotificaciones(paginaActual).observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading->{
                    progressBar.visibility= View.VISIBLE
                }
                is Resource.Success->{
                    Log.e("error",it.data.toString())
                    searchFecha.setText("Pagina ${paginaActual+1}")
                    notificacionAdapter.updateData(it.data)
                    progressBar.visibility= View.GONE
                }
                is Resource.Failure->{
                    snakBar(" ${it.exception.message}")
                    Log.e("error", it.exception.message)
                    progressBar.visibility= View.GONE
                }
            }
        })
    }


    override fun listener(notificacionesList: NotificacionesList, position: Int) {
        if (notificacionesList.isOrder){
            val noti= NotificationsFragmentDirections.actionNavigationNotificationsToOrderPreview(notificacionesList._id)
            findNavController().navigate(noti)
        }else{
            snakBar("No tiene mas datos")
        }
    }
}
