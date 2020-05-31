package com.consorciosm.usuariomonitoreo.ui.main.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseFragment
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.clearAllManagerShared
import com.consorciosm.usuariomonitoreo.ui.auth.view.LoginActivity
import com.consorciosm.usuariomonitoreo.ui.main.MainViewModelFactory
import com.consorciosm.usuariomonitoreo.ui.main.ViewModelMain
import com.squareup.okhttp.internal.Internal.instance
import kotlinx.android.synthetic.main.fragment_notifications.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.kodein
import org.kodein.di.generic.instance

class NotificationsFragment : BaseFragment(),KodeinAware {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel: ViewModelMain
    private val factory: MainViewModelFactory by instance()
    override fun getLayout(): Int =R.layout.fragment_notifications

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= requireActivity().run {
            ViewModelProvider(this,factory).get(ViewModelMain::class.java)
        }
        img_logout.setOnClickListener {
            viewModel.deleteUser()
            viewModel.deleteCarro()
            clearAllManagerShared()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }
    }
}
