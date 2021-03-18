package ipvc.ei20799.smartcity.ui.conta

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ipvc.ei20799.smartcity.R

class ContaFragment : Fragment() {

    private lateinit var contaViewModel: ContaViewModel

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        contaViewModel =
                ViewModelProvider(this).get(ContaViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_conta, container, false)
        val textView: TextView = root.findViewById(R.id.text_notifications)
        contaViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}