@file:Suppress("SpellCheckingInspection")

package ipvc.ei20799.smartcity.adapter

import android.app.AlertDialog
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import ipvc.ei20799.smartcity.NotasApplication
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.dataclasses.Nota
import ipvc.ei20799.smartcity.ui.notas.OpenNoteActivity
import ipvc.ei20799.smartcity.viewmodel.NotaViewModel
import ipvc.ei20799.smartcity.viewmodel.NotaViewModelFactory
import kotlinx.android.synthetic.main.nota_recycler_line.view.*

class NotaAdapter(application: NotaViewModel): RecyclerView.Adapter<LineViewHolder>(){
    /*private val notaViewModel: NotaViewModel by viewModels {
        NotaViewModelFactory((application as NotasApplication).repository)
    }*/
    private var notas = emptyList<Nota>()
    var notaViewModel = application

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineViewHolder {
        val itemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.nota_recycler_line, parent, false)
        return LineViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return notas.size
    }

    override fun onBindViewHolder(holder: LineViewHolder, position: Int) {
        val currentNota = notas[position]
        val NotaId = currentNota.id

        holder.titulo.text = currentNota.titulo
        holder.texto.text = currentNota.texto

        holder.itemView.setOnClickListener{

            val intent = Intent(it.context, OpenNoteActivity::class.java)
            // To pass any data to next activity
            intent.putExtra("notaId", NotaId.toString())
            intent.putExtra("notaTitulo", currentNota.titulo)
            intent.putExtra("notaDescricao", currentNota.texto)
            intent.putExtra("notaData", currentNota.data)
            // Start your next activity
            it.context.startActivity(intent)

        }
        holder.deleteNote.setOnClickListener{
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
                }
                .setNegativeButton(R.string.nao) { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    internal fun setNotas(notas: List<Nota>){
        this.notas = notas
        notifyDataSetChanged()
    }
}

class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var titulo : TextView = itemView.nota_line_titulo
    var texto : TextView = itemView.nota_line_texto
    var deleteNote : ImageView = itemView.deleteNote
}