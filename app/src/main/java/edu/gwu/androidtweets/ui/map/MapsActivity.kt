package edu.gwu.androidtweets.ui.map

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.ImageButton
import android.widget.Toast
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import edu.gwu.androidtweets.R
import edu.gwu.androidtweets.ui.tweet.TweetsActivity
import org.jetbrains.anko.doAsync

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    private lateinit var confirm: Button

    private lateinit var useLocation: ImageButton

    private lateinit var locationProvider: FusedLocationProviderClient

    private var currentAddress: Address? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps2)

        confirm = findViewById(R.id.confirm)
        useLocation = findViewById(R.id.current_location)

        locationProvider = LocationServices.getFusedLocationProviderClient(this)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        useLocation.setOnClickListener {
            // Request the GPS permission from the user
            // Then, if granted, determine their current location

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
                // Permission is already granted
                requestCurrentLocation()
            } else {
                // Permission has not been granted, so we can prompt the user

                // Make sure we're running on Marshmallow or higher
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    // Prompt the user
                    ActivityCompat.requestPermissions(
                        this,
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                        200
                    )
                }
            }
        }

        confirm.setOnClickListener {
            if (currentAddress != null) {
                val intent = Intent(this, TweetsActivity::class.java)
                intent.putExtra("location", currentAddress)
                startActivity(intent)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // Was this the GPS Permission request?
        if (requestCode == 200) {
            // We only request one permission, it's the 1st element
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted
                requestCurrentLocation()
            } else {
                // Permission was denied
                Toast.makeText(
                    this,
                    "Location permission was denied.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    @SuppressLint("MissingPermission")
    fun requestCurrentLocation() {
        locationProvider.requestLocationUpdates(
            LocationRequest.create(),
            locationCallback,
            null
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(result: LocationResult) {
            super.onLocationResult(result)

            // We only need a single result, so shut off the GPS
            // (could also have used locationProvider.lastLocation instead)
            locationProvider.removeLocationUpdates(this)

            val location: Location = result.locations[0]
            val latlng = LatLng(location.latitude, location.longitude)
            geocodeCoordinates(latlng)
        }
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
        mMap.setOnMapLongClickListener { coordinates ->
            // Code in here executes whenever the user long-presses
            googleMap.clear()
            geocodeCoordinates(coordinates)
        }
    }

    private fun geocodeCoordinates(coordinates: LatLng) {
        doAsync {
            // Code in here runs on the background
            val geocoder = Geocoder(this@MapsActivity)
            val results: List<Address> = geocoder.getFromLocation(
                coordinates.latitude,
                coordinates.longitude,
                5
            )

            val first: Address = results[0]
            currentAddress = first

            // UI can only be updated from the UI Thread
            // (so we need to switch back)
            runOnUiThread {
                // Update the UI
                mMap.clear()
                mMap.addMarker(
                    MarkerOptions().position(coordinates)
                )
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 5.0f))
                updateConfirmButton(first)
            }
        }
    }

    private fun updateConfirmButton(address: Address) {
        // Update the button color -- need to load the color from resources first
        val greenColor = ContextCompat.getColor(
            this, R.color.button_green
        )
        val checkIcon = ContextCompat.getDrawable(
            this, R.drawable.ic_check_white_24dp
        )
        confirm.setBackgroundColor(greenColor)

        // Update the left-aligned icon
        confirm.setCompoundDrawablesWithIntrinsicBounds(checkIcon, null, null, null)

        //Update button text
        confirm.text = address.getAddressLine(0)
        confirm.isEnabled = true
    }

}
