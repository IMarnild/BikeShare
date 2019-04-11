package dk.itu.mips.bikeshare.viewmodel.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.*
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import java.util.*

class GPS(val activity: Activity) : LocationListener {

    var locationManager: LocationManager? = this.activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    var lastLocation: Location? = null
    var currentLocation: Location? = null

    fun requestLocationUpdates(): Location? {
        if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            try {
                locationManager!!.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5, 5f, this)
                val location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                this.lastLocation = this.currentLocation
                this.currentLocation = location

            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        return this.currentLocation
    }

    fun getAddress(): String? {
        val geoCoder = Geocoder(this.activity, Locale.getDefault())
        val addresses: List<Address>

        addresses = geoCoder.getFromLocation(
            this.currentLocation!!.latitude,
            this.currentLocation!!.longitude,
            1
        )

        return addresses[0].getAddressLine(0)
    }

    override fun onLocationChanged(location: Location?) {
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
    }

    override fun onProviderEnabled(provider: String?) {
    }

    override fun onProviderDisabled(provider: String?) {
    }

}