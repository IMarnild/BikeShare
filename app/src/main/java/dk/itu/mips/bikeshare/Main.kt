package dk.itu.mips.bikeshare

import android.app.Application
import android.content.Context
import android.graphics.*
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.Toast
import dk.itu.mips.bikeshare.model.Wallet
import io.realm.Realm
import io.realm.RealmConfiguration
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.*

class Main : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        this.initWallet()
    }

    private fun initWallet() {
        val realm = Realm.getInstance(Main.getRealmConfig())
        realm.executeTransaction { r ->
            val wallet = Wallet()
            wallet.id = 1
            wallet.money = 500
            r.insertOrUpdate(wallet)
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

        fun makeToast(context: Context, message: String) {
            Toast.makeText(context, message, Toast.LENGTH_LONG)
                .show()
        }

        fun makeToast(context: Context, message: String, length: Int) {
            Toast.makeText(context, message, length)
                .show()
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