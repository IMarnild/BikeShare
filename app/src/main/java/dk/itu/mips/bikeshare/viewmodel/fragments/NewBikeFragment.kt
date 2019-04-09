package dk.itu.mips.bikeshare.viewmodel.fragments

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.util.BikeCamera
import dk.itu.mips.bikeshare.viewmodel.util.REQUEST_IMAGE_CAPTURE

class NewBikeFragment : Fragment() {

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikePrice: TextView
    private lateinit var cameraButton: ImageButton
    private lateinit var addBikeButton: Button
    private lateinit var imageView: ImageView
    private lateinit var camera: BikeCamera
    private var photo: Bitmap? = null

    private val bikeRealm: BikeRealm = BikeRealm()

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_new_bike, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        this.setButtonListeners()
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikePrice = view.findViewById(R.id.bike_price)
        this.cameraButton = view.findViewById(R.id.btn_camera)
        this.imageView = view.findViewById(R.id.bike_photo)
        this.addBikeButton = view.findViewById(R.id.btn_add_new_bike)
        this.camera = BikeCamera(this)
    }

    private fun setButtonListeners() {
        this.cameraButton = camera.attachCamera(this.cameraButton)
        this.addBikeButton.setOnClickListener {
            if (!this.isAnyFieldBlank()) {
                val bike = this.createBike()
                this.bikeRealm.create(bike)
                Main.replaceFragment(BikeSelectionFragment(), this.fragmentManager!!)
                Toast.makeText(this.context!!, "Bike added!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this.context!!, "Empty field!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createBike(): Bike {
        val bike = Bike()
        bike.name = this.bikeName.text.toString()
        bike.location = this.bikeLocation.text.toString()
        bike.pricePerHour = this.bikePrice.text.toString()
        if (this.photo != null) { bike.photo = BikeCamera.bitmapToByteArray(this.photo!!) }
        return bike
    }

    private fun isAnyFieldBlank(): Boolean {
        return bikeName.text.isBlank() || bikeLocation.text.isBlank() || bikePrice.text.isBlank() || photo == null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            this.photo =  data.extras?.get("data") as Bitmap
            this.imageView.setImageBitmap(this.photo)
        }
    }
}

