package dk.itu.mips.bikeshare.viewmodel.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import dk.itu.mips.bikeshare.ARG_PHOTO
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.REQUEST_IMAGE_CAPTURE
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.util.GPS
import kotlinx.android.synthetic.main.activity_new_bike.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NewBikeActivity : AppCompatActivity() {

    private val bikeRealm: BikeRealm = BikeRealm()
    private var imagePath: String? = null
    private var photo: Bitmap? = null
    private lateinit var gps: GPS

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_bike)
        setButtonListeners()
        this.gps = GPS(this)
        if (savedInstanceState != null) {
            this.photo = savedInstanceState.getParcelable(ARG_PHOTO) as Bitmap?
        }
        if (this.photo != null) this.bike_photo.setImageBitmap(this.photo)
    }

    private fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply { imagePath = absolutePath }
    }

    private fun setButtonListeners() {
        this.btn_camera.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(this.packageManager)?.also {
                    this.createImageFile(this).also {
                        val uri: Uri = FileProvider.getUriForFile(this, "dk.itu.mips.bikeshare.fileprovider", it)
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
                        this.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                    }
                }
            }
        }

        this.btn_add_new_bike.setOnClickListener {
            if (!this.isAnyFieldBlank()) {
                val bike = this.createBike()
                this.bikeRealm.create(bike)
                onBackPressed()
                Toast.makeText(this, "Bike added!", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this, "Empty field!", Toast.LENGTH_SHORT).show()
            }
        }

        this.btn_gps.setOnClickListener {
            this.gps.requestLocationUpdates()
            this.bike_location.setText(this.gps.getAddress()!!.dropLast(9))
        }
    }

    private fun createBike(): Bike {
        val bike = Bike()
        bike.name = this.bike_name.text.toString()
        bike.location = this.bike_location.text.toString()
        bike.pricePerHour = this.bike_price.text.toString().toDouble()
        if (this.photo != null) { bike.photo = Main.bitmapToByteArray(this.photo!!) }
        return bike
    }

    private fun isAnyFieldBlank(): Boolean {
        return bike_name.text.isBlank() || bike_location.text.isBlank() || bike_price.text.isBlank() || photo == null
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(this.imagePath)
            this.photo = Bitmap.createScaledBitmap(bitmap, this.bike_photo.layoutParams.width, this.bike_photo.layoutParams.height, false)
            this.bike_photo.setImageBitmap(this.photo)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        outState!!.putParcelable(ARG_PHOTO, this.photo)
        super.onSaveInstanceState(outState)
    }
}
