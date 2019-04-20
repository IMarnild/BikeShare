package dk.itu.mips.bikeshare.viewmodel.util

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.widget.ImageButton
import android.widget.ImageView
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
        fun getScaledBitmap(bitmap: Bitmap, imageView: ImageView): Bitmap {
            return Bitmap.createScaledBitmap(bitmap, imageView.layoutParams.width, imageView.layoutParams.height, false)
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