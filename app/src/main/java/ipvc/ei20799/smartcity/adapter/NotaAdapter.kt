@file:Suppress("SpellCheckingInspection")

package ipvc.ei20799.smartcity.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.dataclasses.Nota

class NotaAdapter(context: Context): RecyclerView.Adapter<LineViewHolder>(){

    private var notas = emptyList<Nota>()

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

        holder.titulo.text = currentNota.titulo
        holder.texto.text = currentNota.texto
    }

    internal fun setNotas(notas: List<Nota>){
        this.notas = notas
        notifyDataSetChanged()
    }
}

class LineViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    var titulo : TextView = itemView.findViewById(R.id.nota_line_titulo)
    var texto : TextView = itemView.findViewById(R.id.nota_line_texto)
}