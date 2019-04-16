package dk.itu.mips.bikeshare.viewmodel.fragments

import android.graphics.Bitmap
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.model.BikeRealm
import dk.itu.mips.bikeshare.viewmodel.util.BikeCamera
import android.content.DialogInterface
import android.support.v7.app.AlertDialog


private const val ARG_BIKEID = "bike"

class BikeEditFragment : Fragment() {

    private lateinit var bikeId: TextView
    private lateinit var bikeName: TextView
    private lateinit var bikeLocation: TextView
    private lateinit var bikeAvailable: CheckBox
    private lateinit var bikePrice: TextView
    private lateinit var bikePhoto: ImageView
    private var photo: Bitmap? = null
    private lateinit var saveButton: Button
    private lateinit var deleteButton: Button

    private val realm = BikeRealm()
    private lateinit var bike: Bike

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val id = it.getLong(ARG_BIKEID)
            this.bike = this.realm.read(id)!!
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        this.updateInfo(this.bike)
        this.updatePhotoView()
        this.setListeners()
    }

    private fun initVariables(view: View) {
        this.bikeId = view.findViewById(R.id.bike_id)
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikeAvailable = view.findViewById(R.id.bike_available)
        this.bikePrice = view.findViewById(R.id.bike_price)
        this.bikePhoto = view.findViewById(R.id.bike_photo)
        this.saveButton = view.findViewById(R.id.btn_save)
        this.deleteButton = view.findViewById(R.id.btn_delete)

    }

    private fun setListeners() {
        this.saveButton.setOnClickListener {
            println("Hello Save button")
            val bike = createBikeFromFields()
            if(bike != null) {
                this.realm.update(bike)
                Main.replaceFragment(BikeSelectionFragment(), fragmentManager!!)
                Toast.makeText(this.context, "Changes saved!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this.context, "Empty fields!", Toast.LENGTH_SHORT).show()
            }
        }

        this.deleteButton.setOnClickListener {
            this.showDeleteDialog()
        }
    }

    private fun showDeleteDialog() {
        AlertDialog.Builder(this.context!!)
            .setTitle("Delete entry")
            .setMessage("Are you sure you want to delete this entry?")
            .setPositiveButton(android.R.string.yes) { _, _ ->
                this.realm.delete(this.bike.id)
                Main.replaceFragment(BikeSelectionFragment(), fragmentManager!!)
                Toast.makeText(this.context, "Bike deleted!", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton(android.R.string.no, null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun createBikeFromFields(): Bike? {
        if(isAnyFieldBlank()) return null
        val bike = Bike()
        bike.id = this.bikeId.text.toString().toLong()
        bike.name = this.bikeName.text.toString()
        bike.location = this.bikeLocation.text.toString()
        bike.pricePerHour = this.bikePrice.text.toString().toDouble()
        bike.available = this.bikeAvailable.isChecked
        bike.photo = BikeCamera.bitmapToByteArray(this.photo!!)
        return bike
    }

    private fun isAnyFieldBlank(): Boolean {
        return bikeName.text.isBlank() || bikeLocation.text.isBlank() || bikePrice.text.isBlank() || photo == null
    }

    private fun updateInfo(bike: Bike) {
            this.bikeId.text = bike.id.toString()
            this.bikeName.text = bike.name
            this.bikeLocation.text = bike.location
            this.bikeAvailable.isChecked = bike.available
            val price = bike.pricePerHour.toString()
            this.bikePrice.text = price
            this.photo = BikeCamera.byteArrayToBitmap(bike.photo!!)
    }

    private fun updatePhotoView() {
        if (this.bike.photo != null) {
            val bitmap = BikeCamera.byteArrayToBitmap(this.bike.photo!!)
            this.bikePhoto.setImageBitmap(BikeCamera.getScaledBitmap(bitmap, this.activity!!))
        } else {
            this.bikePhoto.setImageDrawable(null)
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_bike_edit, container, false)
    }

    companion object {
        @JvmStatic
        fun newInstance(bikeId: Long) =
            BikeEditFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_BIKEID, bikeId)
                }
            }
    }
}
