package dk.itu.mips.bikeshare.viewmodel.dialogs

import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.TextView
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Wallet
import dk.itu.mips.bikeshare.viewmodel.fragments.ActiveRideFragment
import dk.itu.mips.bikeshare.viewmodel.fragments.MainFragment
import io.realm.Realm
import io.realm.kotlin.where

class PayDialog : DialogFragment() {

    private val ARG_PRICE = "price"
    private val realm = Realm.getInstance(Main.getRealmConfig())
    private lateinit var parent: ActiveRideFragment
    private lateinit var priceLabel: TextView
    private var price: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            this.price = it.getDouble(ARG_PRICE)
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_pay, null)

            builder.setView(layout)
            builder.setTitle("Receipt")

            this.initVariables(layout)
            this.setVariables()
            builder.setPositiveButton("Ok") { _, _ ->
                this.parent.endActiveRide()
                this.withdrawMoney(this.price)
                Main.replaceFragment(MainFragment(), fragmentManager!!)
            }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    fun initVariables(view: View) {
        this.priceLabel = view.findViewById(R.id.label_price)
        this.parent = targetFragment as ActiveRideFragment
    }

    fun setVariables() {
        this.priceLabel.text = String.format("%.2f", this.price)
    }

    private fun withdrawMoney(amount: Double): Double {
        var status = 0.0
        realm.executeTransaction {
            val wallet = realm.where<Wallet>().findFirst()
            wallet!!.money = wallet.money - amount
            status = wallet.money
        }
        return status
    }

    companion object {
        @JvmStatic
        fun newInstance(price: Double) =
            PayDialog().apply {
                arguments = Bundle().apply {
                    putDouble(ARG_PRICE, price)
                }
            }
    }
}