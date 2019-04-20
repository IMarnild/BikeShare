package dk.itu.mips.bikeshare.viewmodel.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.*
import dk.itu.mips.bikeshare.viewmodel.util.GPS
import io.realm.Realm
import io.realm.kotlin.where
import org.jetbrains.anko.contentView

const val ARG_ACTIVE_BIKE_ID = "bikeId"
const val ARG_RIDE_START = "rideStart"

class ActiveRideActivity : AppCompatActivity() {

    private var bike: Bike? = null
    private var time: String? = null
    private val rideRealm: RideRealm = RideRealm()
    private val bikeRealm: BikeRealm = BikeRealm()

    private lateinit var gps: GPS
    private lateinit var gpsButton: ImageButton
    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var rideTimeStart: TextView
    private lateinit var rideEndLocation: TextView
    lateinit var ride: Ride
    lateinit var endRide: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_ride)
        this.initVariables(this.contentView!!)
        this.setListeners()
        this.time = intent.getStringExtra(ARG_RIDE_START)
        val id = intent.getLongExtra(ARG_ACTIVE_BIKE_ID, 0)
        this.bike = bikeRealm.read(id)


        this.bikeRealm.toggleAvailability(this.bike!!)
        this.bikeName.text = this.bike?.name
        this.bikeLocation.text = this.bike?.location
        this.rideTimeStart.text = this.time

        Toast.makeText(this, "Ride started", Toast.LENGTH_SHORT).show()
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.rideTimeStart = view.findViewById(R.id.ride_time_start)
        this.endRide = view.findViewById(R.id.btn_end_ride)
        this.rideEndLocation = view.findViewById(R.id.ride_end_location)
        this.gpsButton = view.findViewById(R.id.btn_gps)
        this.gps = GPS(this)
    }

    private fun setListeners() {
        this.endRide.setOnClickListener {
            if (this.rideEndLocation.text.isBlank()) {
                Toast.makeText(this, "invalid location!", Toast.LENGTH_SHORT).show()
            } else {
                this.rideEndLocation.clearFocus()
                Main.hideKeyboard(this, this.contentView!!)

                this.ride = this.rideRealm.newRide(
                    this.bike!!,
                    this.time!!,
                    this.rideEndLocation.text.toString()
                )

                this.showPayDialog()
            }
        }

        this.gpsButton.setOnClickListener {
            this.gps.requestLocationUpdates()
            this.rideEndLocation.text = this.gps.getAddress()!!.dropLast(9)
        }
    }

    private fun showPayDialog() {
        val cost = this.calculatePrice(this.ride)
        AlertDialog.Builder(this)
            .setTitle("Receipt")
            .setMessage("Total cost: " + String.format("%.2f", cost) + " DKK.")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                this.ride.cost = cost
                this.withdrawMoney(cost)
                this.endActiveRide()
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show()
    }

    fun endActiveRide() {
        this.rideRealm.create(this.ride)
        this.bikeRealm.updateLocation(this.bike!!.id, this.ride.endLocation!!)
        this.bikeRealm.toggleAvailability(this.bike!!)
        Toast.makeText(this, "Ride ended!", Toast.LENGTH_LONG).show()
        this.startActivity(Intent(this, MainActivity::class.java))
    }

    private fun withdrawMoney(amount: Double): Double {
        val realm = Realm.getInstance(Main.getRealmConfig())
        var status = 0.0
        realm.executeTransaction {
            val wallet = realm.where<Wallet>().findFirst()
            wallet!!.money = wallet.money - amount
            status = wallet.money
        }
        return status
    }

    private fun calculatePrice(ride: Ride): Double {
        val price = ride.bike!!.pricePerHour.toLong()
        val time = Main.timeDifferenceInSeconds(ride.startTime, ride.endTime)
        val hours = time/60/60
        return  hours * price
    }


    override fun onBackPressed() {
        this.endRide.performClick()
    }
}
