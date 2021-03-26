@file:Suppress("SpellCheckingInspection")

package ipvc.ei20799.smartcity.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.RecyclerView
import ipvc.ei20799.smartcity.NotasApplication
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.dataclasses.Nota
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
        val id = currentNota.id

        holder.titulo.text = currentNota.titulo
        holder.texto.text = currentNota.texto

        holder.itemView.setOnClickListener{
            Toast.makeText(
                it.context,
                "Open note: $id",
                Toast.LENGTH_SHORT
            ).show()
        }
        holder.deleteNote.setOnClickListener{
            Toast.makeText(
                it.context,
                R.string.deleteNote,
                Toast.LENGTH_SHORT
            ).show()
            notaViewModel.deleteNotaById(id!!)
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