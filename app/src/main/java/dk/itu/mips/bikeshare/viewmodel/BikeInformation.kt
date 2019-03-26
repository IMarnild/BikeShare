package dk.itu.mips.bikeshare.viewmodel

import android.view.View
import android.widget.TextView
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike

class BikeInformation(private val view: View) {

    private val bikeName: TextView = this.view.findViewById(R.id.bike_name)
    private val bikeLocation: TextView = this.view.findViewById(R.id.bike_location)
    private val bikeAvailable: TextView = this.view.findViewById(R.id.bike_available)

    fun bind(bike: Bike) {
        this.bikeName.text = bike.name
        this.bikeLocation.text = bike.location
        this.bikeAvailable.text = bike.available.toString()
    }
}
