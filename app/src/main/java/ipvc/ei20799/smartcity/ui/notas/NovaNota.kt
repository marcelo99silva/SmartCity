package ipvc.ei20799.smartcity.ui.notas

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContentProviderCompat.requireContext
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.dataclasses.Nota
import kotlinx.android.synthetic.main.activity_nova_nota.*
import java.text.SimpleDateFormat
import java.util.*

class NovaNota : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nova_nota)

        if (supportActionBar != null)
            supportActionBar?.hide()

        this.window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE)

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm")
        val currentDate = sdf.format(Date())
        val textView: TextView = newNoteDate as TextView
        textView.text = currentDate

        newNoteSave.setOnClickListener{
            val replyIntent = Intent()
            if(TextUtils.isEmpty(EditTextTituloNota.text) || TextUtils.isEmpty(EditTextDescricaoNota.text)){
                Toast.makeText(
                    this,
                    R.string.empty_not_saved,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else {
                var titulo = EditTextTituloNota.text.toString()
                var descricao = EditTextDescricaoNota.text.toString()
                var data = sdf.format(Date())

                replyIntent.putExtra(EXTRA_REPLY_TITULO, titulo)
                replyIntent.putExtra(EXTRA_REPLY_DESCRICAO, descricao)
                replyIntent.putExtra(EXTRA_REPLY_DATA, data)
                setResult(Activity.RESULT_OK, replyIntent)

                finish()
            }
        }
        newNoteDelete.setOnClickListener{
            finish()
        }
    }

    companion object {
        const val EXTRA_REPLY_TITULO = "com.example.android.titulo"
        const val EXTRA_REPLY_DESCRICAO = "com.example.android.descricao"
        const val EXTRA_REPLY_DATA = "com.example.android.data"
    }
}