package com.example.ahorasiahorano

import android.Manifest
import android.content.pm.PackageManager
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
    var continua: Boolean = false
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
        //dibujarPoligono()
    }

    private fun dibujarPoligono() {
      /*  val polygonOptions = PolygonOptions()
        polygonOptions.add(LatLng(lat1, lng1))
        polygonOptions.add(LatLng(lat2, lng2))
        polygonOptions.add(LatLng(lat3, lng3))
        polygonOptions.add(LatLng(lat4, lng4))
        polygonOptions.strokeWidth(5f)
        polygonOptions.strokeColor(Color.RED)
        polygonOptions.fillColor(Color.BLUE)
        val polygon = googleMap.addPolygon(polygonOptions)
*/
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
                            continua = true
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


    fun main() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url = URL("http://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat=4770801VH4147S&amp;srsname=EPSG::25830")
                val connection = url.openConnection()
                connection.setRequestProperty("Accept", "application/gml+xml")
                val inputStream = connection.getInputStream()
                val document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
                val posListElement = document.getElementsByTagName("gml:posList").item(0)
                val posList = posListElement.textContent.trim().split(" ")
                println(posList)
                puntos = posList.toString()
                Log.i("puntos", posList.toString())
            } catch (e: Exception) {
                println("Ha ocurrido un error: \${e.message}")
                Log.i("puntos", e.message.toString())
            }
        }
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
                        //getPuntos()
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
        }/*
        val url = "https://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&version=2&request=GetFeature&STOREDQUERIE_ID=GetParcel&refcat=4770801VH4147S&srsname=EPSG::25830"

// Crear una instancia de SAXBuilder para procesar el documento GML
        val builder = SAXBuilder()
        val document = builder.build(url)

// Obtener el valor del elemento poslist
        val poslist = document.rootElement.getChild("featureMember")
            .getChild("CP:CadastralParcel", Namespace.getNamespace("CP", "http://www.catastro.meh.es/"))
            .getChild("boundedBy", Namespace.getNamespace("gml", "http://www.opengis.net/gml/3.2"))
            .getChild("Envelope", Namespace.getNamespace("gml", "http://www.opengis.net/gml/3.2"))
            .getChild("lowerCorner", Namespace.getNamespace("gml", "http://www.opengis.net/gml/3.2"))
        val valorPoslist = poslist.text

        Log.i("puntos", valorPoslist)*/


    }
    /*
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
*/*/*/
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
            main()

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