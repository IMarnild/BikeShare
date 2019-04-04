package dk.itu.mips.bikeshare.viewmodel.Util

import android.app.Activity
import android.content.Intent
import android.graphics.*
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.widget.ImageButton

const val REQUEST_IMAGE_CAPTURE = 1

class SpyCam(private val parent: Fragment) {

    private val context = parent.context!!

    fun setButtonListener(button: ImageButton): ImageButton {
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

        fun getResizedBitmap(bitmap: Bitmap, newWidth: Int, newHeight: Int): Bitmap {
            val resizedBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888)

            val scaleX = newWidth / bitmap.width.toFloat()
            val scaleY = newHeight / bitmap.height.toFloat()
            val pivotX = 0f
            val pivotY = 0f

            val scaleMatrix = Matrix()
            scaleMatrix.setScale(scaleX, scaleY, pivotX, pivotY)

            val canvas = Canvas(resizedBitmap)
            canvas.setMatrix(scaleMatrix)
            canvas.drawBitmap(bitmap, 0.0f, 0.0f, Paint(Paint.FILTER_BITMAP_FLAG))

            return resizedBitmap
        }
    }
}