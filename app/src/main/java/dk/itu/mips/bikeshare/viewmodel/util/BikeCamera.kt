package dk.itu.mips.bikeshare.viewmodel.util

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.widget.ImageButton
import java.io.ByteArrayOutputStream

const val REQUEST_IMAGE_CAPTURE = 1

class BikeCamera(private val parent: Fragment) {

    private val context = parent.context!!

    fun attachCamera(button: ImageButton): ImageButton {
        button.setOnClickListener {
            Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
                takePictureIntent.resolveActivity(context.packageManager)?.also {
                    parent.startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
                }
            }
        }
        return button
    }

    companion object {
        fun getScaledBitmap(bitmap: Bitmap, activity: Activity): Bitmap {
            val size = Point()
            activity.windowManager.defaultDisplay
                .getSize(size)

            return Bitmap.createScaledBitmap(bitmap, size.x/2, size.y/3, false)
        }

        fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
            val stream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, stream)
            return stream.toByteArray()
        }

        fun byteArrayToBitmap(byteArray: ByteArray): Bitmap {
            return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
        }
    }
}