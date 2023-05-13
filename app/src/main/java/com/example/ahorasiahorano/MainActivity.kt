package com.example.ahorasiahorano

import android.Manifest
import android.animation.ValueAnimator
import android.content.ContentValues
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
import com.google.android.gms.maps.model.Polygon
import com.google.android.gms.maps.model.PolygonOptions
import com.google.android.gms.maps.model.Polyline
import com.google.gson.Gson
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory
import org.osgeo.proj4j.ProjCoordinate
import org.osgeo.proj4j.proj.TransverseMercatorProjection
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.Locale

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
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

    companion object {
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
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0

        pedirLocalizacion()

/*
        map.setOnPolygonClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Parcela")
                .setMessage("Quieres guardar la imagen o los puntos?")
                .setPositiveButton("Imagen") { dialog, which ->
                    guardarImagen()
                    Toast.makeText(this, "Imagen guardada", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Coordenadas") { dialog, which ->
       //             guardarParcela(it)
                    Toast.makeText(this, "Guardadas las coordenadas de la parcela", Toast.LENGTH_SHORT).show()
                }
                .show()
        }*/
    }

/*    private fun guardarParcela(polygon: Polygon) {
        val pointsString = Gson().toJson(polygon.points)

        val values = ContentValues().apply {
            put("points", pointsString)
        }
        writableDatabase.insert("polygons", null, values)
    }*/
    /*fun leerParcela(): List<Polyline> {
        val polygons = mutableListOf<Polyline>()
        val cursor = readableDatabase.query("polygons", arrayOf("points"), null, null, null, null, null)
        cursor.use {
            while (it.moveToNext()) {
                // Convert the points string to a list of LatLng objects
                val pointsString = it.getString(0)
                val pointsType = object : TypeToken<List<LatLng>>() {}.type
                val points = Gson().fromJson<List<LatLng>>(pointsString, pointsType)

                // Create a new polyline with the points and add it to the list
                val polyline = map.addPolyline(PolylineOptions().clickable(true).addAll(points))
                polygons.add(polyline)
            }
        }
        return polygons
    }*/
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
                    val obtenerRefCat = ObtenerRefCat(longitud, latitud)
                    obtenerRefCat.getRefCatastral { ref ->
                        runOnUiThread {
                            val eligeRef = intent.extras!!.getBoolean("boolean")
                            if (eligeRef){
                                refCatas = ref
                            }else{
                                refCatas = intent.extras!!.getString("referencia").toString()
                            }
                                getPuntos(refCatas)
                        }
                    }
                    Log.i("refcatas", refCatas)
                }
            }
        }

        localizacion.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
             //   p0 ?: return
                for (location in p0.locations) {
                    latActual = location.latitude
                    longActual = location.longitude
                    Log.i("respuesta2", "" + latActual + "," + longActual)
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
        val mediaPlayer: MediaPlayer = MediaPlayer.create(this, R.raw.megaman_x_error)
        val view = window.decorView
        val parpadea = ValueAnimator.ofArgb(Color.WHITE, Color.RED)
        // parpadea.repeatCount = ValueAnimator.INFINITE
        // parpadea.repeatMode = ValueAnimator.REVERSE
        //parpadea.duration = 1000
        parpadea.addUpdateListener { animator ->
            val color = animator.animatedValue as Int
            view.setBackgroundColor(color)
        }
        if (refCatas == refCatasActua) {
            Log.i("comprobar", "estas en el mismo lao")
            pausa = false
            btnPausa.isVisible = false
            parpadea.end()
            view.setBackgroundColor(Color.WHITE)
        } else {
            btnPausa.isVisible = true
            btnReinicio.text = "Reiniciar Límites"
            Log.e("comprobar", "Has cambiado")

            if (!pausa) {
                mediaPlayer.start()
                parpadea.start()
            }
        }
    }

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
                runOnUiThread {
                    map.addPolygon(polygonOptions)
                }
            } catch (e: java.lang.Exception) {
                Log.i("polipuntos", e.message.toString())
            }
        }
    }

    fun utmToLatLon(
        easting: Double,
        northing: Double,
        zoneNumber: Int,
        zoneLetter: String
    ): ProjCoordinate {
        val tm = TransverseMercatorProjection()
        tm.setLonCDegrees(-183.0 + 6.0 * zoneNumber)
        tm.falseEasting = 500000.0
        tm.falseNorthing = if (zoneLetter.uppercase(Locale.ROOT) == "S") 10000000.0 else 0.0
        tm.scaleFactor = 0.9996
        tm.initialize()
        val utmCoord = ProjCoordinate(easting, northing)
        val latLonCoord = ProjCoordinate()
        tm.inverseProject(utmCoord, latLonCoord)
        return latLonCoord
    }

    private fun crearPosicion() {
        val coordenada = LatLng(latitud, longitud)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada, 18f), 4000, null)
    }

    //comprobar permisos
    private fun isPermisos() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    //solicitar localización y pregunto si los permisos están aceptados
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
        } else {
            pedirPermiso()
        }
    }

    //solicitar permisos de localización
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

    //se comprueba si el permiso está aceptado
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
fun guardarImagen(){
    val snapshotReadyCallback = GoogleMap.SnapshotReadyCallback { bitmap ->
        val stream = ByteArrayOutputStream()
        bitmap!!.compress(Bitmap.CompressFormat.PNG, 90, stream)
        val bytes = stream.toByteArray()

        val file = File(this.filesDir, "map_imagen.png")
        val outputStream = FileOutputStream(file)
        outputStream.write(bytes)
        outputStream.close()

        Toast.makeText(this, "Guardado en \${file.absolutePath}", Toast.LENGTH_SHORT).show()
    }

    map.snapshot(snapshotReadyCallback)
}

}