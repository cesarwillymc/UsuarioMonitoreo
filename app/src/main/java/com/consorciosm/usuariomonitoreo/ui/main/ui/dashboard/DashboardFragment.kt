package com.consorciosm.usuariomonitoreo.ui.main.ui.dashboard

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseFragment
import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_ID_USER
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.getSomeStringValue
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.data.model.PuntosFirebase
import com.consorciosm.usuariomonitoreo.data.service.LocationBackground
import com.consorciosm.usuariomonitoreo.ui.main.MainViewModelFactory
import org.kodein.di.android.x.kodein
import com.consorciosm.usuariomonitoreo.ui.main.ViewModelMain
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.startService
import org.jetbrains.anko.support.v4.stopService
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance

class DashboardFragment : BaseFragment(),KodeinAware {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel: ViewModelMain
    private val factory: MainViewModelFactory by instance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= requireActivity().run {
            ViewModelProvider(this,factory).get(ViewModelMain::class.java)
        }


    }



    override fun getLayout(): Int= R.layout.fragment_dashboard


}
