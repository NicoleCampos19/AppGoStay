package emily.jacobo.gostay

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions

class hoteles_cerca : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    companion object {
        const val LOCATION_REQUEST_CODE = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_hoteles_cerca)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        createFragment()
    }

    private fun createFragment() {
        val mapFragment: SupportMapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        // Habilitar la ubicación del usuario solo si los permisos están otorgados
        if (isPermissionsGranted()) {
            enableMyLocation()
        } else {
            requestLocationPermission()
        }
    }

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

    @SuppressLint("MissingPermission")
    private fun enableMyLocation() {
        // Verifica nuevamente si el mapa está inicializado y los permisos están concedidos
        if (isPermissionsGranted()) {
            if (::map.isInitialized) {
                map.isMyLocationEnabled = true
                getCurrentLocation()
            } else {
                Toast.makeText(this, "El mapa aún no está listo", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Permisos no concedidos", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun getCurrentLocation() {
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            if (location != null) {
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15f))

                findNearbyHotels(currentLatLng)
            } else {
                Toast.makeText(this, "No se pudo obtener la ubicación", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun findNearbyHotels(currentLatLng: LatLng) {
        // Implementación de búsqueda de hoteles cercanos
        val nearbyHotel = LatLng(currentLatLng.latitude + 0.01, currentLatLng.longitude + 0.01)
        map.addMarker(MarkerOptions().position(nearbyHotel).title("Hotel cercano"))
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Asegúrate de que el mapa está inicializado
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

}
