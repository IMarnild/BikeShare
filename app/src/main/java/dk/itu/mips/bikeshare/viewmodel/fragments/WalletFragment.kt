package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Wallet
import io.realm.Realm
import io.realm.kotlin.where
import kotlinx.android.synthetic.main.fragment_wallet.*

class WalletFragment : Fragment() {

    private val realm = Realm.getInstance(Main.getRealmConfig())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.updateUI()
        this.btn_add_money.setOnClickListener {
            realm.executeTransaction {
                val wallet = realm.where<Wallet>().findFirst()
                wallet!!.money = wallet.money + 100
            }
            this.updateUI()
        }
    }

    private fun updateUI() {
        val wallet = realm.where<Wallet>().findFirst()
        val amount = String.format("%.2f", wallet!!.money) + " DKK."
        this.money.text = amount
    }
}
