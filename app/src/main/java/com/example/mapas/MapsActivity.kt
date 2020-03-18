package com.example.mapas

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.PackageManager
import android.location.Location
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    //PERMISOS

    private val permissionFineLocation=android.Manifest.permission.ACCESS_FINE_LOCATION
    private val permissionCoarseLocation=android.Manifest.permission.ACCESS_COARSE_LOCATION

    //Para identificar el permiso asignado a la ubicacion, puede ir cualquier numero
    private val CODIGO_SOLICITUD_PERMISO=100

    //variable para obtener latitud y longitud
    var fusedLocationClient: FusedLocationProviderClient?=null
    var locationRequest: LocationRequest?=null
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        //inicializamos la variable
        fusedLocationClient= FusedLocationProviderClient(this)
        InicializarLocationRequest()

        callback = object : LocationCallback() {
            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                if (mMap != null) {
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true


                    for (ubicacion in p0?.locations!!) {
                        //envia mensaje con la latitud y longitud
                        Toast.makeText(
                            applicationContext,
                            ubicacion.latitude.toString() + " , " + ubicacion.longitude.toString(),
                            Toast.LENGTH_LONG
                        ).show()

                        val miposicion = LatLng(ubicacion.latitude, ubicacion.longitude)
                        //marcado para texto descriptivo
                        mMap.addMarker(MarkerOptions().position(miposicion).title("Aqui estoy prros!!"))
                        //camara
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(miposicion))
                    }
                }
            }
        }
    }

    private fun InicializarLocationRequest(){
        locationRequest= LocationRequest()
        //intervalo de refresh de la ubicacion en milisegundos
        locationRequest?.interval=10000
        //la velocidad mas alta se puede alcanzar para esta misma actualizacion
        //el intervalopuede variar pero el maximo es cada segundo
        locationRequest?.fastestInterval=5000
        //Que tanta proximidad se requiere para que use la localizacion
        //PRIORITY_HIGH_ACCURACY da la proximidad mas detallada con 10 mets
        locationRequest?.priority=LocationRequest.PRIORITY_HIGH_ACCURACY
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType=GoogleMap.MAP_TYPE_HYBRID

        // Add a marker in Sydney and move the camera
        /*
        val sydney = LatLng(-34.0, 151.0)
        mMap.addMarker(MarkerOptions().position(sydney).title("Marker in Sydney"))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney))*/
    }

    //validacion de permisos o slicitarlos para usar la localizacion

    private fun ValidarPermisosUbicacion():Boolean{
        //se usa para comparar el permiso de lo que se quiere obtener contra lo que se encuentra en el manifesto
        //el metodo checkSelfPermision permite comparar un permiso para saber si esta habilitado o denegado
        val hayubicacionprecisa=
            ActivityCompat.checkSelfPermission(this,permissionFineLocation)== PackageManager.PERMISSION_GRANTED
        val hayubicacionordinaria=
            ActivityCompat.checkSelfPermission(this,permissionCoarseLocation)== PackageManager.PERMISSION_GRANTED

        return hayubicacionprecisa && hayubicacionordinaria


    }
    @SuppressLint("MissingPermission")
    private fun obtenerubicacion(){
        //Mnitorea los cambios de ubicacion y los acgtualiza
        fusedLocationClient?.removeLocationUpdates(locationRequest, callback,null)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun pedirPermisos(){
        //Esta constante proveerContexto obtiene un balor boleano si ya le pregunto previamente
        //al usuario si da periso para utilizar su ubicacion
        //shouldAShowRequestPermissionRationale este metodo permite validar si el usuario cuando
        //nego permisos sele dio mas informacionde porque son neesarios lospermisos

        val proveerContexto=
            ActivityCompat.shouldShowRequestPermissionRationale(this,permissionCoarseLocation)
        if (proveerContexto){
            solicitudpermiso()
        }else{
            solicitudpermiso()
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun solicitudpermiso(){
        //Poner permisos de la actividad en forma de arreglo, junto con el codigo de reconocimiento
        //desencadena un trigger adicional mediante el metodo OnRequestPermissionResuñt
        requestPermissions(arrayOf(permissionFineLocation,permissionCoarseLocation),CODIGO_SOLICITUD_PERMISO)
    }

}
