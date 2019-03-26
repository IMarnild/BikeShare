package dk.itu.mips.bikeshare

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Point

class PictureUtils {
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
    }
}