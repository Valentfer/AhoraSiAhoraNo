package com.example.ahorasiahorano.activity

import android.Manifest
import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.ahorasiahorano.baseDatos.BBDD
import com.example.ahorasiahorano.clases.ObtenerRefCat
import com.example.ahorasiahorano.R
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
import com.google.android.gms.maps.model.PolygonOptions
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import org.osgeo.proj4j.ProjCoordinate
import org.osgeo.proj4j.proj.TransverseMercatorProjection
import java.io.ByteArrayOutputStream
import java.util.Locale

/*
* En esta clase se realiza la funcionalidad principal de la plicación, se hace uso de la localización fina,
* por lo que hay que solicitar los permisos correspodientes, además de varias llamadas a las API correspondientes
* para obtener los datos con los que realizamos la comparación para distinguir si estamos en una parcela u otra, se crea el mapa donde dibujaremos los límites
* de las parcelas y la funcionalidad de pausar la alarma que nos avisa si hemos salido de los límites además de reicniciar estos límites
* */
class Mapa : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private var latitud: Double = 0.0
    private var longitud: Double = 0.0
    var latActual: Double = 0.0
    var longActual: Double = 0.0
    private lateinit var localizacion: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var refCatas: String = ""
    var refCatasActua: String = ""
    private var pausa: Boolean = false
    private lateinit var btnPausa: Button
    private lateinit var btnReinicio: Button
    private lateinit var puntos: String
    private var eligeRef: Boolean = false

    companion object {
        const val CODIGO_LOCAL = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.mapa)
        btnPausa = findViewById(R.id.btnPausa)
        btnReinicio = findViewById(R.id.btnReinicio)
        createFragment()
        eligeRef = intent.extras!!.getBoolean("boolean")
        btnPausa.setOnClickListener {
            pausa = true

        }
        btnReinicio.setOnClickListener {
            eligeRef = true
            datosLocalizacion()
        }
    }

    //Creamos el fragment con el mapa a través de los métodos que ofrece la librería de google maps
    private fun createFragment() {
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    /*
     * Cuando el mapa esta listo llamamos  a una función para solicitar la localización
     * y definimos que ocurre cuando hacemos click sobre el poligono creado con los límites de la parcela,
     * al hacer click sobre el poligono se crea un mensaje que da dos opciones para guardar o no la imagen con los datos
     **/
    override fun onMapReady(p0: GoogleMap) {
        map = p0
        pedirLocalizacion()

        map.setOnPolygonClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Parcela")
                .setMessage("Quieres guardar la parcela?")
                .setPositiveButton("Si") { _, _ ->
                    //se llama a la función guardar pasandole como parámetros la longitud y latitud con los que después obtendremos datos
                    guardarDatos(latitud, longitud)
                    Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { _, _ ->
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }

    }

    /*
    * Al llamar a la función datosLocalizacion se establece una primera localización donde comprobaremos nuestra posición que
    * será la que usaremos de referencia hasta que se reinicie, para saber si estamos dentro o fuera de la parcela, esta primera ubicación se compara
    * con sucesivas llamadas a obtener la ubicación en tiempo real para comparar cada cierto intervalo, que definimos con los métodos de la clase LocationRequest
    * */
    private fun datosLocalizacion() {
        localizacion = LocationServices.getFusedLocationProviderClient(this)
        //se define el intervalo de tiempo con el que se irá actualizando la ubicación
        locationRequest = LocationRequest.create().apply {
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }
// Necesitamos tener aceptados los permisos para usar estos métodos
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
        //última localización conocida
        localizacion.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                runOnUiThread {
                    latitud = location.latitude
                    longitud = location.longitude
                    crearPosicion()
                    Log.i("localizacion", "lat = " + latitud + "long = " + longitud)
                    //variable para obtener un objeto que realiza una petición get a la API donde por la ubicación obtenemos el número de referencia catastral
                    val obtenerRefCat = ObtenerRefCat(longitud, latitud)
                    obtenerRefCat.getRefCatastral { ref, _, _, _ ->
                        runOnUiThread {
                            refCatas = if (eligeRef) {
                                ref
                            } else {
                                intent.extras!!.getString("referencia").toString()
                            }
                            getPuntos(refCatas)
                        }
                    }
                    Log.i("refcatas", refCatas)
                }
            }
        }
        //Ubicación en tiempo real
        localizacion.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    latActual = location.latitude
                    longActual = location.longitude
                    Log.i("respuesta2", "$latActual,$longActual")
                    val obtenerRefCat = ObtenerRefCat(longActual, latActual)
                    obtenerRefCat.getRefCatastral { ref, _, _, _ ->
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

    /*
    * Llamamos a la función comprobar que nos indica si hemos salido o continuamos en la misma parcela,
    * esta función la llamaremos al obtener los datos de la ubicación en tiempo real, dentro de esta función y a modo de aviso se implementan
    * dos métodos para pintar el fondo de rojo y emitir un sonido
    **/
    private fun comprobar() {
        val mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.megaman_x_error)
        val view = window.decorView
        val parpadea = ValueAnimator.ofArgb(Color.WHITE, Color.RED)
        parpadea.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            view.setBackgroundColor(color)
        }
        if (refCatas == refCatasActua) {
            Log.i("comprobar", "estas en el mismo lao")
            pausa = false
            btnPausa.isVisible = false
            parpadea.end()
            mediaPlayer.stop()
            view.setBackgroundColor(Color.WHITE)
        } else {
            btnPausa.isVisible = true
            btnReinicio.text = "Reiniciar Límites"
            if (!eligeRef) {
                pausa = true
            }
            Log.e("comprobar", "Has cambiado")

            if (!pausa) {
                mediaPlayer.start()
                parpadea.start()
            } else {
                mediaPlayer.stop()
                parpadea.end()
            }
        }
    }

    /*
    * En esta función se realiza una llamada a la API del catastro con la referencia catastral y obtenemos las coordenadas
    * de los puntos del poligono que forma la parcela
    * que recuperamos para poder pintar este poligon en el mapa,
    *  esta API devuelve un XML el cual lo leemos y accedemos al elemento donde nos proporciona las coordenadas,
    * una obtenido estas coordenadas en forma de cadena de texto llamamos a nuestra siguiente función para dibujar el poligono,
    * cabe destacar que para no ocupar el hilo principal hay que realizar esta función en otro hilo secundario con corrutinas
    * */
    @OptIn(DelicateCoroutinesApi::class)
    fun getPuntos(ref: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val url =
                    URL("http://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat=$ref&amp;srsname=EPSG::25830")
                val connection = url.openConnection()
                connection.setRequestProperty("Accept", "application/gml+xml")
                val inputStream = connection.getInputStream()
                val document =
                    DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(inputStream)
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

    /*
    * Para dibujar el poligono debemos analizar la cadena de texto recibida y separar datos que no nos sirva y dividirla en una lista de coordenadas
    * que mas tarde pasaremos a los métodos de la clase PolygonOptions que será la encargada de dibujar este poligono en el mapa, además aqui se hace uso
    * de laa clase utmToLatLon de la librería ProjCoordinate para pasar de coordenadas UTM a coordenadas geométricas.
    * */
    @OptIn(DelicateCoroutinesApi::class)
    private fun dibujarPoligono(puntos: String) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val coordenadas = puntos.replace("[", "").replace("]", "").split(", ")
                Log.i("polipunto", coordenadas.toString())
                val pares = coordenadas.chunked(2) {
                    Pair(it[0], it[1])
                }

                val polygonOptions = PolygonOptions()
                runOnUiThread {
                    map.clear()
                }
                for (i in pares) {
                    val utm = utmToLatLon(i.first.toDouble(), i.second.toDouble(), 25830, "N")
                    polygonOptions.add(
                        LatLng(
                            (utm.y + 0.1612808859756),
                            (utm.x - 2.999877740587968)
                        )
                    )
                    Log.i(
                        "polipunto",
                        "long: " + (utm.x - 2.999877740587968) + " lat: " + (utm.y + 0.1612808859756)
                    )
                }
                polygonOptions.strokeWidth(5f)
                polygonOptions.strokeColor(Color.RED)
                polygonOptions.fillColor(Color.LTGRAY)
                polygonOptions.clickable(true)
                runOnUiThread {
                    map.addPolygon(polygonOptions)
                    //Con la siguiente condición diferenciamos si la  referencia viene pasa por la ubicación o la hemos proporcionado nosotros al inicio
                    if (!eligeRef) {
                        val primerPar = polygonOptions.points[0]
                        val latt = primerPar.latitude
                        val longg = primerPar.longitude
                        val coordenada = LatLng(latt, longg)
                        //Creamos un marcador en un punto del poligono y se realiza una animación de camara para acercar la camara al poligono
                        map.addMarker(MarkerOptions().position(coordenada))
                        map.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(coordenada, 18f),
                            4000,
                            null
                        )
                        latitud = latt
                        longitud = longg
                    }
                }
            } catch (e: java.lang.Exception) {
                Log.i("polipuntos", e.message.toString())
            }
        }
    }

    //Función que transforma las coordenandas UTM a geográficas
    private fun utmToLatLon(
        easting: Double,
        northing: Double,
        zoneNumber: Int,
        zoneLetter: String
    ): ProjCoordinate {
        val tm = TransverseMercatorProjection()
        tm.setLonCDegrees(-183.0 + 6.0 * (zoneNumber - 1))
        tm.falseEasting = 500000.0
        tm.falseNorthing = if (zoneLetter.uppercase(Locale.ROOT) == "S") 10000000.0 else 0.0
        tm.scaleFactor = 0.9996
        tm.initialize()
        val utmCoord = ProjCoordinate(easting, northing)
        val latLonCoord = ProjCoordinate()
        tm.inverseProject(utmCoord, latLonCoord)
        return latLonCoord
    }

    //Se crea una posición en la ubicación y se realiza una animación de zoom de la camara hacía la posición
    private fun crearPosicion() {
        val coordenada = LatLng(latitud, longitud)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada, 18f), 4000, null)
    }

    //Comprobar permisos
    private fun isPermisos() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    //Solicitar localización y pregunto si los permisos están aceptados
    private fun pedirLocalizacion() {
        if (!::map.isInitialized) return
        if (isPermisos()) {
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
            datosLocalizacion()
        } else {
            pedirPermiso()
        }
    }

    //Solicitar permisos de localización
    private fun pedirPermiso() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            Toast.makeText(this, "Acepta los permisos en Ajustes", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                CODIGO_LOCAL
            )
        }
    }

    //Se comprueba si el permiso está aceptado
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CODIGO_LOCAL -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
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
            } else {
                Toast.makeText(this, "Acepta los permisos en Ajustes", Toast.LENGTH_SHORT).show()
            }

            else -> {}
        }
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        if (!::map.isInitialized) return
        if (!isPermisos()) {
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

    //Guardo los datos en una base de datos y transformo la imagen a un string para poder trabajar mejor con ella
    private fun guardarDatos(latitud: Double, longitud: Double) {
        val snapshotReadyCallback = GoogleMap.SnapshotReadyCallback { bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, stream)
            val bytes = stream.toByteArray()
            val imagestring = Base64.encodeToString(bytes, Base64.DEFAULT)

            val admin = BBDD(this, "parcelas", null, 1)
            val baseDatos = admin.writableDatabase
            val registro = ContentValues()

            registro.put("usuario", intent.extras!!.getString("usuario"))
            registro.put("imagen", imagestring)
            registro.put("latitud", latitud)
            registro.put("longitud", longitud)

            baseDatos.insert("parcelas", null, registro)
            baseDatos.close()
        }

        map.snapshot(snapshotReadyCallback)
    }

}

