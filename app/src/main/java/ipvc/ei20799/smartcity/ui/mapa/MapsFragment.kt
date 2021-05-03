package ipvc.ei20799.smartcity.ui.mapa

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.Gravity.apply
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContentProviderCompat.requireContext
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.*
import ipvc.ei20799.smartcity.R
import ipvc.ei20799.smartcity.activities.MainActivity
import ipvc.ei20799.smartcity.adapter.CustomInfoWindowAdapter
import ipvc.ei20799.smartcity.api.EndPoints
import ipvc.ei20799.smartcity.api.ServiceBuilder
import ipvc.ei20799.smartcity.dataclasses.LoginResponse
import ipvc.ei20799.smartcity.dataclasses.Report
import ipvc.ei20799.smartcity.storage.SharedPrefManager
import ipvc.ei20799.smartcity.ui.notas.NovaNota
import kotlinx.android.synthetic.main.fragment_login.*
import kotlinx.android.synthetic.main.fragment_maps.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.Serializable
import java.math.BigDecimal

class MapsFragment : Fragment() {
    private var userId: Int = 0
    private lateinit var reports: List<Report>
    private lateinit var map: GoogleMap
    // variaveis para pedido de ultima localização
    lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // variaveis para pedidos periodicos da localização
    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    var first: Boolean = true
    var distancia: Int = 0
    var tipo: Int = 0

    private val callback = OnMapReadyCallback { googleMap ->
        map = googleMap
        //map.setInfoWindowAdapter(CustomInfoWindowAdapter(requireContext()))
        /**
         * Manipulates the map once available.
         * This callback is triggered when the map is ready to be used.
         * This is where we can add markers or lines, add listeners or move the camera.
         * In this case, we just add a marker near Sydney, Australia.
         * If Google Play services is not installed on the device, the user will be prompted to
         * install it inside the SupportMapFragment. This method will only be triggered once the
         * user has installed Google Play services and returned to the app.
         */

        // carregar pontos dos reports para o mapa
        loadReports()

        // método 1 Last Known Location
        // loadLastLocation()

        //método 2 Request Location Update
        createLocationRequest()

        map.setOnMarkerClickListener { marker ->
            if(marker.snippet.toInt() == userId) {
                val intent = Intent(requireContext(), EditReport::class.java)
                intent.putExtra("report", marker.tag as Serializable)
                startActivity(intent)

            }else{
                val intent = Intent(requireContext(), ViewReport::class.java)
                intent.putExtra("report", marker.tag as Serializable)
                startActivity(intent)
            }
            true
        }
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    private fun loadLastLocation() {
        if( ActivityCompat.checkSelfPermission(this.requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(requireActivity(),
                arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {
            map.isMyLocationEnabled = true
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15.0f))
                }
            }
        }
    }

    private fun loadReports() {
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReports()

        call.enqueue(object : Callback<List<Report>> {
            override fun onResponse(
                call: Call<List<Report>>,
                response: Response<List<Report>>
            ) {
                if (response.body() != null) {
                    reports = response.body()!!
                    populateMap(distancia, 0)
                    /*var positionR: LatLng
                    var marker: Marker
                    for (report in reports) {
                        positionR = LatLng(
                            report.latitude.toDouble(),
                            report.longitude.toDouble()
                        )
                        // se report for do user logado, marker azul
                        if (userId == report.user_id.toInt()) {
                            marker = map.addMarker(
                                MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                    .position(positionR)
                                    .title(report.title)
                                    .snippet(report.user_id)
                            )
                        }
                        // se report for obras, marker laranja
                        else if (report.type_id.toInt() == 2) {
                            marker = map.addMarker(
                                MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                    .position(positionR)
                                    .title(report.title)
                                    .snippet(report.user_id)
                            )
                        } else {
                            marker = map.addMarker(
                                MarkerOptions().position(positionR).title(report.title)
                                    .snippet(report.user_id)
                            )
                        }
                        marker.tag = report
                    }*/
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onCreateView(inflater: LayoutInflater,
                              container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        userId = SharedPrefManager.getInstance(requireContext()).user.id.toInt()
        return inflater.inflate(R.layout.fragment_maps, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        mapFragment?.getMapAsync(callback)

        // initialize fusedLocationClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this.requireActivity())
        // added to implement location periodic updates
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                super.onLocationResult(p0)
                lastLocation = p0.lastLocation
                if(first){
                    first = false
                    val loc = LatLng(lastLocation.latitude, lastLocation.longitude)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15.0f))
                }
                if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(requireActivity(),
                            arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
                }else{
                    map.isMyLocationEnabled = true
                }
            }
        }
        createLocationRequest()

        newReport.setOnClickListener {
            val intent = Intent(requireContext(), NewReport::class.java)
            startActivity(intent)
        }


        radiosFiltros.setOnCheckedChangeListener{ radioGroup, optionId ->
            run {
                if( this::reports.isInitialized ) {
                    when (optionId) {
                        R.id.radioButton1 -> {
                            tipo = 0
                            populateMap(distancia, 0)
                            /*map.clear()
                            var marker: Marker
                            for (report in reports) {
                                val positionR: LatLng = LatLng(report.latitude.toDouble(),
                                        report.longitude.toDouble())
                                // se report for do user logado, marker azul
                                if ( userId == report.user_id.toInt()) {
                                    marker = map.addMarker(
                                            MarkerOptions()
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                                    .position(positionR)
                                                    .title(report.title)
                                                    .snippet(report.user_id))
                                }
                                // se report for obras, marker laranja
                                else if (report.type_id.toInt() == 2) {
                                    marker = map.addMarker(
                                            MarkerOptions()
                                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                                    .position(positionR)
                                                    .title(report.title)
                                                    .snippet(report.user_id))
                                } else {
                                    marker = map.addMarker(MarkerOptions().position(positionR).title(report.title).snippet(report.user_id))
                                }
                                marker.tag = report
                            }*/
                        }
                        R.id.radioButton2 -> {
                            tipo = 1
                            populateMap(distancia, 1)
                            /*map.clear()
                            var marker: Marker
                            for (report in reports){
                                val positionR: LatLng = LatLng(report.latitude.toDouble(),
                                        report.longitude.toDouble())
                                if (report.type_id.toInt() == 1){
                                    if( userId == report.user_id.toInt() ){
                                        marker = map.addMarker(
                                                MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                                        .position(positionR)
                                                        .title(report.title)
                                                        .snippet(report.user_id))
                                    } else {
                                        marker = map.addMarker(MarkerOptions().position(positionR).title(report.title).snippet(report.user_id))
                                    }
                                    marker.tag = report
                                }
                            }*/
                        }
                        R.id.radioButton3 -> {
                            tipo = 2

                            populateMap(distancia, 2)
                            /*map.clear()
                            var marker: Marker
                            for (report in reports){
                                val positionR: LatLng = LatLng(report.latitude.toDouble(),
                                        report.longitude.toDouble())
                                if (report.type_id.toInt() == 2) {
                                    if( userId == report.user_id.toInt() ){
                                        marker = map.addMarker(
                                                MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                                        .position(positionR)
                                                        .title(report.title)
                                                        .snippet(report.user_id))
                                    } else {
                                        marker = map.addMarker(
                                                MarkerOptions()
                                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                                        .position(positionR)
                                                        .title(report.title)
                                                        .snippet(report.user_id))
                                    }
                                    marker.tag = report
                                }
                            }*/
                        }
                    }
                }

            }
        }

        buttonDistancia.setOnClickListener {
            val builder = AlertDialog.Builder(requireContext())
            val inflater = layoutInflater
            val dialogLayout = inflater.inflate(R.layout.alert_dialog_distancia, null)
            val numberPicker  = dialogLayout.findViewById<NumberPicker>(R.id.numberPickerDistancia)
            numberPicker.minValue = 0
            numberPicker.maxValue = 20000
            numberPicker.wrapSelectorWheel = true
            numberPicker.value = distancia
            numberPicker.setOnValueChangedListener { picker, oldVal, newVal ->
                distancia = newVal
            }
            builder.setMessage(R.string.filtroDistancia)
                    .setView(dialogLayout)
                    .setPositiveButton(R.string.apply) { dialog, i ->
                        populateMap(distancia, tipo)
                    }
                    .setNegativeButton(R.string.removerFiltro) { dialog, id ->
                        // Dismiss the dialog
                        distancia = 0
                        populateMap(distancia, tipo)
                    }
            builder.show()
        }
    }

    private fun filtrarDistancia(distancia: Int): MutableList<Report> {
        Toast.makeText(requireContext(), distancia.toString(), Toast.LENGTH_SHORT).show()
        val reportsInserir = emptyList<Report>().toMutableList()
        for(report in reports){
            val distToReport = calculateDistance(lastLocation.latitude, lastLocation.longitude, report.latitude.toDouble(), report.longitude.toDouble())
            if ( distToReport <= distancia.toFloat() ) {
                reportsInserir += report //= report
            }
        }
        for(report in reportsInserir){
            Log.d("******* SMARTCITY *****", report.title)
        }
        return reportsInserir
    }

    private fun populateMap(distancia: Int, tipo: Int) {
        map.clear()
        if (distancia == 0) {
            // insere por tipo
            for (report in reports) {
                val marker: Marker
                val positionR: LatLng = LatLng(report.latitude.toDouble(),
                        report.longitude.toDouble())
                if (tipo == 0) {
                    //insere todos
                    // se report for do user logado, marker azul
                    if ( userId == report.user_id.toInt()) {
                        marker = map.addMarker(
                                MarkerOptions()
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                        .position(positionR)
                                        .title(report.title)
                                        .snippet(report.user_id))
                    }
                    // se report for obras, marker laranja
                    else if (report.type_id.toInt() == 2) {
                        marker = map.addMarker(
                                MarkerOptions()
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                        .position(positionR)
                                        .title(report.title)
                                        .snippet(report.user_id))
                    } else {
                        marker = map.addMarker(MarkerOptions().position(positionR).title(report.title).snippet(report.user_id))
                    }
                    marker.tag = report
                }
                else if (tipo == 1) {
                    //insere acidentes
                    // se report for do user logado, marker azul
                    if (report.type_id.toInt() == 1){
                        if( userId == report.user_id.toInt() ){
                            marker = map.addMarker(
                                    MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .position(positionR)
                                            .title(report.title)
                                            .snippet(report.user_id))
                        } else {
                            marker = map.addMarker(MarkerOptions().position(positionR).title(report.title).snippet(report.user_id))
                        }
                        marker.tag = report
                    }
                }
                else if (tipo == 2) {
                    //insere obras
                    // se report for do user logado, marker azul
                    if (report.type_id.toInt() == 2) {
                        if (userId == report.user_id.toInt()) {
                            marker = map.addMarker(
                                    MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .position(positionR)
                                            .title(report.title)
                                            .snippet(report.user_id))
                        } else {
                            marker = map.addMarker(
                                    MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                            .position(positionR)
                                            .title(report.title)
                                            .snippet(report.user_id))
                        }
                        marker.tag = report
                    }
                }
            }
        }
        else {
            map.addCircle(CircleOptions()
                    .center(LatLng(lastLocation.latitude, lastLocation.longitude))
                    .radius(distancia.toDouble()) )

            val reportsDentroDistancia = filtrarDistancia(distancia)

            for(report in reportsDentroDistancia) {

                val marker: Marker
                val positionR: LatLng = LatLng(report.latitude.toDouble(),
                        report.longitude.toDouble())
                if (tipo == 0) {
                    //insere todos os tipos
                    // se report for do user logado, marker azul
                    if ( userId == report.user_id.toInt()) {
                        marker = map.addMarker(
                                MarkerOptions()
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                        .position(positionR)
                                        .title(report.title)
                                        .snippet(report.user_id))
                    }
                    // se report for obras, marker laranja
                    else if (report.type_id.toInt() == 2) {
                        marker = map.addMarker(
                                MarkerOptions()
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                        .position(positionR)
                                        .title(report.title)
                                        .snippet(report.user_id))
                    } else {
                        marker = map.addMarker(MarkerOptions().position(positionR).title(report.title).snippet(report.user_id))
                    }
                    marker.tag = report
                }
                else if (tipo == 1) {
                    //insere acidentes
                    // se report for do user logado, marker azul
                    if (report.type_id.toInt() == 1){
                        if( userId == report.user_id.toInt() ){
                            marker = map.addMarker(
                                    MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .position(positionR)
                                            .title(report.title)
                                            .snippet(report.user_id))
                        } else {
                            marker = map.addMarker(MarkerOptions().position(positionR).title(report.title).snippet(report.user_id))
                        }
                        marker.tag = report
                    }
                }
                else if (tipo == 2) {
                    //insere obras
                    // se report for do user logado, marker azul
                    if (report.type_id.toInt() == 2) {
                        if (userId == report.user_id.toInt()) {
                            marker = map.addMarker(
                                    MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                            .position(positionR)
                                            .title(report.title)
                                            .snippet(report.user_id))
                        } else {
                            marker = map.addMarker(
                                    MarkerOptions()
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                            .position(positionR)
                                            .title(report.title)
                                            .snippet(report.user_id))
                        }
                        marker.tag = report
                    }
                }
            }
        }


        /*var positionR: LatLng
        var marker: Marker
        for(report in reportsDentroDistancia){
            val distToReport = calculateDistance(lastLocation.latitude, lastLocation.longitude, report.latitude.toDouble(), report.longitude.toDouble())
            if ( distToReport <= distancia.toFloat() ) {
                positionR = LatLng(
                        report.latitude.toDouble(),
                        report.longitude.toDouble()
                )
                // se report for do user logado, marker azul
                if (userId == report.user_id.toInt()) {
                    marker = map.addMarker(
                            MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                                    .position(positionR)
                                    .title(report.title)
                                    .snippet(report.user_id)
                    )
                }
                // se report for obras, marker laranja
                else if (report.type_id.toInt() == 2) {
                    marker = map.addMarker(
                            MarkerOptions()
                                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                                    .position(positionR)
                                    .title(report.title)
                                    .snippet(report.user_id)
                    )
                } else {
                    marker = map.addMarker(
                            MarkerOptions().position(positionR).title(report.title)
                                    .snippet(report.user_id)
                    )
                }
                marker.tag = report
            }
        }*/
    }

    private fun calculateDistance(latitude1: Double, longitude1: Double, latitude2: Double, longitude2: Double): Float {
        val results = FloatArray(1)
        Location.distanceBetween(latitude1, longitude1, latitude2, longitude2, results)
        return results[0]
    }

    override fun onPause() {
        super.onPause()
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    override fun onResume() {
        super.onResume()
        startLocationUpdates()
        if (!first) {
            map.clear()
        }
        loadReports()
        radiosFiltros.check(radioButton1.id)
    }

    private fun startLocationUpdates() {
        if( ActivityCompat.checkSelfPermission(this.requireContext(),
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ){
            ActivityCompat.requestPermissions(requireActivity(),
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), 0)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

}