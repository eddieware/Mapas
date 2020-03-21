package com.example.mapas

import android.annotation.SuppressLint
import android.content.pm.PackageManager
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
    //var fusedLocationClient: FusedLocationProviderClient?=null
    var fusedLocationClient : FusedLocationProviderClient? = null
    var locationRequest: LocationRequest?=null
    private var callback : LocationCallback? = null
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
        solLocationRequest()

        callback = object : LocationCallback() {

            override fun onLocationResult(p0: LocationResult?) {
                super.onLocationResult(p0)

                if (mMap != null) {
                    mMap.isMyLocationEnabled = true
                    mMap.uiSettings.isMyLocationButtonEnabled = true


                    for (ubicacion in p0?.locations!!) {
                        //envia mensaje con la latitud y longitud
                        Toast.makeText(applicationContext, ubicacion.latitude.toString() + " , " + ubicacion.longitude.toString(), Toast.LENGTH_LONG
                        ).show()

                        val miPosicion = LatLng(ubicacion.latitude, ubicacion.longitude)
                        //marcado para texto descriptivo
                        mMap.addMarker(MarkerOptions().position(miPosicion).title("Aqui estoy prros!!"))
                        //camara
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(miPosicion))
                    }
                }
            }
        }
    }

    private fun solLocationRequest(){
       /* locationRequest= LocationRequest()
        //intervalo de refresh de la ubicacion en milisegundos
        locationRequest?.interval=10000
        //la velocidad mas alta se puede alcanzar para esta misma actualizacion
        //el intervalopuede variar pero el maximo es cada segundo
        locationRequest?.fastestInterval=5000
        //Que tanta proximidad se requiere para que use la localizacion
        //PRIORITY_HIGH_ACCURACY da la proximidad mas detallada con 10 mets
        locationRequest?.priority=LocationRequest.PRIORITY_HIGH_ACCURACY*/


        locationRequest = LocationRequest()
        locationRequest?.interval = 10000
        locationRequest?.fastestInterval = 5000
        locationRequest?.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
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

    private fun validarPermisosUbicacion():Boolean{
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
        fusedLocationClient?.requestLocationUpdates(locationRequest,callback,null)
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
            solicitudPermiso()
        }else{
            solicitudPermiso()
        }
    }


    private fun solicitudPermiso(){
        //Poner permisos de la actividad en forma de arreglo, junto con el codigo de reconocimiento
        //desencadena un trigger adicional mediante el metodo OnRequestPermissionResuñt
        requestPermissions(arrayOf(permissionFineLocation,permissionCoarseLocation),CODIGO_SOLICITUD_PERMISO)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        //requestCode almcacena el codigo de la solicitud el cual s eocmpara con mis constamte solicitud permiso y se mapea

        when(requestCode){
            CODIGO_SOLICITUD_PERMISO->{
                //grantResults es una arreglo donde viene el resultado en la primera posicion
                //si tiene algo en la primera posicion y admeas es igual al permiso requerido
                //para la ubicacion entonves se obtiene ubicacion si no se envia  un mensaje de que no se dieron permisos
                if(grantResults.size>0 &&grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    //si se concedio el permiso conceder ubicacion
                    obtenerubicacion()
                }else {
                    Toast.makeText(this,"No diste pErmiso para acceder a la ubicacion", Toast.LENGTH_LONG).show()

                    }
                }
            }

        }
        private fun detenerActualizacionUbicacion(){

            fusedLocationClient?.removeLocationUpdates(callback)
        }



    @RequiresApi(Build.VERSION_CODES.M)
    override fun onStart() {
        super.onStart()

        //se validan permisos de ubicacion si se tienen se inicia el metodo obtner
        if(validarPermisosUbicacion()){
            obtenerubicacion()
        }else{
            solicitudPermiso()
        }

    }



    override fun onPause() {
        super.onPause()
        detenerActualizacionUbicacion()
    }

}







