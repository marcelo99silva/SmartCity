package ipvc.ei20799.smartcity.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ipvc.ei20799.smartcity.MainActivity
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.ui.notas.NovaNota
import kotlinx.android.synthetic.main.activity_nova_nota.*
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import kotlinx.android.synthetic.main.fragment_notas.view.*

class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // check shared pref
        // if  login intent mapactivity
        // if !login intent loginactivity

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_login, container, false)

        root.loginBt.setOnClickListener{
            if(TextUtils.isEmpty(emailET.text) || TextUtils.isEmpty(passwordET.text)){
                Toast.makeText(
                    requireContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                val intent = Intent(requireContext(), MainActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(intent)
            }
        }

        return root
    }

}