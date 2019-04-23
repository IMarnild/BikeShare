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
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.widget.Toast
import dk.itu.mips.bikeshare.*
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import kotlinx.android.synthetic.main.activity_edit_bike.*
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditBikeActivity : AppCompatActivity() {

    private val realm = BikeRealm()
    private var photo: Bitmap? = null
    private var imagePath: String? = null
    private lateinit var bike: Bike

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_bike)
        this.setListeners()
        val id = intent.getLongExtra(ARG_BIKEID, 0)
        this.bike = realm.read(id)!!

        this.updateInfo(this.bike)
        if (savedInstanceState != null) {
            this.photo = savedInstanceState.getParcelable(ARG_PHOTO) as Bitmap?
        }
        this.updatePhotoView()
    }

    private fun setListeners() {
        this.btn_save.setOnClickListener {
            val bike = createBikeFromFields()
            if(bike != null) {
                this.realm.update(bike)
                Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show()
                onBackPressed()
            } else {
                Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show()
            }
        }

        this.btn_delete.setOnClickListener {
            this.showDeleteDialog()
        }

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
    }

    private fun createImageFile(context: Context): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.GERMAN).format(Date())
        val storageDir: File = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply { imagePath = absolutePath }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setTitle("Delete entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                this.realm.delete(this.bike.id)
                Toast.makeText(this, "Bike deleted!", Toast.LENGTH_LONG).show()
                this.startActivity(Intent(this, MainActivity::class.java))
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun createBikeFromFields(): Bike? {
        if(isAnyFieldBlank()) return null
        val bike = Bike()
        bike.id = this.bike_id.text.toString().toLong()
        bike.name = this.bike_name.text.toString()
        bike.location = this.bike_location.text.toString()
        bike.pricePerHour = this.bike_price.text.toString().toDouble()
        bike.available = this.bike_available.isChecked
        bike.photo = if (this.photo != null) Main.bitmapToByteArray(this.photo!!) else this.bike.photo
        return bike
    }

    private fun isAnyFieldBlank(): Boolean {
        return bike_name.text.isBlank() || bike_location.text.isBlank() || bike_price.text.isBlank()
    }

    private fun updateInfo(bike: Bike) {
        this.bike_id.text = bike.id.toString()
        this.bike_name.setText(bike.name)
        this.bike_location.setText(bike.location)
        this.bike_available.isChecked = bike.available
        val price = bike.pricePerHour.toString()
        this.bike_price.setText(price)
    }

    private fun updatePhotoView() {
        when {
            this.photo != null -> this.bike_photo.setImageBitmap(this.photo)
            this.bike.photo != null -> {
                val bitmap = Main.byteArrayToBitmap(this.bike.photo!!)
                this.bike_photo.setImageBitmap(Bitmap.createScaledBitmap(bitmap, this.bike_photo.layoutParams.width, this.bike_photo.layoutParams.height, false))
            }
            else -> this.bike_photo.setImageDrawable(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            println("hello")
            val bitmap = BitmapFactory.decodeFile(this.imagePath)
            this.photo = Bitmap.createScaledBitmap(bitmap, this.bike_photo.layoutParams.width, this.bike_photo.layoutParams.height, false)
            this.bike_photo.setImageBitmap(this.photo)
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(ARG_PHOTO, this.photo)
    }
}
