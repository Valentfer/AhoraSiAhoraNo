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
import android.util.Base64
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

    private fun createFragment() {
        val mapFragment: SupportMapFragment =
            supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0

        pedirLocalizacion()

        map.setOnPolygonClickListener {

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Parcela")
                .setMessage("Quieres guardar la parcela?")
                .setPositiveButton("Si") { _, _ ->
                    guardarDatos(latitud, longitud)
                    Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("No") { _, _ ->
                }
            val alertDialog = builder.create()
            alertDialog.show()
        }

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
                    val obtenerRefCat = ObtenerRefCat(longitud, latitud)
                    obtenerRefCat.getRefCatastral { ref, _, _, _, _ ->
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

        localizacion.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult) {
                for (location in p0.locations) {
                    latActual = location.latitude
                    longActual = location.longitude
                    Log.i("respuesta2", "$latActual,$longActual")
                    val obtenerRefCat = ObtenerRefCat(longActual, latActual)
                    obtenerRefCat.getRefCatastral { ref, _, _, _, _ ->
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
                polygonOptions.clickable(true)
                runOnUiThread {
                    map.addPolygon(polygonOptions)

                    if (!eligeRef) {
                        val primerPar = polygonOptions.points[0]
                        val latt = primerPar.latitude
                        val longg = primerPar.longitude
                        val coordenada = LatLng(latt, longg)
                        val marker = map.addMarker(MarkerOptions().position(coordenada))
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

    private fun utmToLatLon(
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
            datosLocalizacion()
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

    private fun guardarDatos(latitud: Double, longitud: Double) {
        val snapshotReadyCallback = GoogleMap.SnapshotReadyCallback { bitmap ->
            val stream = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, stream)
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

