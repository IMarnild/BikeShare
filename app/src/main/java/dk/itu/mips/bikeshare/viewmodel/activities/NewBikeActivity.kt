package dk.itu.mips.bikeshare.viewmodel.activities

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.*
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.REQUEST_IMAGE_CAPTURE
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import org.jetbrains.anko.contentView

class NewBikeActivity : AppCompatActivity() {

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikePrice: TextView
    private lateinit var cameraButton: ImageButton
    private lateinit var addBikeButton: Button
    private lateinit var imageView: ImageView
    private var photo: Bitmap? = null
    private val ARG_PHOTO = "photo"

    private val bikeRealm: BikeRealm = BikeRealm()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_bike)
        initVariables(this.contentView!!)
        setButtonListeners()
        if (savedInstanceState != null) {
            this.photo = savedInstanceState.getParcelable(ARG_PHOTO) as Bitmap
        }
        if (this.photo != null) this.imageView.setImageBitmap(this.photo)
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikePrice = view.findViewById(R.id.bike_price)
        this.cameraButton = view.findViewById(R.id.btn_camera)
        this.imageView = view.findViewById(R.id.bike_photo)
        this.addBikeButton = view.findViewById(R.id.btn_add_new_bike)
    }

    private fun setButtonListeners() {
        this.cameraButton.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(this.packageManager)?.also {
                    this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }

        this.addBikeButton.setOnClickListener {
            if (!this.isAnyFieldBlank()) {
                val bike = this.createBike()
                this.bikeRealm.create(bike)
                onBackPressed()
                Toast.makeText(this, "Bike added!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Empty field!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createBike(): Bike {
        val bike = Bike()
        bike.name = this.bikeName.text.toString()
        bike.location = this.bikeLocation.text.toString()
        bike.pricePerHour = this.bikePrice.text.toString().toDouble()
        if (this.photo != null) { bike.photo = Main.bitmapToByteArray(this.photo!!) }
        return bike
    }

    private fun isAnyFieldBlank(): Boolean {
        return bikeName.text.isBlank() || bikeLocation.text.isBlank() || bikePrice.text.isBlank() || photo == null
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap =  data!!.extras?.get("data") as Bitmap
            this.photo = Bitmap.createScaledBitmap(bitmap, this.imageView.width, this.imageView.height, false)
            this.imageView.setImageBitmap(this.photo)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(ARG_PHOTO, this.photo)
    }
}
