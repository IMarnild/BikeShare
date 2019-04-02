package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.model.Ride
import dk.itu.mips.bikeshare.model.RideRealm

class ActiveRideFragment : Fragment() {

    private val ARG_BIKEID = "bike"
    private val ARG_TIME = "time"
    private var bike: Bike? = null
    private var time: String? = null
    private val rideRealm: RideRealm = RideRealm()
    private val bikeRealm: BikeRealm = BikeRealm()

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var rideTimeStart: TextView
    private lateinit var rideEndLocation: TextView
    private lateinit var endRide: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val id= it.getLong(ARG_BIKEID)
            this.bike = bikeRealm.read(id)
            time = it.getString(ARG_TIME)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_active_ride, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        this.setListeners()

        this.bikeName.text = this.bike?.name
        this.bikeLocation.text = this.bike?.location
        this.rideTimeStart.text = this.time

        Main.makeToast(this.context!!, "Ride started")
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.rideTimeStart = view.findViewById(R.id.ride_time_start)
        this.endRide = view.findViewById(R.id.btn_end_ride)
        this.rideEndLocation = view.findViewById(R.id.ride_end_location)
    }

    private fun setListeners() {
        this.endRide.setOnClickListener {
            if (this.endLocationIsBlank()) {
                this.noEndLocationWarning()
            } else {
                this.endActiveRide()
                Main.replaceFragment(MainFragment(), fragmentManager!!)
            }
        }
    }

    private fun endActiveRide() {
        val ride = this.createRide(this.bike!!)
        this.rideRealm.create(ride)
        this.updateBikeLocation(this.bike!!, ride.location_end!!)
        Main.makeToast(this.context!!, "Ride ended!")
    }

    private fun updateBikeLocation(bike: Bike, location: String): Bike {
        val temp = Bike()
        temp.id = bike.id
        temp.name = bike.name
        temp.location = location
        temp.available = bike.available
        temp.price = bike.price
        this.bikeRealm.update(temp)
        return temp
    }

    private fun createRide(bike: Bike): Ride {
        val ride = Ride()
        ride.bike = bike
        ride.bikeName = bike.name
        ride.location_start = bike.location
        ride.location_end = this.rideEndLocation.text.toString()
        ride.time_start = this.time
        ride.time_end = Main.getDate()
        return ride
    }

    fun noEndLocationWarning() {
        this.rideEndLocation.setHintTextColor(resources.getColor(R.color.colorError))
    }

    fun endLocationIsBlank(): Boolean {
        return this.rideEndLocation.text.isBlank()
    }

    companion object {
        @JvmStatic
        fun newInstance(bikeId: Long, time: String) =
            ActiveRideFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_BIKEID, bikeId)
                    putString(ARG_TIME, time)
                }
            }
    }
}
