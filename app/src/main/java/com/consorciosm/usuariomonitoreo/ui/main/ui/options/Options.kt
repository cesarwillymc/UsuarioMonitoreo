package com.consorciosm.usuariomonitoreo.ui.main.ui.options

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseFragment
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager
import com.consorciosm.usuariomonitoreo.data.service.LocationBackground
import com.consorciosm.usuariomonitoreo.ui.auth.view.LoginActivity
import com.consorciosm.usuariomonitoreo.ui.main.MainViewModelFactory
import com.consorciosm.usuariomonitoreo.ui.main.ViewModelMain

import kotlinx.android.synthetic.main.fragment_options.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

/**
 * A simple [Fragment] subclass.
 */
class Options : BaseFragment(),KodeinAware {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel: ViewModelMain
    private val factory: MainViewModelFactory by instance()
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        super.onViewCreated(view, savedInstanceState)
        viewModel= requireActivity().run {
            ViewModelProvider(this,factory).get(ViewModelMain::class.java)
        }
        img_logout.setOnClickListener {
            requireActivity().stopService(Intent(requireContext(), LocationBackground::class.java))
            viewModel.deleteUser()
            viewModel.deleteCarro()

            SharedPreferencsManager.clearAllManagerShared()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }

    override fun getLayout(): Int = R.layout.fragment_options

}
