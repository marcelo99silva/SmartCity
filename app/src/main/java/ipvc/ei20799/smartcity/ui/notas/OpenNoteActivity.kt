package ipvc.ei20799.smartcity.ui.notas

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.viewModels
import ipvc.ei20799.smartcity.NotasApplication
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.viewmodel.NotaViewModel
import ipvc.ei20799.smartcity.viewmodel.NotaViewModelFactory
import kotlinx.android.synthetic.main.activity_nova_nota.*
import kotlinx.android.synthetic.main.activity_open_note.*
import java.text.SimpleDateFormat
import java.util.*

class OpenNoteActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_open_note)

        if (supportActionBar != null)
            supportActionBar?.hide()

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val notaViewModel: NotaViewModel by viewModels {
            NotaViewModelFactory((application as NotasApplication).repository)
        }

        var NotaId = intent.getStringExtra("notaId").toInt()
        var titulo = intent.getStringExtra("notaTitulo")
        var descricao = intent.getStringExtra("notaDescricao")
        var data = intent.getStringExtra("notaData")

        openEditTextTituloNota.setText(titulo)
        openEditTextDescricaoNota.setText(descricao)
        openNoteDate.setText(data)

        openNoteDelete.setOnClickListener{
            val builder = AlertDialog.Builder(it.context)
            builder.setMessage(R.string.confirmationDelete)
                .setCancelable(false)
                .setPositiveButton(R.string.sim) { dialog, id ->
                    // Delete selected note from database
                    Toast.makeText(
                        it.context,
                        R.string.deleteNote,
                        Toast.LENGTH_SHORT
                    ).show()
                    notaViewModel.deleteNotaById(NotaId!!)
                    finish()
                }
                .setNegativeButton(R.string.nao) { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
        openNoteSave.setOnClickListener{
            if(TextUtils.isEmpty(openEditTextTituloNota.text) || TextUtils.isEmpty(openEditTextDescricaoNota.text)){
                Toast.makeText(
                    this,
                    R.string.empty_not_saved,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
                val nTitulo = openEditTextTituloNota.text.toString()
                val nDescricao = openEditTextDescricaoNota.text.toString()
                val nData = sdf.format(Date())

                notaViewModel.updateNotaById(NotaId, nTitulo, nDescricao, nData)
                finish()
            }
        }
    }
}