package ipvc.ei20799.smartcity.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.text.TextUtils
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import ipvc.ei20799.smartcity.activities.MainActivity
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.api.EndPoints
import ipvc.ei20799.smartcity.api.ServiceBuilder
import ipvc.ei20799.smartcity.dataclasses.LoginResponse
import ipvc.ei20799.smartcity.storage.SharedPrefManager
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_login.view.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

class LoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment
        val root = inflater.inflate(R.layout.fragment_login, container, false)

        root.loginBt.setOnClickListener{
            val email = emailET.text.toString().trim()
            val pass = passwordET.text.toString().trim()

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass)){
                Toast.makeText(
                    requireContext(),
                    R.string.empty_not_saved,
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.userLogin(email, pass)

                call.enqueue(object : Callback<LoginResponse> {
                    override fun onResponse(
                        call: Call<LoginResponse>,
                        response: Response<LoginResponse>
                    ) {
                        if (!response.body()?.error!!) {
                            if(loginCheckBox.isChecked){
                                SharedPrefManager.getInstance(requireContext()).saveUser(response.body()!!.user)
                            }
                            else{
                                SharedPrefManager.getInstance(requireContext()).saveId(response.body()!!.user.id)
                            }
                            val intent = Intent(requireContext(), MainActivity::class.java).apply {
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(requireContext(), R.string.login_error, Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                        Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
                    }
                })
            }
        }

        return root
    }

}