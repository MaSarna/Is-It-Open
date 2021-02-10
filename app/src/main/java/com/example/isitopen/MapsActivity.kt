package com.example.isitopen

import android.Manifest
import android.content.IntentSender
import android.content.pm.PackageManager
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Transformations.map
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.firestore.FirebaseFirestore

class MapsActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var locationRequest: LocationRequest
    private var locationUpdateState = false
    val TAG = "MapsActivity"
    var db = FirebaseFirestore.getInstance()
    var arrayLocations = arrayListOf<Locations>()

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
        private const val REQUEST_CHECK_SETTINGS = 2
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_maps)
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
                .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)


//        val firebase = FirebaseDatabase.getInstance()
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

        db.collection("locations")
                .addSnapshotListener { result, e ->
                    if (e != null) {
                        Log.w(TAG, "Listen failed.", e)
                        return@addSnapshotListener
                    }
                    arrayLocations.clear()
                    arrayLocations.addAll(result!!.toObjects(Locations::class.java))

                    for (place in arrayLocations) {
                        val geoPosition = LatLng(place.latitude, place.longitude)
                        mMap.addMarker(MarkerOptions().position(geoPosition).title(place.name))

                    }
                }

//        val cracow = LatLng(50.049683, 19.944544)
//        val orakon = LatLng(50.050016100617974, 19.948198217562414)
//        val bracciate = LatLng(50.0463736471702, 19.927057470089814)
//        val koko = LatLng(50.060368468313975, 19.935269104510997)
//        val wordsSwords = LatLng(50.06629854291088, 19.935270875823303)
//        val korba = LatLng(50.06679406701404, 19.942679945416995)
//        val zdrowaKrowa = LatLng(50.070947541813275, 19.94422125739153)
//        val wagonRodzinnie = LatLng(50.08678721630554, 19.904372923464567)
//        val carlBart = LatLng(50.09701338086927, 19.965920750449143)
//        val wesoleGary = LatLng(50.08608198353258, 20.01476362302043)
//        val barZoltoNiebiesko = LatLng(50.07895333907443, 20.028701819405843)
//        val krakowskaSpizarnia = LatLng(49.98410516046104, 19.89231654328277)
//        val tesone = LatLng(50.0191878329213, 19.90682026644413)
//        val czarnaOwca = LatLng(50.05067082723072, 19.949687230750687)
//        val knitted = LatLng(50.049119887345164, 19.959561823825783)
//        val boka = LatLng(50.05026184306268, 19.959500574047553)
//        val talerz = LatLng(50.050343700697184, 19.959442914383942)

        mMap.uiSettings.isZoomControlsEnabled = true
//        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cracow, 12.0f))
//        mMap.addMarker(MarkerOptions().position(orakon).title("Orakon - Studio W19"))
//        mMap.addMarker(MarkerOptions().position(bracciate).title("A'Bracciate Pasta & Wine"))
//        mMap.addMarker(MarkerOptions().position(koko).title("Gospoda Koko"))
//        mMap.addMarker(MarkerOptions().position(wordsSwords).title("Hotel Words & Swords"))
//        mMap.addMarker(MarkerOptions().position(korba).title("KORBA Piwo Kawiarnia"))
//        mMap.addMarker(MarkerOptions().position(zdrowaKrowa).title("Zdrowa Krowa Kraków"))
//        mMap.addMarker(MarkerOptions().position(wagonRodzinnie).title("Małopolski Wagon Rodzinnie - mięso & pizza"))
//        mMap.addMarker(MarkerOptions().position(carlBart).title("Restauracja włoska Carl & Bart"))
//        mMap.addMarker(MarkerOptions().position(wesoleGary).title("Wesołe Gary"))
//        mMap.addMarker(MarkerOptions().position(barZoltoNiebiesko).title("Bary Na Żółto i Na Niebiesko"))
//        mMap.addMarker(MarkerOptions().position(krakowskaSpizarnia).title("NOWA Krakowska Spiżarnia"))
//        mMap.addMarker(MarkerOptions().position(tesone).title("Restauracja Tesone"))
//        mMap.addMarker(MarkerOptions().position(czarnaOwca).title("Czarna Owca Wino Bar"))
//        mMap.addMarker(MarkerOptions().position(knitted).title("KNITTED Coffee"))
//        mMap.addMarker(MarkerOptions().position(boka).title("Boka coffee bar"))
//        mMap.addMarker(MarkerOptions().position(talerz).title("Talerz Polish Restaurant"))

        setUpMap()

    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        //draws blue dot on the map (based on location of device)
        mMap.isMyLocationEnabled = true
        //gives the most recent location of device
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            //go to last location
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }

    }

    override fun onMarkerClick(p0: Marker?): Boolean = false

    //Check for location permission; if it is granted then request for location updates.
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */)
    }

    private fun createLocationRequest() {
        locationRequest = LocationRequest()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        val builder = LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest)

        val client = LocationServices.getSettingsClient(this)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener {
            locationUpdateState = true
            startLocationUpdates()
        }
        task.addOnFailureListener { e ->
            if (e is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    e.startResolutionForResult(this@MapsActivity,
                            REQUEST_CHECK_SETTINGS)
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
    }
}