package dk.itu.mips.bikeshare.viewmodel.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.View
import android.widget.TextView
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import io.realm.Realm
import io.realm.kotlin.where

class EditBikeDialog : DialogFragment() {

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikePrice: TextView
    private lateinit var builder: AlertDialog.Builder
    private lateinit var parent: BikeSelectionFragment
    private var bike: Bike? = null

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_bike_new, null)
            this.builder = AlertDialog.Builder(it)
                .setView(layout)

            this.initVariables(layout)
            this.updateTextFields()
            this.setListeners()
            this.builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikePrice = view.findViewById(R.id.bike_price)
        this.parent = targetFragment as BikeSelectionFragment
    }

    private fun updateTextFields() {
        this.bikeName.text = this.bike?.name
        this.bikeLocation.text = this.bike?.location
        this.bikePrice.text = this.bike?.price.toString()
    }

    private fun setListeners() {
        this.builder.setPositiveButton("Save") { _, _ ->
            this.updateBike()
            this.parent.updateUI()
        }
        builder.setNegativeButton("Delete") { _, _ ->
            this.parent.deleteBike(bike?.id!!)
        }
    }

    private fun updateBike() {
        val realm = Realm.getInstance(Main.getRealmConfig())
        val realmBike = realm.where<Bike>().equalTo("id", this.bike?.id).findFirst()
        realm.executeTransaction {
            realmBike!!.name = this.bikeName.text.toString()
            realmBike.location = this.bikeLocation.text.toString()
            realmBike.price = this.bikePrice.text.toString()
        }

        Main.makeToast(this.parent.context!!, "Bike updated!")
    }

    companion object {
        @JvmStatic
        fun newInstance(bike: Bike): EditBikeDialog {
            val dialog = EditBikeDialog()
            dialog.bike = bike
            return dialog
        }
    }
}