package com.consorciosm.usuariomonitoreo.data.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.*
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.consorciosm.usuariomonitoreo.common.utils.Coroutines
import com.google.android.gms.location.*
import kotlinx.coroutines.delay
import java.text.ParseException
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.app.MyApp.Companion.getContextApp
import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_COLOR
import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_ID_USER
import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_PLACA
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.getSomeIntValue
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.getSomeStringValue
import com.consorciosm.usuariomonitoreo.data.local.db.AppDB
import com.consorciosm.usuariomonitoreo.data.model.PuntosFirebase
import com.consorciosm.usuariomonitoreo.data.model.vehiculo.Carro
import com.google.firebase.firestore.FirebaseFirestore

class LocationBackground: Service() {

    private val TAG = "MapaAndroid"

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    private val UPDATE_INTERVAL_IN_MILLISECONDS: Long = 5000

    /**
     * The fastest rate for active location updates. Updates will never be more frequent
     * than this value.
     */
    private val FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
        UPDATE_INTERVAL_IN_MILLISECONDS / 2
    /**
     * The current location.
     */
    private var mLocation: Location? = null

    /**
     * Provides access to the Fused Location Provider API.
     */
    private lateinit var mFusedLocationClient: FusedLocationProviderClient

    private val mContext: Context? = null
    /**
     * Callback for changes in location.
     */
    private var mLocationCallback: LocationCallback? = null
    private val mBinder = LocalBinder()


    class LocalBinder : Binder() {
        fun getService():LocationBackground = LocationBackground()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
//        datosCar=AppDB(getContextApp()).vehiculoDao().selectCarro().value!!
        super.onCreate()
    }

    override fun onDestroy() {

        super.onDestroy()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        AsyncTask.execute {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(applicationContext)
            try {


                mLocationCallback = object : LocationCallback() {
                    override fun onLocationResult(p0: LocationResult?) {
                        super.onLocationResult(p0)
                    }
                }
                val mLocationRequest = LocationRequest()
                mLocationRequest.interval = UPDATE_INTERVAL_IN_MILLISECONDS;
                mLocationRequest.fastestInterval = FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS;
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                Coroutines.main{

                    repeat(800000){
                        try {
                            mFusedLocationClient
                                .lastLocation
                                .addOnCompleteListener {
                                    if (it.isSuccessful && it.result != null) {
                                        mLocation = it.result
                                        displayNotify("Daloo Rider esta obteniendo tu ubicacion!.","Mantente conectado: ${mLocation!!.latitude}   ${mLocation!!.longitude}")
                                        Log.e(TAG, "Location : $mLocation")


                                        val value=PuntosFirebase(mLocation!!.latitude,mLocation!!.longitude,true,getSomeIntValue(PREF_COLOR)!!,getSomeStringValue(PREF_PLACA)!!)
                                        FirebaseFirestore.getInstance().collection("vehiculos").document(getSomeStringValue(PREF_ID_USER)!!).set(value)
                                        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                                    } else {
                                        Log.e(TAG, "Failed to get location.")
                                    }
                                }
                        } catch (unlikely: SecurityException) {
                            Log.e(TAG, "Lost location permission.$unlikely")
                        }
                        delay(10000L)
                    }

                }

                try {
                    mFusedLocationClient.requestLocationUpdates(mLocationRequest, null)
                } catch (unlikely: SecurityException) {
                    Log.e(TAG, "Lost location permission. Could not request updates. $unlikely");
                }

            } catch (ignored: ParseException) {
                Log.e(TAG, "ingnored.")
            }
        }
        return android.app.Service.START_STICKY;
    }
    private fun displayNotify(task: String, desc: String) {
        val manager =
            applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel =
                NotificationChannel(
                    "DalooRider",
                    "DalooRider",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            manager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(applicationContext, "DalooRider")
            .setContentTitle(task).setContentText(desc).setSmallIcon(R.mipmap.ic_launcher)
        manager.notify(1, builder.build())

    }
}