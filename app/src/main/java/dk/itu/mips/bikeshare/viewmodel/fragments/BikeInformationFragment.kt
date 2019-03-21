package dk.itu.mips.bikeshare.viewmodel.fragments

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.model.Bike
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.viewmodel.EditBikeDialog
import dk.itu.mips.bikeshare.viewmodel.NewBikeDialog
import io.realm.Realm
import io.realm.kotlin.where


class BikeInformationFragment : Fragment(), AdapterView.OnItemSelectedListener {

    private var bike: Bike? = null
    private lateinit var bikeName: TextView

    private lateinit var bikeLocation: TextView
    private lateinit var bikeSpinner: Spinner
    private lateinit var adapter: ArrayAdapter<Any>
    private lateinit var list: Array<Any>
    private lateinit var startRide: Button
    private lateinit var editBike: Button
    private lateinit var addBike: Button

    override fun onCreateView( inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle? ): View? {
        return inflater.inflate(R.layout.fragment_bike_infomation, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        this.initVariables(view)
        populateSpinner(view)
        this.setListeners()
        this.updateUI()
    }

    private fun initVariables(view: View) {
        this.bikeName = view.findViewById(R.id.bike_name)
        this.bikeLocation = view.findViewById(R.id.bike_location)
        this.bikeSpinner = view.findViewById(R.id.spinner_bike)
        this.startRide = view.findViewById(R.id.btn_start_ride)
        this.editBike = view.findViewById(R.id.btn_edit_bike)
        this.addBike = view.findViewById(R.id.btn_add_new_bike)
        this.bikeSpinner.onItemSelectedListener = this
    }

    fun populateSpinner(view: View) {
        val realm = Realm.getInstance(Main.getRealmConfig())
        this.list = realm.where<Bike>().sort("id").findAllAsync().toArray()

        this.adapter = ArrayAdapter(view.context, android.R.layout.simple_spinner_dropdown_item, list)
        this.adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        this.bikeSpinner.adapter = adapter
    }

    fun selectBike(index: Int) {
        this.bikeSpinner.setSelection(list.size-1)
    }

    private fun setListeners() {
        this.startRide.setOnClickListener {
            Main.replaceFragment(ActiveRideFragment.newInstance(this.bike!!, Main.getDate()), fragmentManager!!)
        }

        this.addBike.setOnClickListener {
            val dialog = NewBikeDialog()
            dialog.setTargetFragment(this,1)
            dialog.show(fragmentManager,"New Bike")
        }

        this.editBike.setOnClickListener {
            val dialog = EditBikeDialog()
            dialog.bike = this.bike
            dialog.show(fragmentManager, "Edit Bike")
        }
    }

    private fun updateUI() {
        this.bikeName.text = this.bike?.name
        this.bikeLocation.text = this.bike?.location
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        this.bike = list[0] as Bike
        this.updateUI()
    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        this.bike = list[position] as Bike
        this.updateUI()
    }
}
