package com.example.ahorasiahorano

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map:GoogleMap
    var latitud: Double = 0.0
    var longitud: Double = 0.0
    lateinit var localizacion:  LocationManager
    private var refCatas: String = ""


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
        crearPosicion()
        pedirLocalizacion()
        datosLocalizacion()
        //dibujarPoligono()
    }

    private fun dibujarPoligono() {
        val call = getPuntos().create(ApiServiceRefCatas::class.java).getPuntos()
        val puntos = call.body()
        if (call.isSuccessful){
            print(puntos)
        }
    }

    private fun datosLocalizacion() {
           // latitud = map.myLocation.latitude
           // longitud = map.myLocation.longitude

        localizacion = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        var loc: Location = if (ActivityCompat.checkSelfPermission(
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
            localizacion.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        latitud = loc.latitude
        longitud = loc.longitude
    }

    fun getRefCatastral(): Retrofit{
        return Retrofit.Builder().baseUrl("http://www.cartociudad.es/geocoder/api/geocoder/reverseGeocode?lon="+ longitud +"&lat="+ latitud + "&type=refcatastral").addConverterFactory(GsonConverterFactory.create()).build()
        refCatas = getRefCatastral().create(ApiServiceCoord::class.java).getRefCatastral().toString()
      print(refCatas)
        getPuntos()
    }
    fun getPuntos(): Retrofit {
        //refCatas = getRefCatastral().toString()
        //refCatas = getRefCatastral().create(ApiServiceCoord::class.java).getRefCatastral().toString()
        return Retrofit.Builder().baseUrl("http://ovc.catastro.meh.es/INSPIRE/wfsCP.aspx?service=wfs&amp;version=2&amp;request=GetFeature&amp;STOREDQUERIE_ID=GetParcel&amp;refcat="+ refCatas +"&amp;srsname=EPSG::25830").addConverterFactory(SimpleXmlConverterFactory.create()).build()
    }
    private fun crearPosicion() {
        val coordenada = LatLng(38.110134,-3.637166)
        val posicion : MarkerOptions = MarkerOptions().position(coordenada).title("mi posición")
        map.addMarker(posicion)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada, 18f), 4000,null)
    }
    //comprobar permisos
    private fun isPermisos() = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
    //solicitar localización y pregunto si los permisos están aceptados
    private fun pedirLocalizacion(){
        if(!::map.isInitialized) return
        if(isPermisos()){
            map.isMyLocationEnabled = true
        }else{
            pedirPermiso()
        }
    }
    //solicitar permisos de localización
    private fun pedirPermiso() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Acepta los permisos en Ajustes", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), CODIGO_LOCAL)
        }
    }
    //se comprueba si el permiso está aceptado
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when(requestCode){
            CODIGO_LOCAL -> if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
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
            map.isMyLocationEnabled = false
            Toast.makeText(this, "Acepta los permisos en Ajustes", Toast.LENGTH_SHORT).show()
        }
    }


}