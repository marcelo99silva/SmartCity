package ipvc.ei20799.smartcity.ui.mapa

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.text.TextUtils
import android.util.Base64
import android.view.MotionEvent
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.graphics.drawable.toBitmap
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.activities.MainActivity
import ipvc.ei20799.smartcity.api.EndPoints
import ipvc.ei20799.smartcity.api.ServiceBuilder
import ipvc.ei20799.smartcity.dataclasses.NewReport
import ipvc.ei20799.smartcity.dataclasses.Report
import ipvc.ei20799.smartcity.dataclasses.UpdateReport
import kotlinx.android.synthetic.main.activity_edit_report.*
import kotlinx.android.synthetic.main.activity_new_report.*
import kotlinx.android.synthetic.main.activity_view_report.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

private const val CODE_TIRAR_FOTO = 1
private const val CODE_ESCOLHER_FOTO = 2;
private const val CODE_PERMISSION = 3;

class EditReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_report)

        val report = intent.extras?.get("report") as Report

        val decodedString: ByteArray = Base64.decode(report.image, Base64.DEFAULT)
        val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)

        // Create an ArrayAdapter
        val adapter = ArrayAdapter.createFromResource(this,
            R.array.tipos, android.R.layout.simple_spinner_item)
        SpinnerEditReport.adapter = adapter

        EditTituloReport.setText(report.title)
        SpinnerEditReport.setSelection(report.type_id.toInt()-1)
        EditDescricaoReport.setText(report.description)
        editImage.setImageBitmap(decodedByte)
        editReportDate.text = report.time

        EditDescricaoReport.setOnTouchListener { view, event ->
            view.parent.requestDisallowInterceptTouchEvent(true)
            if ((event.action and MotionEvent.ACTION_MASK) == MotionEvent.ACTION_UP) {
                view.parent.requestDisallowInterceptTouchEvent(false)
            }
            return@setOnTouchListener false
        }

        editTirarFoto.setOnClickListener {
            val tirarPhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

            if(tirarPhotoIntent.resolveActivity(this.packageManager) != null){
                startActivityForResult(tirarPhotoIntent, CODE_TIRAR_FOTO)
            }else{
                Toast.makeText(this, R.string.erro, Toast.LENGTH_SHORT).show()
            }
        }

        editEscolherFoto.setOnClickListener {
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

        editReportSave.setOnClickListener{
            editReport(report)
        }
        editReportDelete.setOnClickListener{
            val builder = AlertDialog.Builder(it.context)
            builder.setMessage(R.string.confirmationDelete)
                .setCancelable(false)
                .setPositiveButton(R.string.sim) { dialog, id ->

                    val request = ServiceBuilder.buildService(EndPoints::class.java)
                    val call = request.deleteReport(report.id)
                    call.enqueue(object : Callback<UpdateReport> {
                        override fun onResponse(
                            call: Call<UpdateReport>,
                            response: Response<UpdateReport>
                        ) {
                            if (response.isSuccessful) {
                                Toast.makeText(applicationContext, R.string.deleteReport, Toast.LENGTH_SHORT).show()
                                finish()
                            }
                        }
                        override fun onFailure(call: Call<UpdateReport>, t: Throwable) {
                            Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
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
            editImage.setImageBitmap(takenImage)
        }
        else if( requestCode == CODE_ESCOLHER_FOTO && resultCode == Activity.RESULT_OK ){
            editImage.setImageURI(data?.data)
        }
        else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    private fun editReport(report: Report) {
        if(TextUtils.isEmpty(EditTituloReport.text) || TextUtils.isEmpty(EditDescricaoReport.text)){
            Toast.makeText(
                this,
                R.string.empty_not_saved,
                Toast.LENGTH_SHORT
            ).show()
            return
        } else {
            var type_id = 0
            val typeString = SpinnerEditReport.selectedItem.toString()
            val title = EditTituloReport.text.toString()
            val description = EditDescricaoReport.text.toString()

            val image = editImage
            val bitmap= image.drawable.toBitmap()
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val encodedImage = android.util.Base64.encodeToString(byteArrayOutputStream.toByteArray(), android.util.Base64.DEFAULT)

            if ( typeString == "Acidente" || typeString == "Accident"){
                type_id = 1
            } else if (typeString == "Obras" || typeString == "Accident") {
                type_id = 2
            }

            val request = ServiceBuilder.buildService(EndPoints::class.java)
            val call = request.updateReport(report.id, type_id, title, description, encodedImage)
            call.enqueue(object : Callback<UpdateReport> {
                override fun onResponse(
                    call: Call<UpdateReport>,
                    response: Response<UpdateReport>
                ) {
                    if (response.isSuccessful) {
                        Toast.makeText(applicationContext, R.string.updatedReport, Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
                override fun onFailure(call: Call<UpdateReport>, t: Throwable) {
                    Toast.makeText(applicationContext, "${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun deleteReport(report: Report) {

    }
}