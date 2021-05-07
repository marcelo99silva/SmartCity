package ipvc.ei20799.smartcity.ui.mapa

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Log
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.api.EndPoints
import ipvc.ei20799.smartcity.api.ServiceBuilder
import ipvc.ei20799.smartcity.dataclasses.NewReport
import ipvc.ei20799.smartcity.storage.SharedPrefManager
import ipvc.ei20799.smartcity.ui.notas.NovaNota
import kotlinx.android.synthetic.main.activity_edit_report.*
import kotlinx.android.synthetic.main.activity_new_report.*
import kotlinx.android.synthetic.main.activity_nova_nota.*
import kotlinx.android.synthetic.main.activity_open_note.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

private const val CODE_TIRAR_FOTO = 1
private const val CODE_ESCOLHER_FOTO = 2;
private const val CODE_PERMISSION = 3;

class NewReport : AppCompatActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // add to implement last known location
    private lateinit var lastLocation: Location

    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_report)

        if (supportActionBar != null)
            supportActionBar?.hide()

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val currentDate = sdf.format(Date())
        newReportDate.text = currentDate

        // Create an ArrayAdapter
        val adapter = ArrayAdapter.createFromResource(this,
            R.array.tipos, android.R.layout.simple_spinner_item)
        SpinnerNewReport.adapter = adapter

        EditTextDescricaoReport.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }

        // initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        checkPermissions()

        bTirarFoto.setOnClickListener {
            val tirarPhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(tirarPhotoIntent.resolveActivity(this.packageManager) != null){
                startActivityForResult(tirarPhotoIntent, CODE_TIRAR_FOTO)
            }else{
                Toast.makeText(this, R.string.erro, Toast.LENGTH_SHORT).show()
            }
        }

        bEscolherFoto.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    requestPermissions(permissions, CODE_PERMISSION)
                }else{
                    escolherImagem()
                }
            }else{
                escolherImagem()
            }
        }

        newReportSave.setOnClickListener{
            createReport()
        }
        newReportDelete.setOnClickListener{
            val builder = AlertDialog.Builder(it.context)
            builder.setMessage(R.string.confirmationDelete)
                    .setCancelable(false)
                    .setPositiveButton(R.string.sim) { dialog, id ->
                        Toast.makeText(
                                it.context,
                                R.string.deleteReport,
                                Toast.LENGTH_SHORT
                        ).show()
                        finish()
                    }
                    .setNegativeButton(R.string.nao) { dialog, id ->
                        // Dismiss the dialog
                        dialog.dismiss()
                    }
            val alert = builder.create()
            alert.show()
        }

    }

    private fun escolherImagem() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, CODE_ESCOLHER_FOTO)
    }

    private fun checkPermissions(){
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
        }else{
            getLastLocation()
        }
    }

    private fun getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        fusedLocationClient.lastLocation?.addOnSuccessListener {
            location : Location? ->
            if (location != null) {
                lastLocation = location
            }
        }
    }

    private fun createReport(){
        if(TextUtils.isEmpty(EditTextTituloReport.text) || TextUtils.isEmpty(EditTextDescricaoReport.text)){
            Toast.makeText(
                this,
                R.string.empty_not_saved,
                Toast.LENGTH_SHORT
            ).show()
            return
        }
        else{
            val user_id = SharedPrefManager.getInstance(this).user.id
            var type_id = 0
            val typeString = SpinnerNewReport.selectedItem.toString()
            val title = EditTextTituloReport.text.toString()
            val description = EditTextDescricaoReport.text.toString()
            val latitude = lastLocation.latitude.toBigDecimal()
            val longitude = lastLocation.longitude.toBigDecimal()

            val image = ivImage
            val bitmap= image.drawable.toBitmap()
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val encodedImage = android.util.Base64.encodeToString(byteArrayOutputStream.toByteArray(), android.util.Base64.DEFAULT)

            if ( typeString == "Acidente" || typeString == "Accident"){
                type_id = 1
            } else if (typeString == "Obras" || typeString == "Roadwork") {
                type_id = 2
            }

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.addReport(user_id, type_id, title, description, encodedImage, latitude, longitude)
            call.enqueue(object : Callback<NewReport> {
                override fun onResponse(
                        call: Call<NewReport>,
                        response: Response<NewReport>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, R.string.createdReport, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onFailure(call: Call<NewReport>, t: Throwable) {
                    Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })

        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CODE_PERMISSION -> {
                if(grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    escolherImagem()
                } else{
                    Toast.makeText(this, R.string.noPermission, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if(requestCode == CODE_TIRAR_FOTO && resultCode == Activity.RESULT_OK){
            val takenImage = data?.extras?.get("data") as Bitmap
            ivImage.setImageBitmap(takenImage)
        }
        else if( requestCode == CODE_ESCOLHER_FOTO && resultCode == Activity.RESULT_OK ){
            ivImage.setImageURI(data?.data)
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}