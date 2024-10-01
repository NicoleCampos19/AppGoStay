package emily.jacobo.gostay

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
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
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class hoteles_cerca : AppCompatActivity(), OnMapReadyCallback {

    // Variable para la instancia del mapa de Google Maps
    private lateinit var map: GoogleMap
    // Cliente para acceder a la ubicación del dispositivo
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    // Callback para recibir actualizaciones de ubicación
    private lateinit var locationCallback: LocationCallback

    // Código de solicitud para permisos de ubicación
    companion object {
        const val LOCATION_REQUEST_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hoteles_cerca)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createFragment()

        //Para poder recibir actualizaciones en tiempo real
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult.locations) {
                    val currentLatLng = LatLng(location.latitude, location.longitude)
                    map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                    //Para actualizar la lista de hoteles cercanos cada vez que la ubicación cambia
                    showNearbyHotels(currentLatLng)
                }
            }
        }
    }

    // Para que se cree el fragment del mapa
    private fun createFragment() {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    // Para que al tener los permisos el mapa se inicie
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        if (isPermissionsGranted()) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }

        // Listener para manejar el clic en los marcadores
        map.setOnMarkerClickListener { marker ->
            val gmmIntentUri = Uri.parse("geo:${marker.position.latitude},${marker.position.longitude}?q=${marker.position.latitude},${marker.position.longitude}(${marker.title})")
            val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
            mapIntent.setPackage("com.google.android.apps.maps")
            if (mapIntent.resolveActivity(packageManager) != null) {
                startActivity(mapIntent)
            } else {
                Toast.makeText(this, "No se pudo abrir Google Maps", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    // Solicita los permisos
    private fun isPermissionsGranted() = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    private fun requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(this, "Ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_REQUEST_CODE)
        }
    }

    // Si los permisos son rechazados
    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        if (isPermissionsGranted()) {
            if (::map.isInitialized) {
                map.isMyLocationEnabled = true
                startLocationUpdates()
            } else {
                Toast.makeText(this, "El mapa aún no está listo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permisos no concedidos", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            //Para recibir actualizaciones (10 segundos)
            interval = 10000
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)
    }

    // Método para mostrar los hoteles cercanos con el nombre de cada hotel
    private fun showNearbyHotels(currentLatLng: LatLng) {
        // Aquí defines las coordenadas y nombres de tus hoteles
        val decameron = Triple(13.53275, -89.811638, "Royal Decameron Salinitas")
        val sheraton = Triple(13.69160, -89.24176, "Sheraton Presidente")
        val oasis = Triple(13.72301, -89.200800, "Hotel Oasis")
        val intercontinental = Triple(13.70768, -89.21305, "Real Intercontinental")
        val resort = Triple(13.17292, -88.11821, "Las Flores Resort")

        val hotels = listOf(
            LatLng(decameron.first, decameron.second) to decameron.third,
            LatLng(sheraton.first, sheraton.second) to sheraton.third,
            LatLng(oasis.first, oasis.second) to oasis.third,
            LatLng(intercontinental.first, intercontinental.second) to intercontinental.third,
            LatLng(resort.first, resort.second) to resort.third
        )

        map.clear()

        // Agrega un marcador para cada hotel en el nombre que se le puso anteriormente
        for (hotel in hotels) {
            map.addMarker(
                MarkerOptions()
                    .position(hotel.first)
                    .title(hotel.second) // Título del hotel
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            )
        }

        // Ajusta la cámara para mostrar los hoteles
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))
    }
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if (::map.isInitialized) {
                        enableMyLocation()
                    } else {
                        Toast.makeText(this, "Mapa no está inicializado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Para activar la localización ve a ajustes y acepta los permisos", Toast.LENGTH_SHORT).show()
                }
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        // Detener las actualizaciones de ubicación cuando se salga de la activity
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
}
