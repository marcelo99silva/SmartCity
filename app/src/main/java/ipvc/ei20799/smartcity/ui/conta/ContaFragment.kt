package ipvc.ei20799.smartcity.ui.conta

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.activities.MainActivity
import ipvc.ei20799.smartcity.activities.MainActivityLogin
import ipvc.ei20799.smartcity.storage.SharedPrefManager
import kotlinx.android.synthetic.main.fragment_conta.view.*
import kotlinx.android.synthetic.main.fragment_login.view.*

class ContaFragment : Fragment() {

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {

        val root = inflater.inflate(R.layout.fragment_conta, container, false)

        root.logoutBt.setOnClickListener{
            SharedPrefManager.getInstance(requireContext()).clear()

            val intent = Intent(requireContext(), MainActivityLogin::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }

        return root
    }
}