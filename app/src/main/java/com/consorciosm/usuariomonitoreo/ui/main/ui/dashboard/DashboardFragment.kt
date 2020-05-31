package com.consorciosm.usuariomonitoreo.ui.main.ui.dashboard

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.provider.MediaStore
import android.text.format.DateFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.DatePicker
import android.widget.TimePicker
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.consorciosm.usuariomonitoreo.R
import com.consorciosm.usuariomonitoreo.base.BaseFragment
import com.consorciosm.usuariomonitoreo.common.constants.Constants.PREF_ID_USER
import com.consorciosm.usuariomonitoreo.common.shared.SharedPreferencsManager.Companion.getSomeStringValue
import com.consorciosm.usuariomonitoreo.common.utils.Resource
import com.consorciosm.usuariomonitoreo.common.utils.detectar_formato
import com.consorciosm.usuariomonitoreo.common.utils.getPath
import com.consorciosm.usuariomonitoreo.data.model.*
import com.consorciosm.usuariomonitoreo.data.service.LocationBackground
import com.consorciosm.usuariomonitoreo.ui.main.MainViewModelFactory
import org.kodein.di.android.x.kodein
import com.consorciosm.usuariomonitoreo.ui.main.ViewModelMain
import com.google.firebase.firestore.FirebaseFirestore
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import kotlinx.android.synthetic.main.fragment_dashboard.*
import org.jetbrains.anko.support.v4.startService
import org.jetbrains.anko.support.v4.stopService
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import java.io.File
import java.util.*

class DashboardFragment : BaseFragment(),KodeinAware, TimePickerDialog.OnTimeSetListener,
    DatePickerDialog.OnDateSetListener  {
    override val kodein: Kodein by kodein()
    private lateinit var viewModel: ViewModelMain
    private val factory: MainViewModelFactory by instance()
    var hora=0
    var minuto=0

    var file: File? = null
    var new = true
    var timeData= ""
    //Fecha
    var anio= Calendar.getInstance().get(Calendar.YEAR)
    var mes= Calendar.getInstance().get(Calendar.MONTH)
    var dia= Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel= requireActivity().run {
            ViewModelProvider(this,factory).get(ViewModelMain::class.java)
        }
        lbl_hora_inicio_fparte.setOnClickListener {
            timeData="Kinicio"
            showDialog()
        }
        lbl_hora_final_fparte.setOnClickListener {
            timeData="Kfinal"
            showDialog()
        }
        lbl_horasalida_fparte.setOnClickListener {
            timeData="GarajeSalidainicio"
            showDialog()
        }
        lbl_horaingreso_fparte.setOnClickListener {
            timeData="GarajeEntradaFinal"
            showDialog()
        }
        lbl_fecha_fparte.setOnClickListener {
            showDialogPicker()
        }
        sendParte.setOnClickListener {
            if (new){
                if (file!=null){
                    if (comprobarDatos()){
                        registrarDatos()
                    }
                }else{
                    snakBar("Necesitas introducir una imagen para continuar.")
                }
            }else{
                if (comprobarDatos()){
                    registrarDatos()
                }
            }
        }
        editPhotoCombustible.setOnClickListener {
            if (new)
                registroVehiculo()
            else snakBar("No puedes modificar la photo que subiste anteriormente")
        }
    }

    private fun registrarDatos() {
        val fechaParte=lbl_fecha_fparte.text.toString().trim()
        val empresaproveedora=lbl_empresaprov_fparte.text.toString().trim()
        val licenciaEmpresa=lbl_empresaprov_licencia.text.toString().trim()
        val horasalida=lbl_horasalida_fparte.text.toString().trim()
        val horaentrada=lbl_horaingreso_fparte.text.toString().trim()
        val actividad=lbl_actividad_fparte.text.toString().trim()
        val horainicioparte=lbl_hora_inicio_fparte.text.toString().trim()
        val kilometrajeinicio=lbl_kilometraje_inicio_fparte.text.toString().trim()
        val horafinalparte=lbl_hora_final_fparte.text.toString().trim()
        val kilometrajefinal=lbl_kilometraje_final_fparte.text.toString().trim()
        val kilometrosAbastecimiento= lbl_kilometraje_abastecimiento_fparte.text.toString().trim()
        val galonesAbastecimiento= lbl_galones_abastecimiento_fparte.text.toString().trim()
        val combustible= niveldeConbustible.text.toString().trim()

        //Estado Vehiculo

        val tarjetaPropiedad= cb_tarjeta_proiedad_fparte.isChecked
        val soat= cb_soat_fparte.isChecked
        val conosSeguridad= cb_triangulos_seguridad_fparte.isChecked
        val botiquin= cb_botiquin_fparte.isChecked
        val nivelAceite= cb_nivel_aceite_fparte.isChecked
        val nivelAgua= cb_nivel_agua_fparte.isChecked
        val liquidosFrenos= cb_nivel_liquido_frenos_fparte.isChecked
        val liquidoHidroliga= cb_nivel_liquido_hidrolina_fparte.isChecked
        val espejos= cb_espejos_fparte.isChecked
        val gata= cb_gata_palanca_fparte.isChecked
        val extintor= cb_extintor_fparte.isChecked
        val lucesExteriores= cb_luces_exteriores_fparte.isChecked
        val refrigerante= cb_nivel_refrigerante_fparte.isChecked
        val alarmaRetroceso= cb_alarma_retroceso_fparte.isChecked
        val herramientas= cb_herramientas_fparte.isChecked


        viewModel.subirParteData(ParteDiario(
            InfoGeneral(fechaParte,empresaproveedora,licenciaEmpresa,horasalida,horaentrada),
            EstadoVehiculo(tarjetaPropiedad,conosSeguridad,nivelAceite,liquidosFrenos,espejos,soat,botiquin,nivelAgua,
                liquidoHidroliga,gata,extintor,lucesExteriores,refrigerante,alarmaRetroceso,herramientas),
            ActividadDiaria(actividad,horainicioparte,kilometrajeinicio,horafinalparte,kilometrajefinal),
            Ncombustible(combustible),Abastecimiento(kilometrosAbastecimiento,galonesAbastecimiento)
        ),file!!,"name").observe(viewLifecycleOwner, Observer {
            when(it){
                is Resource.Loading->{
                    login_progressbar.visibility= View.VISIBLE
                }
                is Resource.Success->{
                    new = it.data==null
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

    fun comprobarDatos():Boolean{
        val fechaParte=lbl_fecha_fparte.text.toString().trim()
        val empresaproveedora=lbl_empresaprov_fparte.text.toString().trim()
        val licenciaEmpresa=lbl_empresaprov_licencia.text.toString().trim()
        val horasalida=lbl_horasalida_fparte.text.toString().trim()
        val horaentrada=lbl_horaingreso_fparte.text.toString().trim()
        val actividad=lbl_actividad_fparte.text.toString().trim()
        val horainicioparte=lbl_hora_inicio_fparte.text.toString().trim()
        val kilometrajeinicio=lbl_kilometraje_inicio_fparte.text.toString().trim()
        val horafinalparte=lbl_hora_final_fparte.text.toString().trim()
        val kilometrajefinal=lbl_kilometraje_final_fparte.text.toString().trim()
        val kilometrosAbastecimiento= lbl_kilometraje_abastecimiento_fparte.text.toString().trim()
        val galonesAbastecimiento= lbl_galones_abastecimiento_fparte.text.toString().trim()
        val combustible= niveldeConbustible.text.toString().trim()
        if (combustible.isEmpty()){
            niveldeConbustible.error=" Debe introducir el nivel de combustible"
            niveldeConbustible.requestFocus()
            return false
        }
        if (fechaParte.isEmpty()){
            lbl_fecha_fparte.error=" Debe introducir la fecha del parte"
            lbl_fecha_fparte.requestFocus()
            return false
        }
        if (empresaproveedora.isEmpty()){
            lbl_empresaprov_fparte.error=" Debe introducir la empresa proveedora"
            lbl_empresaprov_fparte.requestFocus()
            return false
        }
        if (licenciaEmpresa.isEmpty()){
            lbl_empresaprov_licencia.error=" Debe introducir la licencia de la empresa"
            lbl_empresaprov_licencia.requestFocus()
            return false
        }
        if (horasalida.isEmpty()){
            lbl_horasalida_fparte.error=" Debe introducir la hora que salio del garaje"
            lbl_horasalida_fparte.requestFocus()
            return false
        }
        if (horaentrada.isEmpty()){
            lbl_horaingreso_fparte.error=" Debe introducir la hora que ingreso al garaje"
            lbl_horaingreso_fparte.requestFocus()
            return false
        }
        if (actividad.isEmpty()){
            lbl_actividad_fparte.error=" Debe introducir la actividad realizada en el dia"
            lbl_actividad_fparte.requestFocus()
            return false
        }
        if (horainicioparte.isEmpty()){
            lbl_hora_inicio_fparte.error=" Debe introducir la hora en que esta realizando el parte"
            lbl_hora_inicio_fparte.requestFocus()
            return false
        }
        if (kilometrajeinicio.isEmpty()){
            lbl_kilometraje_inicio_fparte.error="Debe introducir con que kilometraje empezo el dia "
            lbl_kilometraje_inicio_fparte.requestFocus()
            return false
        }
        if (horafinalparte.isEmpty()){
            lbl_hora_final_fparte.error="Debe introducir la hora en que esta realizando el parte"
            lbl_hora_final_fparte.requestFocus()
            return false
        }
        if (kilometrajefinal.isEmpty()){
            lbl_kilometraje_final_fparte.error=" Debe introducir los kilometros que entrego"
            lbl_kilometraje_final_fparte.requestFocus()
            return false
        }
        if (kilometrosAbastecimiento.isEmpty()){
            lbl_kilometraje_abastecimiento_fparte.error=" Debe introducir el kilometraje entregado"
            lbl_kilometraje_abastecimiento_fparte.requestFocus()
            return false
        }
        if (galonesAbastecimiento.isEmpty()){
            lbl_galones_abastecimiento_fparte.error=" Debe introducir cuantos galones de abastecimiento hay"
            lbl_galones_abastecimiento_fparte.requestFocus()
            return false
        }
        return true
    }
    private fun showDialog() {
        TimePickerDialog( requireActivity(),
            this,hora,minuto,
            DateFormat.is24HourFormat(requireActivity())).show()
    }
    private fun showDialogPicker() {
        val datepicker= DatePickerDialog(requireContext(),this,
            anio,
            mes,
            dia)
        datepicker.show()
    }
    fun registroVehiculo() {
        val opciones =
            arrayOf<CharSequence>("Tomar Foto", "Cargar Imagen")
        val alertOpciones =
            AlertDialog.Builder(requireContext())

        alertOpciones.setTitle("Seleccione una opción:")
        alertOpciones.setItems(
            opciones
        ) { dialogInterface, i ->
            try {
                if (opciones[i] == "Tomar Foto") {
                    file = null
                    val builder = StrictMode.VmPolicy.Builder()
                    StrictMode.setVmPolicy(builder.build())
                    val directoryPath =
                        Environment.getExternalStorageDirectory().toString() + "/" + "evanstechnologiesdriver" + "/"
                    val filePath =
                        directoryPath + java.lang.Long.toHexString(System.currentTimeMillis()) + ".jpeg"
                    val data = File(directoryPath)
                    if (!data.exists()) {
                        data.mkdirs()
                    }
                    file = File(filePath)

                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    intent.putExtra(
                        MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(File(filePath))
                    )
                    startActivityForResult(intent, 20)
                } else {
                    file = null
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    intent.type = "image/"
                    startActivityForResult(
                        Intent.createChooser(
                            intent,
                            "Seleccione la Aplicación"
                        ), 10
                    )
                }
            } catch (e: Exception) {
                Log.e("errorcamera", e.message)
            }
        }

        alertOpciones.show()

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                10 -> {
                    val value = data!!.data
                    val dato = File(requireActivity().getPath(value!!))
                    //setea a la imagen
                    if (detectar_formato(dato.path) != "ninguno") {
                        file = dato
                        photoCombustible.setImageURI(value)

                    } else {
                        snakBar("Formato de imagen no valido")
                    }


                }

                20 -> {
                    if (file!!.exists()) {
                        val value = Uri.fromFile(file)
                        photoCombustible.setImageURI(value)


                        //setea a la imagen
                    }
                }
            }
        }
    }
    override fun getLayout(): Int= R.layout.fragment_dashboard
    override fun onTimeSet(view: TimePicker?, hourOfDay: Int, minute: Int) {
        hora=hourOfDay
        minuto=minute
        when(timeData){
             "Kinicio"->{
                 lbl_hora_inicio_fparte.setText("$hora:$minute hrs")
             }
            "Kfinal"->{
                lbl_hora_final_fparte.setText("$hora:$minute hrs")
            }
            "GarajeSalidainicio"->{
                lbl_horasalida_fparte.setText("$hora:$minute hrs")
            }
            "GarajeEntradaFinal"->{
                lbl_horaingreso_fparte.setText("$hora:$minute hrs")
            }
        }

    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        anio=year
        mes=month
        dia=dayOfMonth
        lbl_fecha_fparte.setText("$anio-$mes-$dia")
    }


}
