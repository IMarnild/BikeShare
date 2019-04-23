package dk.itu.mips.bikeshare.viewmodel.activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import dk.itu.mips.bikeshare.ARG_ACTIVE_BIKE_ID
import dk.itu.mips.bikeshare.ARG_RIDE_START
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.*
import dk.itu.mips.bikeshare.viewmodel.util.GPS
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.activity_active_ride.*
import org.jetbrains.anko.contentView

class ActiveRideActivity : AppCompatActivity() {

    private val bikeRealm: BikeRealm = BikeRealm()
    private val rideRealm: RideRealm = RideRealm()
    private var time: String? = null
    private lateinit var bike: Bike
    private lateinit var gps: GPS
    private lateinit var ride: Ride

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_active_ride)
        this.setListeners()
        this.gps = GPS(this)
        this.time = intent.getStringExtra(ARG_RIDE_START)
        val id = intent.getLongExtra(ARG_ACTIVE_BIKE_ID, 0)
        this.bike = bikeRealm.read(id)!!
        this.bikeRealm.toggleAvailability(this.bike)

        this.bike_name.text = this.bike.name
        this.bike_location.text = this.bike.location
        this.ride_time_start.text = this.time

        Toast.makeText(this, "Ride started", Toast.LENGTH_SHORT).show()
    }

    private fun setListeners() {
        this.btn_end_ride.setOnClickListener {
            if (this.ride_end_location.text.isBlank()) {
                Toast.makeText(this, "invalid location!", Toast.LENGTH_SHORT).show()
            } else {
                this.ride_end_location.clearFocus()
                Main.hideKeyboard(this, this.contentView!!)

                this.ride = this.rideRealm.newRide(
                    this.bike,
                    this.time!!,
                    this.ride_end_location.text.toString()
                )

                this.showPayDialog()
            }
        }

        this.btn_gps.setOnClickListener {
            this.gps.requestLocationUpdates()
            this.ride_end_location.setText(this.gps.getAddress()!!.dropLast(9))
        }
    }

    private fun showPayDialog() {
        val cost = this.calculatePrice(this.ride)
        AlertDialog.Builder(this)
            .setTitle("Receipt")
            .setMessage("Total cost: " + String.format("%.2f", cost) + " DKK.")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                this.endActiveRide()
                Toast.makeText(this, "Ride ended!", Toast.LENGTH_LONG).show()
                this.startActivity(Intent(this, MainActivity::class.java))
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_info)
            .show()
    }

    private fun endActiveRide() {
        val cost = this.calculatePrice(this.ride)
        this.ride.cost = cost
        this.withdrawMoney(cost)
        this.rideRealm.create(this.ride)
        this.bikeRealm.updateLocation(this.bike.id, this.ride.endLocation!!)
        this.bikeRealm.toggleAvailability(this.bike)
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
        this.btn_end_ride.performClick()
    }
}
