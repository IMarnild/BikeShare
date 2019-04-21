package dk.itu.mips.bikeshare.viewmodel.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.*
import dk.itu.mips.bikeshare.*
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import org.jetbrains.anko.contentView
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class EditBikeActivity : AppCompatActivity() {

    private lateinit var bikeId: TextView
    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikeAvailable: CheckBox
    private lateinit var bikePrice: TextView
    private lateinit var bikePhoto: ImageView
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button
    private lateinit var photoButton: ImageButton

    private val realm = BikeRealm()
    private var photo: Bitmap? = null
    private var imagePath: String? = null
    private lateinit var bike: Bike

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_bike)
        this.initVariables(this.contentView!!)
        this.setListeners()
        val id = intent.getLongExtra(ARG_BIKEID, 0)
        this.bike = realm.read(id)!!

        this.updateInfo(this.bike)
        if (savedInstanceState != null) {
            this.photo = savedInstanceState.getParcelable(ARG_PHOTO) as Bitmap
        }
        this.updatePhotoView()
    }

    private fun initVariables(view: View) {
        this.bikeId = view.findViewById(R.id.bike_id)
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikeAvailable = view.findViewById(R.id.bike_available)
        this.bikePrice = view.findViewById(R.id.bike_price)
        this.bikePhoto = view.findViewById(R.id.bike_photo)
        this.saveButton = view.findViewById(R.id.btn_save)
        this.deleteButton = view.findViewById(R.id.btn_delete)
        this.photoButton = view.findViewById(R.id.btn_camera)
    }

    private fun setListeners() {
        this.saveButton.setOnClickListener {
            println("Hello Save button")
            val bike = createBikeFromFields()
            if(bike != null) {
                this.realm.update(bike)
                Toast.makeText(this, "Changes saved!", Toast.LENGTH_SHORT).show()
                onBackPressed()
            } else {
                Toast.makeText(this, "Empty fields!", Toast.LENGTH_SHORT).show()
            }
        }

        this.deleteButton.setOnClickListener {
            this.showDeleteDialog()
        }

        this.photoButton.setOnClickListener {
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
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
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
        bike.id = this.bikeId.text.toString().toLong()
        bike.name = this.bikeName.text.toString()
        bike.location = this.bikeLocation.text.toString()
        bike.pricePerHour = this.bikePrice.text.toString().toDouble()
        bike.available = this.bikeAvailable.isChecked
        bike.photo = Main.bitmapToByteArray(this.photo!!)
        return bike
    }

    private fun isAnyFieldBlank(): Boolean {
        return bikeName.text.isBlank() || bikeLocation.text.isBlank() || bikePrice.text.isBlank() || photo == null
    }

    private fun updateInfo(bike: Bike) {
        this.bikeId.text = bike.id.toString()
        this.bikeName.text = bike.name
        this.bikeLocation.text = bike.location
        this.bikeAvailable.isChecked = bike.available
        val price = bike.pricePerHour.toString()
        this.bikePrice.text = price
        this.photo = Main.byteArrayToBitmap(bike.photo!!)
    }

    private fun updatePhotoView() {
        when {
            this.photo != null -> this.bikePhoto.setImageBitmap(Main.getScaledBitmap(this.photo!!, this.bikePhoto))
            this.bike.photo != null -> {
                val bitmap = Main.byteArrayToBitmap(this.bike.photo!!)
                this.bikePhoto.setImageBitmap(Main.getScaledBitmap(bitmap, this.bikePhoto))
            }
            else -> this.bikePhoto.setImageDrawable(null)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap = BitmapFactory.decodeFile(this.imagePath)
            this.photo = Bitmap.createScaledBitmap(bitmap, this.bikePhoto.layoutParams.width, this.bikePhoto.layoutParams.height, false)
            this.updatePhotoView()
        }
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        super.onSaveInstanceState(outState)
        outState!!.putParcelable(ARG_PHOTO, this.photo)
    }
}
