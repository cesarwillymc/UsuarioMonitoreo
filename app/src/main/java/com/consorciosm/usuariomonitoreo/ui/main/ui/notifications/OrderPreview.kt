package com.consorciosm.usuariomonitoreo.ui.main.ui.notifications

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseFragment
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.data.model.NotificacionesList
import com.consorciosm.usuariomonitoreo.data.model.OrdenProgramada
import com.consorciosm.usuariomonitoreo.ui.main.MainViewModelFactory
import com.consorciosm.usuariomonitoreo.ui.main.ViewModelMain
import kotlinx.android.synthetic.main.fragment_order_preview.*

import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance


class OrderPreview : BaseFragment(),KodeinAware {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel: ViewModelMain
    private val factory: MainViewModelFactory by instance()
    var id:String=""
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= requireActivity().run {
            ViewModelProvider(this,factory).get(ViewModelMain::class.java)
        }
        id= OrderPreviewArgs.fromBundle(requireArguments()).id
        viewModel.getNotificationById(id).observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading->{
                    login_progressbar.visibility= View.VISIBLE
                }
                is Resource.Success->{
                    funLoadData(it.data)
                    login_progressbar.visibility= View.GONE
                }
                is Resource.Failure->{
                    snakBar(" ${it.exception.message}")
                    Log.e("error", it.exception.message)
                    login_progressbar.visibility= View.GONE
                }
            }
        })

    }

    private fun funLoadData(data: OrdenProgramada) {
        lbl_order_aitorizadopor.text=data.autorizadoPor
        lbl_order_aprobado.text=data.aprobado
        lbl_order_autorizadoa.text=data.autorizadoA
        lbl_order_autorizadoacargo.text=data.autorizadoACargo
        lbl_order_autorizadoporcargo.text=data.autorizadoPorCargo
        lbl_order_codigo.text=data.codigo

        lbl_order_conductor.text=data.conductorAutorizado
        lbl_order_destinosalida.text=data.salidaDestino
        lbl_order_fecharetorno.text=data.retornoFecha
        lbl_order_fechasalida.text=data.salidaFecha
        lbl_order_fechasolicitud.text=data.fechaSolicitud
        lbl_order_formulario.text=data.formulario

        lbl_order_observaciones.text=data.observaciones
        lbl_order_ocupantes.text=data.ocupantes
        lbl_order_origensalida.text=data.salidaOrigen
        lbl_order_placa.text=data.camionetaPlaca
        lbl_order_proyecto.text=data.proyecto
        lbl_order_retornodestino.text=data.retornoDestino

        lbl_order_retornoorigen.text=data.retornoOrigen
        lbl_order_tiempoestimado.text=data.tiempoEstimadoViaje
        lbl_order_version.text=data.version


    }

    override fun getLayout(): Int = R.layout.fragment_order_preview

}
