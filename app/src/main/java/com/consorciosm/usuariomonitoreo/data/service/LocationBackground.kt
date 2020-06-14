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
import kotlinx.coroutines.Job

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

    private lateinit var trabajo:Job
    var latitudAnterior =0.toDouble()
    var longitudAnterior=0.toDouble()
    class LocalBinder : Binder() {
        fun getService():LocationBackground = LocationBackground()
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        val value= PuntosFirebase(0.toDouble(),0.toDouble(),false, getSomeIntValue(
            PREF_COLOR)!!, getSomeStringValue(PREF_PLACA)!!,getSomeStringValue(PREF_ID_USER)!!,
            getSomeStringValue("IDVEHICULO")!!,0.toDouble(),0.toDouble())
        FirebaseFirestore.getInstance().collection("vehiculos").document(getSomeStringValue(PREF_ID_USER)!!).set(value)
//        datosCar=AppDB(getContextApp()).vehiculoDao().selectCarro().value!!
        super.onCreate()
    }

    override fun onDestroy() {
        trabajo.cancel()
        FirebaseFirestore.getInstance().collection("vehiculos").document(getSomeStringValue(PREF_ID_USER)!!).update("state",false)
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
                trabajo=Coroutines.main{

                    repeat(90000000){
                        try {
                            mFusedLocationClient
                                .lastLocation
                                .addOnCompleteListener {
                                    if (it.isSuccessful && it.result != null) {
                                        mLocation = it.result
                                        displayNotify("San Miguel esta obteniendo tu ubicacion!.","Mantente conectado: ${mLocation!!.latitude}   ${mLocation!!.longitude}")

                                        if (latitudAnterior==0.toDouble()&&longitudAnterior==0.toDouble()){
                                            latitudAnterior=mLocation!!.latitude
                                            longitudAnterior=mLocation!!.longitude
                                        }
                                        val latitude=mLocation!!.latitude
                                        val longitude=mLocation!!.longitude
                                        Log.e("metros","$latitude  $longitude   \n ${latitudAnterior} ${longitudAnterior}" )
                                        val puntoa = Location("pntoa")
                                        puntoa.latitude=latitudAnterior
                                        puntoa.longitude=longitudAnterior
                                        val puntob = Location("distaaaaaa")
                                        puntob.latitude= latitude
                                        puntob.longitude= longitude
                                        var metros=0f
                                        metros = try{
                                            puntoa.distanceTo(puntob)
                                        }catch (e:Exception){
                                            puntob.distanceTo(puntoa)
                                        }
                                        Log.e("metros"," $metros \n $latitude  $longitude   \n ${latitudAnterior} ${longitudAnterior}" )
                                        if (metros<=500 ){
                                            FirebaseFirestore.getInstance().collection("vehiculos").document(getSomeStringValue(PREF_ID_USER)!!)
                                                .update("latitude",mLocation!!.latitude,"longitude",mLocation!!.longitude,
                                                    "latAnterior",latitudAnterior,"latPosterior",longitudAnterior,"state",true).addOnCompleteListener {
                                                    if (it.isSuccessful){
                                                        Log.e(TAG, "Location : $mLocation")
                                                        latitudAnterior=mLocation!!.latitude
                                                        longitudAnterior=mLocation!!.longitude
                                                    }
                                                }
                                        }
                                        mFusedLocationClient.removeLocationUpdates(mLocationCallback)
                                    } else {
                                        Log.e(TAG, "Failed to get location.")
                                    }
                                }
                        } catch (unlikely: SecurityException) {
                            Log.e(TAG, "Lost location permission.$unlikely")
                        }
                        delay(20000L)
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
                    "San Miguel",
                    "San Miguel",
                    NotificationManager.IMPORTANCE_DEFAULT
                )
            manager.createNotificationChannel(channel)
        }
        val builder = NotificationCompat.Builder(applicationContext, "San Miguel")
            .setContentTitle(task).setContentText(desc).setSmallIcon(R.mipmap.ic_launcher)
        manager.notify(1, builder.build())

    }
}