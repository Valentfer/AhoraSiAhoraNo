package com.example.ahorasiahorano

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
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
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import java.net.HttpURLConnection
import java.net.URL

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


    companion object{
        const val CODIGO_LOCAL = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        createFragment()

    }

    private fun createFragment() {
        val mapFragment : SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
       // crearPosicion()
        pedirLocalizacion()
       // datosLocalizacion()
        //dibujarPoligono()
    }

    private fun dibujarPoligono() {
     //   val call = getPuntos().create(ApiServiceRefCatas::class.java).getPuntos()
       // val puntos = call.body()
       // if (call.isSuccessful){
        //    print(puntos)
        //}
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

//esto deberia estar en oncreate??
        localizacion.lastLocation.addOnSuccessListener {
                location ->
            if (location != null){
                runOnUiThread {
                    latitud = location.latitude
                    longitud = location.longitude
                    Log.i("localizacion", "lat = " + latitud + "long = " + longitud)
                    //getRefCatastral(latitud, longitud)
                    //refCatas = ObtenerRefCat(latitud, longitud).getRefCatastral(latitud,longitud).toString()
                    refCatas = ObtenerRefCat(latitud, longitud).ref
                    Log.i("refcatas",refCatas)
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
                    //refCatasActua = ObtenerRefCat(latActual , longActual).getRefCatastral(latActual,longActual).toString()
                    Log.i("refcataActual", refCatasActua)
                }
            }
        }, null)

    }
/*
    fun getRefCatastral(latitud: Double, longitud: Double){
        val url = "reverseGeocode?lon=$longitud&lat=$latitud&type=refcatastral"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call= getRetrofitRef().create(ApiServiceCoord::class.java)
                    .getRefCatastral(url)
                val response = call.body()
                if (call.isSuccessful){
                    runOnUiThread {
                        refCatas = response?.address.toString()
                        println(response?.address)
                        Log.i("RESPUESTA", response?.address.toString())
                        getPuntos()
                        //main()
                    }
                }else{
                    runOnUiThread {
                        Log.i("error", "ERROR EN LA RESPUESTA")
                    }
                }
            }catch (e:java.lang.Exception){
                runOnUiThread {
                    Log.i("error", e.message.toString())
                }
            }
        }
    }

    fun getRefCatastral2(latitud: Double, longitud: Double){
        val url = "reverseGeocode?lon=$longitud&lat=$latitud&type=refcatastral"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call= getRetrofitRef().create(ApiServiceCoord::class.java)
                    .getRefCatastral(url)
                val response = call.body()
                if (call.isSuccessful){
                    runOnUiThread {
                        refCatasActua = response?.address.toString()
                        Log.i("RESPUESTA2", response?.address.toString())
                        comprobar()
                    }
                }else{
                    runOnUiThread {
                        Log.i("error", "ERROR EN LA RESPUESTA")
                    }
                }
            }catch (e:java.lang.Exception){
                runOnUiThread {
                    Log.i("error", e.message.toString())
                }
            }
        }
    }

    private fun comprobar() {
        if (refCatas.equals(refCatasActua) ){
            Log.i("comprobar","estas en el mismo lao")
        }
    }

    fun getRetrofitRef(): Retrofit{
        val urlBase = "http://www.cartociudad.es/geocoder/api/geocoder/"
        return Retrofit.Builder().baseUrl(urlBase).addConverterFactory(GsonConverterFactory.create()).build()
    }
    fun getRetrofitPuntos(): Retrofit{
        val urlBase = "http://ovc.catastro.meh.es/INSPIRE/"
        //return Retrofit.Builder().baseUrl(urlBase).addConverterFactory(SimpleXmlConverterFactory.create()).build()
        return Retrofit.Builder().baseUrl(urlBase).client(OkHttpClient()).addConverterFactory(SimpleXmlConverterFactory.create()).build()
    }
    fun getPuntos(){
       // val url = "wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat=${refCatas}&amp;srsname=EPSG::25830"
        val url = "wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat=3870005VH4137S&amp;srsname=EPSG::25830"
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call= getRetrofitPuntos().create(ApiServiceRefCatas::class.java)
                    .getPuntos(url)
                val response = call.body()
                if (call.isSuccessful) {
                    runOnUiThread {
                       // obtenerPuntos(response)
                        Log.i("Puntos", response?.text.toString())
                       // Log.i("Puntos", response?.member!!.CadastralParcel!!.geometry!!.MultiSurface!!.surfaceMember!!.Surface!!.patches!!.PolygonPatch!!.exterior!!.LinearRing!!.posList!!.toString())
                    }
                }else{
                    runOnUiThread {
                        Log.i("error", "ERROR EN LA RESPUESTA")
                    }
                }
            }catch (e:java.lang.Exception){
                runOnUiThread {
                    Log.i("error2", e.message.toString())
                }
            }
        }
    }
    fun main() {
        val url = URL("https://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&version=2&request=GetFeature&STOREDQUERIE_ID=GetParcel&refcat=4770801VH4147S&srsname=EPSG::25830")
        val connection = url.openConnection() as HttpURLConnection
        connection.requestMethod = "GET"

        val responseCode = connection.responseCode
        if (responseCode == HttpURLConnection.HTTP_OK) {
            val inputStream = connection.inputStream
            val response = inputStream.bufferedReader().use { it.readText() }
            println(response)
            Log.i("otros",response)
        } else {
            println("Error: $responseCode")
            Log.i("errormain", responseCode.toString())
        }
    }
    /*
    fun main2() {
        val xmlFile = File("archivo.xml")
        val xmlMapper = XmlMapper()
        val jsonMapper = ObjectMapper()

        val xml = xmlFile.readText()
        val jsonObject = xmlMapper.readValue(xml, Any::class.java)
        val json = jsonMapper.writeValueAsString(jsonObject)

        println(json)
    }
    */
/*
    private fun obtenerPuntos(response: GmlposList?) {
        //response?.content
       //response?.gmlposList
    }
*/*/
    private fun crearPosicion() {
        val coordenada = LatLng(38.110134,-3.637166)
        val posicion : MarkerOptions = MarkerOptions().position(coordenada).title("mi posición")
        map.addMarker(posicion)
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