package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.model.Ride
import dk.itu.mips.bikeshare.model.RideRealm
import dk.itu.mips.bikeshare.viewmodel.dialogs.PayDialog
import dk.itu.mips.bikeshare.viewmodel.util.GPS

class ActiveRideFragment : Fragment() {

    private val ARG_BIKEID = "bike"
    private val ARG_TIME = "time"
    private var bike: Bike? = null
    private var time: String? = null
    private val rideRealm: RideRealm = RideRealm()
    private val bikeRealm: BikeRealm = BikeRealm()

    private lateinit var gps: GPS
    private lateinit var gpsButton: Button
    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var rideTimeStart: TextView
    private lateinit var rideEndLocation: TextView
    lateinit var ride: Ride
    lateinit var endRide: Button


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

        Toast.makeText(this.context!!, "Ride started", Toast.LENGTH_SHORT).show()
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.rideTimeStart = view.findViewById(R.id.ride_time_start)
        this.endRide = view.findViewById(R.id.btn_end_ride)
        this.rideEndLocation = view.findViewById(R.id.ride_end_location)
        this.gpsButton = view.findViewById(R.id.btn_gps)
        this.gps = GPS(this.activity!!)
    }

    private fun setListeners() {
        this.endRide.setOnClickListener {
            if (this.rideEndLocation.text.isBlank()) {
                Toast.makeText(this.context!!, "invalid location!", Toast.LENGTH_SHORT).show()
            } else {
                this.rideEndLocation.clearFocus()
                Main.hideKeyboard(this.context!!, this.view!!)

                this.ride = this.rideRealm.newRide(
                    this.bike!!,
                    this.time!!,
                    this.rideEndLocation.text.toString()
                )

                val dialog = PayDialog.newInstance(this.calculatePrice(this.ride))
                dialog.setTargetFragment(this, 1)
                dialog.show(fragmentManager, "Payment")
            }
        }

        this.gpsButton.setOnClickListener {
            this.gps.updateLocation()
            this.rideEndLocation.text = this.gps.getAddress()!!.dropLast(9)
        }
    }

    fun endActiveRide() {
        this.rideRealm.create(this.ride)
        this.bikeRealm.updateLocation(this.bike!!.id, this.ride.endLocation!!)
        Toast.makeText(this.context!!, "Ride ended!", Toast.LENGTH_LONG).show()
    }

    private fun calculatePrice(ride: Ride): Double {
        val price = ride.bike!!.pricePerHour!!.toLong()
        val time = Main.timeDifferenceInSeconds(ride.startTime, ride.endTime)
        val hours = time/60/60
        return  hours * price
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
