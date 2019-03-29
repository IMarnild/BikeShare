package dk.itu.mips.bikeshare.viewmodel.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.app.DialogFragment
import android.support.v4.content.FileProvider
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.viewmodel.fragments.BikeSelectionFragment

class NewBikeDialog : DialogFragment() {

    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikePrice: TextView
    private lateinit var builder: AlertDialog.Builder

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            this.builder = AlertDialog.Builder(it)
            val inflater = requireActivity().layoutInflater
            val layout = inflater.inflate(R.layout.dialog_bike_new, null)

            this.builder.setView(layout)
            this.initVariables(layout)
            this.setListeners()
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikePrice = view.findViewById(R.id.bike_price)
    }

    private fun setListeners() {
        this.builder.setPositiveButton(R.string.add
        ) { _, _ -> this.addBike() }

        this.builder.setNegativeButton(R.string.cancel
        ) { _, _ -> dialog.cancel() }
    }

    private fun addBike() {
        if (!isAnyFieldBlank()) {
            val bike = this.createBike()
            val parent = targetFragment as BikeSelectionFragment
            parent.addBike(bike)
        }
    }

    private fun createBike(): Bike {
        val bike = Bike()
        bike.name = this.bikeName.text.toString()
        bike.location = this.bikeLocation.text.toString()
        bike.price = this.bikePrice.text.toString()
        return bike
    }

    private fun isAnyFieldBlank(): Boolean {
        return !bikeName.text.isNotBlank() && bikeLocation.text.isNotBlank() && bikePrice.text.isNotBlank()
    }
}