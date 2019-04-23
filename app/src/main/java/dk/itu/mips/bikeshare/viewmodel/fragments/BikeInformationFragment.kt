package dk.itu.mips.bikeshare.viewmodel.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.mips.bikeshare.*
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.activities.ActiveRideActivity
import dk.itu.mips.bikeshare.viewmodel.activities.EditBikeActivity
import kotlinx.android.synthetic.main.fragment_bike_information.*

class BikeInformationFragment : Fragment() {

    private val bikeRealm: BikeRealm = BikeRealm()
    private var id: Long = -1L
    private lateinit var bike: Bike

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.id = it.getLong(ARG_BIKEID, -1L)
        }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_bike_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.updateInfo()
        this.setButtonListeners()
    }

    override fun onResume() {
        super.onResume()
        this.updateInfo()
    }

    private fun updateInfo() {
        if (this.id != -1L) {
            this.bike = bikeRealm.read(this.id)!!
            this.bike_id.text = this.bike.id.toString()
            this.bike_name.text = this.bike.name
            this.bike_location.text = this.bike.location
            this.bike_available.text = this.bike.available.toString()
            val price = this.bike.pricePerHour.toString() + " DKK."
            this.bike_price.text = price
            updatePhotoView()
            if (this.id != -1L && !this.bike.available) this.btn_start_ride.visibility = View.GONE else this.btn_start_ride.visibility = View.VISIBLE
        }
    }

    private fun updatePhotoView() {
       if (this.bike.photo != null) {
            val bitmap = Main.byteArrayToBitmap(this.bike.photo!!)
            this.bike_photo.setImageBitmap(Bitmap.createScaledBitmap(bitmap, this.bike_photo.layoutParams.width, this.bike_photo.layoutParams.height, false))
       } else {
            this.bike_photo.setImageDrawable(null)
       }
    }

    private fun setButtonListeners() {
        this.btn_start_ride.setOnClickListener {
            val intent = Intent(this.context, ActiveRideActivity::class.java)
            intent.putExtra(ARG_ACTIVE_BIKE_ID, this.bike.id)
            intent.putExtra(ARG_RIDE_START, Main.getDate())
            this.startActivity(intent)
        }

        this.btn_edit.setOnClickListener {
            val intent = Intent(this.context, EditBikeActivity::class.java)
            intent.putExtra(ARG_BIKEID, this.bike.id)
            this.startActivity(intent)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bikeId: Long) =
            BikeInformationFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_BIKEID, bikeId)
                }
            }
    }
}
