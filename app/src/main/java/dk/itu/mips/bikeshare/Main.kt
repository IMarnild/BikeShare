package dk.itu.mips.bikeshare

import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.widget.Toast
import io.realm.Realm
import io.realm.RealmConfiguration
import java.text.SimpleDateFormat
import java.util.*

class Main : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
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
    }
}