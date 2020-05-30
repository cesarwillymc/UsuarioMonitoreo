package com.consorciosm.usuariomonitoreo.ui.main

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.appbar_transparent.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.android.kodein
class MainActivity : BaseActivity(),KodeinAware {

    override val kodein: Kodein by kodein()
    private lateinit var viewModel:ViewModelMain
    private val factory: MainViewModelFactory by instance()

    lateinit var navController: NavController
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel= run {
            ViewModelProvider(this,factory).get(ViewModelMain::class.java)
        }
        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        setSupportActionBar(toolbar_transparent)
        supportActionBar!!.setDisplayShowTitleEnabled(false)
        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navController.addOnDestinationChangedListener { controller, destination, arguments ->
            Dexter.withContext(this).withPermissions(
                listOf(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_BACKGROUND_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION
                )
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(p0: MultiplePermissionsReport?) {
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: MutableList<com.karumi.dexter.listener.PermissionRequest>?,
                    p1: PermissionToken?
                ) {
                }
            }).check()
        }

    }

    override fun getLayout(): Int = R.layout.activity_main
}
