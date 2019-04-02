package dk.itu.mips.bikeshare.viewmodel

import android.content.Intent
import android.content.pm.PackageManager
import android.provider.MediaStore
import android.support.v4.content.FileProvider
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import dk.itu.mips.bikeshare.PictureUtils
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.viewmodel.fragments.BikeSelectionFragment
import java.io.File

class BikeInformation(private val parent: BikeSelectionFragment) {

    private val view = parent.view!!
    private val bikeId: TextView = this.view.findViewById(R.id.bike_id)
    private val bikeName: TextView = this.view.findViewById(R.id.bike_name)
    private val bikeLocation: TextView = this.view.findViewById(R.id.bike_location)
    private val bikeAvailable: TextView = this.view.findViewById(R.id.bike_available)
    private val bikePrice: TextView = this.view.findViewById(R.id.bike_price)
    private val cameraButton: ImageButton = this.view.findViewById(R.id.btn_camera)
    private val photoView: ImageView = this.view.findViewById(R.id.bike_photo)

    lateinit var photoFile: File

    fun bind(bike: Bike) {
        this.bikeId.text = bike.id.toString()
        this.bikeName.text = bike.name
        this.bikeLocation.text = bike.location
        val price = bike.price.toString() + " DKK."
        this.bikePrice.text = price
        this.bikeAvailable.text = bike.available.toString()
        this.photoFile = getPhotoFile(bike)
        this.camera()
        updatePhotoView()
    }

    fun camera() {
        val context = parent.context!!
        val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        val canTakePhoto = captureImage.resolveActivity(context.packageManager) != null
        cameraButton.isEnabled = canTakePhoto

        cameraButton.setOnClickListener {

            val uri = FileProvider.getUriForFile(context,"dk.itu.mips.bikeshare.fileprovider", this.photoFile)
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            val cameraActivities = context.packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            cameraActivities.forEach { a -> context.grantUriPermission(a.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }

            parent.startActivityForResult(captureImage, 2)
        }
    }

    fun updatePhotoView() {
        if (!photoFile.exists()) {
            this.photoView.setImageDrawable(null)
        } else {
            val bitmap = PictureUtils.getScaledBitmap( this.photoFile.path, this.parent.activity!!)
            this.photoView.setImageBitmap(bitmap)
        }
    }

    fun getPhotoFile(bike: Bike): File {
        val filesDir = this.parent.context!!.filesDir
        return File(filesDir, PictureUtils.getPhotoFilename(bike))
    }
}
