package dk.itu.mips.bikeshare

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Environment
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.model.DummyRealm
import dk.itu.mips.bikeshare.model.Wallet
import io.realm.Realm
import io.realm.RealmConfiguration
import io.realm.kotlin.createObject
import io.realm.kotlin.where
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

const val ARG_ACTIVE_BIKE_ID = "bikeId"
const val ARG_RIDE_START = "rideStart"
const val ARG_BIKEID = "bike"
const val ARG_PHOTO = "photo"
const val REQUEST_IMAGE_CAPTURE = 3


class Main : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        this.initWallet()
        this.initDummies()
    }

    private fun initWallet() {
        val realm = Realm.getInstance(Main.getRealmConfig())
        val wallet = realm.where<Wallet>().findFirst()
        if (wallet == null) realm.createObject<Wallet>(1)
    }

    private fun initDummies() {
        val realm = BikeRealm()
        if (realm.read().isEmpty()) {
            val dummyRealm = DummyRealm(this.resources)
            dummyRealm.load()
        }
    }

    companion object {
        fun getRealmConfig(): RealmConfiguration =
            RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()

        fun replaceFragment(fragment: Fragment, fragmentManager: FragmentManager) {
            val transaction = fragmentManager.beginTransaction()
            transaction.replace(R.id.fragment_container, fragment)
            transaction.addToBackStack(null)
            transaction.commit()
        }

        fun getDate(): String {
            val date = Calendar.getInstance().time
            val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMAN)
            return simpleDateFormat.format(date)
        }

        fun hideKeyboard(context: Context, view: View) {
            val inputMethodManager: InputMethodManager = context.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
        }

        fun timeDifferenceInSeconds(start: String?, end: String?): Double {
            val time = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMAN).parse(start)
            val time2 = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.GERMAN).parse(end)
            return (time2.time - time.time).toDouble()/1000
        }

        fun isViewHorizontal(resources: Resources): Boolean {
            val orientation = resources.configuration.orientation
            return orientation == Configuration.ORIENTATION_LANDSCAPE
        }

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