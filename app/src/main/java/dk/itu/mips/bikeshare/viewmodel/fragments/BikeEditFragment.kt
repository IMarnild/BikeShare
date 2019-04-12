package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.util.BikeCamera

private const val ARG_BIKEID = "bike"

class BikeEditFragment : Fragment() {

    private lateinit var bikeId: TextView
    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikeAvailable: TextView
    private lateinit var bikePrice: TextView
    private lateinit var bikePhoto: ImageView

    private val realm = BikeRealm()
    private lateinit var bike: Bike

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val id = it.getLong(ARG_BIKEID)
            this.bike = this.realm.read(id)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        this.updateInfo(this.bike)
        this.updatePhotoView()
    }

    private fun initVariables(view: View) {
        this.bikeId = view.findViewById(R.id.bike_id)
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikeAvailable = view.findViewById(R.id.bike_available)
        this.bikePrice = view.findViewById(R.id.bike_price)
        this.bikePhoto = view.findViewById(R.id.bike_photo)
    }

    private fun updateInfo(bike: Bike) {
            this.bikeId.text = bike.id.toString()
            this.bikeName.text = bike.name
            this.bikeLocation.text = bike.location
            this.bikeAvailable.text = bike.available.toString()
            val price = bike.pricePerHour.toString()
            this.bikePrice.text = price
    }

    fun updatePhotoView() {
        if (this.bike.photo != null) {
            val bitmap = BikeCamera.byteArrayToBitmap(this.bike.photo!!)
            this.bikePhoto.setImageBitmap(BikeCamera.getScaledBitmap(bitmap, this.activity!!))
        } else {
            this.bikePhoto.setImageDrawable(null)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bike_edit, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(bikeId: Long) =
            BikeEditFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_BIKEID, bikeId)
                }
            }
    }
}
