package dk.itu.mips.bikeshare.viewmodel.Util

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v4.content.FileProvider
import android.widget.Button
import android.widget.ImageButton
import java.io.File

class Camera(private val parent: Fragment) {

    private val context = parent.context!!
    private val captureImage = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
    private var photo: File = this.getBikePhoto("default")

    private fun isAvailable(): Boolean {
        return captureImage.resolveActivity(context.packageManager) != null
    }

    fun setButtonListener(button: ImageButton) {
        button.setOnClickListener {

            val uri = FileProvider.getUriForFile(context,"dk.itu.mips.bikeshare.fileprovider", this.photo)
            captureImage.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            val cameraActivities = context.packageManager.queryIntentActivities(captureImage, PackageManager.MATCH_DEFAULT_ONLY)

            cameraActivities.forEach { a -> context.grantUriPermission(a.activityInfo.packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }

            parent.startActivityForResult(captureImage, 2)
        }
    }

    fun setPhotoName(name: String) {
        this.photo = this.getBikePhoto(name)
    }

    fun photoExists(): Boolean {
        return this.photo.exists()
    }

    fun photoPath(): String {
        return this.photo.path
    }

    private fun getBikePhoto(id: String): File {
        val filesDir = this.parent.context!!.filesDir
        return File(filesDir, getPhotoFilename(id))
    }

    companion object {
        fun getScaledBitmap(path: String,destWidth: Int, destHeight: Int): Bitmap {

            // Read in the dimensions of the image on disk
            var options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(path, options)
            val srcWidth: Float = options.outWidth.toFloat()
            val srcHeight: Float = options.outHeight.toFloat()

            // Figure out how much to scale down by
            var inSampleSize = 1
            var heightScale: Float? = null
            var widthScale: Float? = null

            if (srcHeight > destHeight || srcWidth > destWidth) {
                heightScale = srcHeight / destHeight
                widthScale = srcWidth / destWidth
            }

            if (heightScale != null && widthScale != null) {
                val num = if (heightScale > widthScale) heightScale else widthScale
                inSampleSize = Math.round(num)
            }

            options = BitmapFactory.Options()
            options.inSampleSize = inSampleSize

            // Read in and create final bitmap
            return BitmapFactory.decodeFile(path, options)
        }

        fun getScaledBitmap(path: String, activity: Activity): Bitmap {
            val size = Point()
            activity.windowManager.defaultDisplay
                .getSize(size)

            return getScaledBitmap(path, size.x, size.y)
        }

        fun getPhotoFilename(id: String): String {
            return "IMG_$id.jpg"
        }
    }
}