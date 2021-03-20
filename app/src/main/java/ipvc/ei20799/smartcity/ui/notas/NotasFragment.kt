package ipvc.ei20799.smartcity.ui.notas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListAdapter
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ipvc.ei20799.smartcity.MainActivity
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.adapter.NotaAdapter
import ipvc.ei20799.smartcity.dataclasses.Nota
import kotlinx.android.synthetic.main.fragment_notas.*
import kotlinx.android.synthetic.main.fragment_notas.view.*

class NotasFragment : Fragment() {

    private lateinit var notasViewModel: NotasViewModel
    private lateinit var myList: ArrayList<Nota>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        notasViewModel =
                ViewModelProvider(this).get(NotasViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_notas, container, false)

        myList = ArrayList<Nota>()
        for(i in 0 until 20){
            myList.add(Nota("Titulo $i", "$i ${getString(R.string.teste_nota)}"))
            print(myList[i])
        }

        val recyclerView: RecyclerView = root.findViewById(R.id.recyclerNotas)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = NotaAdapter(myList)

        root.botaoCriarNota.setOnClickListener{
            clickCriarNota()
        }

        return root
    }

    fun clickCriarNota(){
        Toast.makeText(context, "You clicked me.", Toast.LENGTH_SHORT).show()
    }

}