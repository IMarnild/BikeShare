package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Wallet
import io.realm.Realm
import io.realm.kotlin.where

class WalletFragment : Fragment() {

    private lateinit var money: TextView
    private lateinit var addMoneyButton: Button
    private val realm = Realm.getInstance(Main.getRealmConfig())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_wallet, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        this.money = view.findViewById(R.id.money)
        this.addMoneyButton = view.findViewById(R.id.btn_add_money)
        this.updateUI()
        this.addMoneyButton.setOnClickListener {
            realm.executeTransaction {
                val wallet = realm.where<Wallet>().findFirst()
                wallet!!.money = wallet.money + 100
            }
            this.updateUI()
        }
    }

    private fun updateUI() {
        val wallet = realm.where<Wallet>().findFirst()
        val amount = wallet!!.money.toString() + " DKK."
        this.money.text = amount
    }
}
