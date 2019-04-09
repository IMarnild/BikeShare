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
import java.text.SimpleDateFormat
import java.util.*

class ActiveRideFragment : Fragment() {

    private val ARG_BIKEID = "bike"
    private val ARG_TIME = "time"
    private var bike: Bike? = null
    private var time: String? = null
    private val rideRealm: RideRealm = RideRealm()
    private val bikeRealm: BikeRealm = BikeRealm()

    private lateinit var gps: GPS
    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var rideTimeStart: TextView
    private lateinit var rideEndLocation: TextView
    private lateinit var endRide: Button
    private lateinit var gpsButton: Button
    lateinit var ride: Ride

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
        this.gpsButton = view.findViewById(R.id.btn_gps)
        this.gps = GPS(this.activity!!)
    }

    private fun setListeners() {
        this.endRide.setOnClickListener {
            if (this.endLocationIsBlank()) {
                this.noEndLocationWarning()
            } else {
                this.rideEndLocation.clearFocus()
                Main.hideKeyboard(this.context!!, this.view!!)
                this.ride = this.createRide(this.bike!!)
                val dialog = PayDialog.newInstance(this.calculatePrice(this.ride))
                dialog.setTargetFragment(this, 1)
                dialog.show(fragmentManager, "Payment")
            }
        }

        this.gpsButton.setOnClickListener {
            this.gps.updateLocation()
            println("location lat: " + this.gps.getLocation().latitude)
        }
    }

    fun endActiveRide() {
        this.rideRealm.create(this.ride)
        this.updateBikeLocation(this.bike!!, this.ride.endLocation!!)
        Main.makeToast(this.context!!, "Ride ended!")
    }

    private fun updateBikeLocation(bike: Bike, location: String): Bike {
        val temp = Bike()
        temp.id = bike.id
        temp.name = bike.name
        temp.location = location
        temp.available = bike.available
        temp.pricePerHour = bike.pricePerHour
        temp.photo = bike.photo
        this.bikeRealm.update(temp)
        return temp
    }

    private fun createRide(bike: Bike): Ride {
        val ride = Ride()
        ride.bike = bike
        ride.bikeName = bike.name
        ride.startLocation = bike.location
        ride.endLocation = this.rideEndLocation.text.toString()
        ride.startTime = this.time
        ride.endTime = Main.getDate()
        return ride
    }

    private fun timeDifferenceInSeconds(start: String?, end: String?): Double {
        val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMAN).parse(start)
        val time2 = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMAN).parse(end)
        return (time2.time - time.time).toDouble()/1000
    }

    private fun calculatePrice(ride: Ride): Double {
        val price = ride.bike!!.pricePerHour!!.toLong()
        val time = this.timeDifferenceInSeconds(ride.startTime, ride.endTime)
        val hours = time/60/60
        return  hours * price
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
