package com.example.ahorasiahorano

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map:GoogleMap
    var latitud: Double = 0.0
    var longitud: Double = 0.0
    var latActual: Double = 0.0
    var longActual: Double = 0.0
    lateinit var localizacion: FusedLocationProviderClient
    lateinit var locationRequest: LocationRequest
    var refCatas: String = ""
    var refCatasActua: String = ""
    var pausa: Boolean = false
    lateinit var btnPausa: Button
    lateinit var btnReinicio: Button
    lateinit var puntos: String

    companion object{
        const val CODIGO_LOCAL = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
         btnPausa = findViewById(R.id.btnPausa)
         btnReinicio = findViewById(R.id.btnReinicio)
        createFragment()

        btnPausa.setOnClickListener {
            pausa = true
        }
        btnReinicio.setOnClickListener {
            datosLocalizacion()
        }
    }

    private fun createFragment() {
        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
        pedirLocalizacion()
    }

    private fun datosLocalizacion() {

        localizacion = LocationServices.getFusedLocationProviderClient(this)

        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return
        }

    localizacion.lastLocation.addOnSuccessListener { location ->
        if (location != null) {
            runOnUiThread {
                latitud = location.latitude
                longitud = location.longitude
                crearPosicion()
                Log.i("localizacion", "lat = " + latitud + "long = " + longitud)
                //getRefCatastral(latitud, longitud)
                    val obtenerRefCat = ObtenerRefCat(longitud, latitud)
                    obtenerRefCat.getRefCatastral { ref ->
                        runOnUiThread {
                            refCatas = ref
                            getPuntos(refCatas)
                        }
                    }
                    Log.i("refcatas", refCatas)
                }
        }
    }

    localizacion.requestLocationUpdates(locationRequest, object : LocationCallback() {
        override fun onLocationResult(p0: LocationResult) {
            p0 ?: return
            for (location in p0.locations) {
                latActual = location.latitude
                longActual = location.longitude
                Log.i("respuesta2", "" + latActual + "," + longActual)
                 //getRefCatastral2(latActual, longActual)
                val obtenerRefCat = ObtenerRefCat(longActual, latActual)
                obtenerRefCat.getRefCatastral { ref ->
                    runOnUiThread {
                        refCatasActua = ref
                    }
                }
                Log.i("refcataActual", refCatasActua)
                comprobar()
            }
        }
    }, null)
    }
    private fun comprobar() {
        if (refCatas.equals(refCatasActua) ){
            Log.i("comprobar","estas en el mismo lao")
            pausa = false
            btnPausa.isVisible = false
        }else{
            btnPausa.isVisible = true
            Log.e("comprobar","Has cambiado")
            val mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.megaman_x_error)
            //val parpadea: Animation = AnimationUtils.loadAnimation()
            if(!pausa){
                mediaPlayer.start()
            }
        }
    }

    fun getPuntos(ref: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat=$ref&amp;srsname=EPSG::25830")
                val connection = url.openConnection()
                connection.setRequestProperty("Accept", "application/gml+xml")
                val inputStream = connection.getInputStream()
                val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
                val posListElement = document.getElementsByTagName("gml:posList").item(0)
                val posList = posListElement.textContent.trim().split(" ")
                println(posList)
                puntos = posList.toString()
                dibujarPoligono(puntos)
                Log.i("puntos", posList.toString())
            } catch (e: Exception) {
                Log.i("puntos", e.message.toString())
            }
        }
    }

    private fun dibujarPoligono(puntos: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val coordenadas = puntos.replace("[", "").replace("]","").split(", ")
                Log.i("polipunto",  coordenadas.toString())
                val pares = coordenadas.chunked(2){
                    Pair(it[0], it[1])
                }
                val polygonOptions = PolygonOptions()

                for (i in pares){
                    polygonOptions.add(LatLng(i.first.toDouble(), i.second.toDouble()))
                    Log.i("polipunto",  "long: "+i.first+" lat: "+ i.second)
                }

                //polygonOptions.addAll(listaCoordenadas)
                polygonOptions.strokeWidth(5f)
                polygonOptions.strokeColor(Color.RED)
                polygonOptions.fillColor(Color.BLUE)
                map.addPolygon(polygonOptions)
            }catch (e: java.lang.Exception){
                Log.i("polipuntos", e.message.toString())
            }
        }
    }

    private fun crearPosicion() {
        val coordenada = LatLng(latitud,longitud)
        //val posicion : MarkerOptions = MarkerOptions().position(coordenada).title("mi posición")
        //map.addMarker(posicion)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada, 18f), 4000,null)
    }
    //comprobar permisos
    private fun isPermisos() = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    //solicitar localización y pregunto si los permisos están aceptados
    private fun pedirLocalizacion(){
        if(!::map.isInitialized) return
        if(isPermisos()){

            datosLocalizacion()

            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = true
        }else{
            pedirPermiso()
        }
    }
    //solicitar permisos de localización
    private fun pedirPermiso() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Acepta los permisos en Ajustes", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), CODIGO_LOCAL)
        }
    }
    //se comprueba si el permiso está aceptado
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            CODIGO_LOCAL -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return
                }
                map.isMyLocationEnabled = true
            }else{
                Toast.makeText(this, "Acepta los permisos en Ajustes", Toast.LENGTH_SHORT).show()
            } else ->{}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isPermisos()){
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return
            }
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Acepta los permisos en Ajustes", Toast.LENGTH_SHORT).show()
        }
    }


}