package dk.itu.mips.bikeshare

import android.app.Activity
import android.app.Application
import android.content.Context
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.model.DummyRealm
import dk.itu.mips.bikeshare.model.Wallet
import io.realm.Realm
import io.realm.RealmConfiguration
import java.text.SimpleDateFormat
import java.util.*

class Main : Application() {

    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        this.initWallet()
        this.initDummies()
    }

    private fun initWallet() {
        val realm = Realm.getInstance(Main.getRealmConfig())
        realm.executeTransaction { r ->
            val wallet = Wallet()
            wallet.id = 1
            wallet.money = 100.0
            r.insertOrUpdate(wallet)
        }
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
    }
}