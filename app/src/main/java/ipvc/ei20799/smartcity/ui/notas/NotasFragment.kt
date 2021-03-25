package ipvc.ei20799.smartcity.ui.notas

import android.app.Activity
import android.app.Application
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ipvc.ei20799.smartcity.MainActivity
import ipvc.ei20799.smartcity.NotasApplication
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.adapter.NotaAdapter
import ipvc.ei20799.smartcity.dataclasses.Nota
import ipvc.ei20799.smartcity.viewmodel.NotaViewModel
import ipvc.ei20799.smartcity.viewmodel.NotaViewModelFactory
import kotlinx.android.synthetic.main.fragment_notas.*
import kotlinx.android.synthetic.main.fragment_notas.view.*

class NotasFragment : Fragment() {

    private val notaViewModel: NotaViewModel by viewModels {
        NotaViewModelFactory((activity?.application as NotasApplication).repository)
    }
    private val newNotaActivityRequestCode = 1
    private lateinit var myList: ArrayList<Nota>

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_notas, container, false)

        /*myList = ArrayList<Nota>()
        for(i in 0 until 20){
            myList.add(Nota(id= "$i", titulo = "Titulo $i", texto = "$i ${getString(R.string.teste_nota)}", data = "$i/$i/2021 $i:$i"))
            print(myList[i])
        }*/

        val recyclerView: RecyclerView = root.recyclerNotas
        val adapter = NotaAdapter(requireContext())
        recyclerView.layoutManager = LinearLayoutManager(activity)
        recyclerView.adapter = adapter

        //notaViewModel = ViewModelProvider(this).get(NotaViewModel::class.java)
        // Add an observer on the LiveData returned by getAlphabetizedWords.
        // The onChanged() method fires when the observed data changes and the activity is
        // in the foreground.
        notaViewModel.allNotas.observe(viewLifecycleOwner) { notas ->
            // Update the cached copy of the words in the adapter.
            notas?.let { adapter.setNotas(it) }
        }

        root.botaoCriarNota.setOnClickListener{
            //(activity as MainActivity).clickCriarNota()
            val intent = Intent(requireContext(), NovaNota::class.java)
            startActivityForResult(intent, newNotaActivityRequestCode)
        }

        return root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newNotaActivityRequestCode && resultCode == Activity.RESULT_OK){
            val ntitulo = data?.getStringExtra(NovaNota.EXTRA_REPLY_TITULO)
            val ndescricao = data?.getStringExtra(NovaNota.EXTRA_REPLY_DESCRICAO)
            val ndata = data?.getStringExtra(NovaNota.EXTRA_REPLY_DATA)

            if (ntitulo!= null && ndescricao != null && ndata != null) {
                val nota = Nota(titulo = ntitulo, texto = ndescricao, data = ndata)
                notaViewModel.insert(nota)
            } else {
                Toast.makeText(
                    requireContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG
                 ).show()
            }
        }
    }

}