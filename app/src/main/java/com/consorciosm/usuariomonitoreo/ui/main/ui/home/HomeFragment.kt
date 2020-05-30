package com.consorciosm.usuariomonitoreo.ui.main.ui.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseActivity
import com.consorciosm.usuariomonitoreo.base.BaseFragment
import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_COLOR
import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_ID_USER
import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_PLACA
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.getSomeStringValue
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.setSomeIntValue
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.setSomeStringValue
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.data.model.PuntosFirebase
import com.consorciosm.usuariomonitoreo.data.service.LocationBackground
import com.consorciosm.usuariomonitoreo.ui.main.MainViewModelFactory
import com.consorciosm.usuariomonitoreo.ui.main.ViewModelMain
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_home.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.android.x.kodein
class HomeFragment : BaseFragment() , KodeinAware {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel: ViewModelMain
    private val factory: MainViewModelFactory by instance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("usuario",getSomeStringValue(PREF_ID_USER)!!)
        viewModel= requireActivity().run {
            ViewModelProvider(this,factory).get(ViewModelMain::class.java)
        }
        fd_img_switch.setOnClickListener{
            fd_img_switch.isSelected = !it.isSelected
            if (fd_img_switch.isSelected){
                comprobarDatos()
            } else{
                val value= PuntosFirebase(213213.toDouble(),213213.toDouble(),false,213,"asasdd")

                FirebaseFirestore.getInstance().collection("vehiculos").document(getSomeStringValue(PREF_ID_USER)!!).set(value)
                viewModel.deleteCarro()
            }

        }
        viewModel.getCarroData.observe(viewLifecycleOwner, Observer {
            if (it!=null){
                fd_img_switch.isSelected=true
                fd_text_switch.text= "APAGAR"
                setSomeStringValue(PREF_PLACA,it.numeroPlaca)
                setSomeIntValue(PREF_COLOR,it.color)
                requireActivity().startService(Intent(requireContext(), LocationBackground::class.java))
            }else{
                requireActivity().stopService(Intent(requireContext(),LocationBackground::class.java))
                fd_text_switch.isSelected=false
                fd_text_switch.text= "ENCENDER"
            }
        })
    }
    private fun comprobarDatos() {

        viewModel.getVehiculoVinculado().observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading->{
                    progress_circular.visibility= View.VISIBLE
                }
                is Resource.Success->{
                    if (it.data==null)
                        fd_img_switch.isSelected = false
                    progress_circular.visibility= View.GONE
                }
                is Resource.Failure->{
                    fd_img_switch.isSelected=false
                    snakBar(" ${it.exception.message}")
                    Log.e("error", it.exception.message)
                    progress_circular.visibility= View.GONE
                }
            }
        })


    }
    override fun getLayout(): Int= R.layout.fragment_home
}
