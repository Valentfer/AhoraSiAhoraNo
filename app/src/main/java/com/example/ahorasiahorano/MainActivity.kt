package com.example.ahorasiahorano

import android.content.pm.PackageManager
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

class MainActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map:GoogleMap
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
    }

    private fun crearPosicion() {
        val coordenada = LatLng(38.110134,-3.637166)
        val posicion : MarkerOptions = MarkerOptions().position(coordenada).title("mi posición")
        map.addMarker(posicion)
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordenada, 18f), 4000,null)
    }

    private fun isPermisos() = ContextCompat.checkSelfPermission(this,android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

    private fun pedirLocalizacion(){
        if(!::map.isInitialized) return
        if(isPermisos()){
            map.isMyLocationEnabled = true
        }else{
            pedirPermiso()
        }
    }

    private fun pedirPermiso() {
        if(ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(this, "Acepta los permisos en Ajustes", Toast.LENGTH_SHORT).show()
        }else{
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), CODIGO_LOCAL)
        }
    }

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