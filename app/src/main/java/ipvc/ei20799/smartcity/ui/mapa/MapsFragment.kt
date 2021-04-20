package ipvc.ei20799.smartcity.ui.mapa

import android.content.Intent
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.activities.MainActivity
import ipvc.ei20799.smartcity.api.EndPoints
import ipvc.ei20799.smartcity.api.ServiceBuilder
import ipvc.ei20799.smartcity.dataclasses.LoginResponse
import ipvc.ei20799.smartcity.dataclasses.Report
import ipvc.ei20799.smartcity.storage.SharedPrefManager
import kotlinx.android.synthetic.main.fragment_login.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapsFragment : Fragment() {
    private lateinit var map: GoogleMap
    private lateinit var reports: List<Report>

    private val callback = OnMapReadyCallback { googleMap ->
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        // chamar webservice
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReports()

        call.enqueue(object : Callback<List<Report>> {
            override fun onResponse(
                call: Call<List<Report>>,
                response: Response<List<Report>>
            ) {
                reports = response.body()!!
                var positionR: LatLng
                for (report in reports){
                    positionR = LatLng(report.latitude.toDouble(),
                        report.longitude.toDouble())
                    // se report for do user logado, marker azul
                    if( SharedPrefManager.getInstance(requireContext()).user.id.toInt() == report.idUser.toInt() ){
                        googleMap.addMarker(
                            MarkerOptions()
                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                            .position(positionR).title(report.title))
                    }
                    // se report for obras, marker laranja
                    else if (report.idType.toInt() == 2){
                        googleMap.addMarker(
                            MarkerOptions()
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                .position(positionR).title(report.title))
                    }
                    else{
                        googleMap.addMarker(MarkerOptions().position(positionR).title(report.title))
                    }
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(41.701856, -8.817862), 13.0f))
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {


        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)
    }
}