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
import dk.itu.mips.bikeshare.model.Ride
import io.realm.Realm
import io.realm.kotlin.createObject
import io.realm.kotlin.where

class ActiveRideFragment : Fragment() {

    private val ARG_BIKE = "bike"
    private val ARG_TIME = "time"
    private var bike: Bike? = null
    private var time: String? = null

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var rideTimeStart: TextView
    private lateinit var rideEndLocation: TextView
    private lateinit var endRide: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            bike = it.getSerializable(ARG_BIKE) as Bike
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

            if (this.rideEndLocation.text.isBlank()) {
                this.rideEndLocation.setHintTextColor(resources.getColor(R.color.error_color_material_light))
            } else {
                this.endActiveRide()
                Main.replaceFragment(MainFragment(), fragmentManager!!)

            }
        }
    }

    private fun endActiveRide() {
        val realm = Realm.getInstance(Main.getRealmConfig())
        val index = realm.where<Ride>().findAll().toArray().size
        realm.executeTransaction { realm ->
            // Add a ride
            val ride = realm.createObject<Ride>(index)
            ride.bike = this.bike
            ride.location_start = this.bike!!.location
            ride.location_end = this.rideEndLocation.text.toString()
            ride.time_start = this.time
            ride.time_end = Main.getDate()

            // Update bike location
            ride.bike!!.location = this.rideEndLocation.text.toString()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bike: Bike, time: String) =
            ActiveRideFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_BIKE, bike)
                    putString(ARG_TIME, time)
                }
            }
    }
}
