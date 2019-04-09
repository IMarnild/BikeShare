package dk.itu.mips.bikeshare.viewmodel.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

class GPS(val activity: Activity) : LocationListener {

    var locationManager: LocationManager? = this.activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager?
    var lastLocation: Location? = null
    var currentLocation: Location? = null

    fun updateLocation(): Location? {
        if (ContextCompat.checkSelfPermission(this.activity, Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this.activity,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            try {
                locationManager!!.requestSingleUpdate(LocationManager.GPS_PROVIDER, this, null)
                val location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                this.lastLocation = this.currentLocation
                this.currentLocation = location

            } catch (e: SecurityException) {
                e.printStackTrace()
            }
        }
        return this.currentLocation
    }

    fun getLocation(): Location {
        while(this.lastLocation == this.currentLocation) {
            updateLocation()
        }
        return this.currentLocation!!
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