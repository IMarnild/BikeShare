package dk.itu.mips.bikeshare.viewmodel.fragments

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import dk.itu.mips.bikeshare.*
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.activities.ActiveRideActivity
import dk.itu.mips.bikeshare.viewmodel.activities.EditBikeActivity

class BikeInformationFragment : Fragment() {
    private val bikeRealm: BikeRealm = BikeRealm()
    private var id: Long = -1L
    private lateinit var bike: Bike

    private lateinit var bikeId: TextView
    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikeAvailable: TextView
    private lateinit var bikePrice: TextView
    private lateinit var bikePhoto: ImageView
    private lateinit var startRideButton: Button
    private lateinit var editBikeButton: Button

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
        this.initVariables(view)
        this.updateInfo()
        this.setButtonListeners()
        if (this.id != -1L && !this.bike.available) this.startRideButton.visibility = View.GONE
    }

    override fun onResume() {
        super.onResume()
        this.updateInfo()
    }

    private fun initVariables(view: View) {
        this.bikeId = view.findViewById(R.id.bike_id)
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikeAvailable = view.findViewById(R.id.bike_available)
        this.bikePrice = view.findViewById(R.id.bike_price)
        this.bikePhoto = view.findViewById(R.id.bike_photo)
        this.startRideButton = view.findViewById(R.id.btn_start_ride)
        this.editBikeButton = view.findViewById(R.id.btn_edit)
    }

    private fun updateInfo() {
        if (this.id != -1L) {
            this.bike = bikeRealm.read(this.id)!!
            this.bikeId.text = this.bike.id.toString()
            this.bikeName.text = this.bike.name
            this.bikeLocation.text = this.bike.location
            this.bikeAvailable.text = this.bike.available.toString()
            val price = this.bike.pricePerHour.toString() + " DKK."
            this.bikePrice.text = price
            updatePhotoView()
        }
    }

    private fun updatePhotoView() {
       if (this.bike.photo != null) {
            val bitmap = Main.byteArrayToBitmap(this.bike.photo!!)
            this.bikePhoto.setImageBitmap(Bitmap.createScaledBitmap(bitmap, this.bikePhoto.layoutParams.width, this.bikePhoto.layoutParams.height, false))
       } else {
            this.bikePhoto.setImageDrawable(null)
       }
    }

    private fun setButtonListeners() {
        this.startRideButton.setOnClickListener {
            val intent = Intent(this.context, ActiveRideActivity::class.java)
            intent.putExtra(ARG_ACTIVE_BIKE_ID, this.bike.id)
            intent.putExtra(ARG_RIDE_START, Main.getDate())
            this.startActivity(intent)
        }

        this.editBikeButton.setOnClickListener {
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
