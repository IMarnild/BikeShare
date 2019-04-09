package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.util.BikeCamera

class BikeInformationFragment : Fragment() {
    private val ARG_BIKE_ID = "bike"
    private val bikeRealm: BikeRealm = BikeRealm()
    private var bike: Bike? = null

    private lateinit var bikeId: TextView
    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikeAvailable: TextView
    private lateinit var bikePrice: TextView
    private lateinit var bikePhoto: ImageView
    private lateinit var startRideButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val id = it.getLong(ARG_BIKE_ID)
            this.bike = bikeRealm.read(id)
        }
    }

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_bike_information, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        this.updateInfo(this.bike)
        this.setButtonListeners()
        this.startRideButton.isEnabled = this.bike?.available ?: false
        this.updatePhotoView()
    }

    private fun initVariables(view: View) {
        this.bikeId = view.findViewById(R.id.bike_id)
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikeAvailable = view.findViewById(R.id.bike_available)
        this.bikePrice = view.findViewById(R.id.bike_price)
        this.bikePhoto = view.findViewById(R.id.bike_photo)
        this.startRideButton = view.findViewById(R.id.btn_start_ride)
    }

    private fun updateInfo(bike: Bike?) {
        if (bike != null) {
            this.bikeId.text = bike.id.toString()
            this.bikeName.text = bike.name
            this.bikeLocation.text = bike.location
            this.bikeAvailable.text = bike.available.toString()
            val price = bike.pricePerHour + " DKK."
            this.bikePrice.text = price
        }
    }

    fun updatePhotoView() {
        if (this.bike!!.photo != null) {
            val bitmap = BikeCamera.byteArrayToBitmap(this.bike!!.photo!!)
            this.bikePhoto.setImageBitmap(BikeCamera.getScaledBitmap(bitmap, this.activity!!))
        } else {
            this.bikePhoto.setImageDrawable(null)
        }
    }

    private fun setButtonListeners() {
        this.startRideButton.setOnClickListener {
            Main.replaceFragment(ActiveRideFragment.newInstance(this.bike!!.id, Main.getDate()), fragmentManager!!)
            Toast.makeText(this.context!!,"Ride Started!", Toast.LENGTH_SHORT).show()
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(bikeId: Long) =
            BikeInformationFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_BIKE_ID, bikeId)
                }
            }
    }
}
