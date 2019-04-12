package dk.itu.mips.bikeshare.viewmodel.util

import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import dk.itu.mips.bikeshare.Main
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Bike

class BikeArrayAdapter(private val myDataset: List<Any>, val listener: (Bike) -> Unit) :
    RecyclerView.Adapter<BikeArrayAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(inflater: LayoutInflater, val parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.adapter_holder_bike, parent, false)) {

        var bike: TextView = itemView.findViewById(R.id.bike_name)
        var location: TextView = itemView.findViewById(R.id.bike_location)
        var price: TextView = itemView.findViewById(R.id.bike_price)
        var available: TextView = itemView.findViewById(R.id.bike_available)
        var photo: ImageView = itemView.findViewById(R.id.bike_photo)

        fun setOnClickListener(bike: Bike, listener: (Bike) -> Unit) {
            itemView.setOnClickListener {
                listener(bike)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(parent: ViewGroup,
                                    viewType: Int): MyViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return MyViewHolder(inflater, parent)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // - get element from your data-set at this position
        // - replace the contents of the view with that element
        val bike = myDataset[position] as Bike

        holder.bike.text = bike.name
        holder.location.text = bike.location
        holder.price.text = bike.pricePerHour.toString() + " Dkk/hour"
        if (bike.available) {
            holder.available.text = "available"
            holder.available.setTextColor(Color.GREEN)
        } else {
            holder.available.text = "unavailable"
            holder.available.setTextColor(Color.RED)
        }
        holder.photo.setImageBitmap(BikeCamera.byteArrayToBitmap(bike.photo!!))
        holder.setOnClickListener(bike, this.listener)
    }

    // Return the size of your data-set (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}

