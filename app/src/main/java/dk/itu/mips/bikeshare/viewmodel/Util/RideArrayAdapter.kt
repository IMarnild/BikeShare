package dk.itu.mips.bikeshare.viewmodel.Util

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import dk.itu.mips.bikeshare.R
import dk.itu.mips.bikeshare.model.Ride

class RideArrayAdapter(private val myDataset: List<Any>, val listener: (Ride) -> Unit) :
    RecyclerView.Adapter<RideArrayAdapter.MyViewHolder>() {

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder.
    // Each data item is just a string in this case that is shown in a TextView.
    class MyViewHolder(inflater: LayoutInflater, val parent: ViewGroup)
        : RecyclerView.ViewHolder(inflater.inflate(R.layout.adapter_holder_ride, parent, false)) {

        var bike: TextView = itemView.findViewById(R.id.bike_name)
        var date: TextView = itemView.findViewById(R.id.ride_date)

        fun setOnClickListener(ride: Ride, listener: (Ride) -> Unit) {
            itemView.setOnClickListener {
                listener(ride)
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
        val ride = myDataset[position] as Ride

        holder.bike.text = ride.bikeName
        holder.date.text = ride.time_start
        holder.setOnClickListener(ride, this.listener)
    }

    // Return the size of your data-set (invoked by the layout manager)
    override fun getItemCount() = myDataset.size
}

