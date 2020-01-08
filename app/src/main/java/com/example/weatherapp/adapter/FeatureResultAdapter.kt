package com.example.weatherapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.esri.arcgisruntime.data.Feature
import com.example.weatherapp.R
import kotlinx.android.synthetic.main.item_weather_hazard_result.view.text_view_location
import kotlinx.android.synthetic.main.item_weather_hazard_result.view.text_view_name
import kotlinx.android.synthetic.main.item_wild_fire_result.view.*

class FeatureResultAdapter(val onFeatureResultItemClickListener: OnFeatureResultItemClickListener) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        const val TYPE_WEATHER_HAZARD = 1
        const val TYPE_WILDFIRE = 2
        const val TYPE_EARTHQUAKE = 3
        const val TYPE_UNKNOWN = 0
    }

    private var featureList = emptyList<Feature>()
    //private var onFeatureResultItemClickListener: OnFeatureResultItemClickListener

    init {
        //  this.onFeatureResultItemClickListener = onFeatureResultItemClickListener
    }

    // turn into kotlin. use by observable for notifydata
    fun setFeatureList(list: List<Feature>) {
        featureList = list
        // this.onFeatureResultItemClickListener = onFeatureResultItemClickListener
        notifyDataSetChanged()
    }

    fun getFeatureList(): List<Feature> = featureList


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_WEATHER_HAZARD -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_weather_hazard_result, parent, false)
                WeatherHazardViewHolder(itemView, this.onFeatureResultItemClickListener)
            }
            TYPE_WILDFIRE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_wild_fire_result, parent, false)
                WildFireViewHolder(itemView, this.onFeatureResultItemClickListener)
            }
            TYPE_EARTHQUAKE -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_earthquake_result, parent, false)
                EarthquakeViewHolder(itemView, this.onFeatureResultItemClickListener)
            }
            else -> {
                val itemView = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_unknown_result, parent, false)
                UnknownViewHolder(itemView)
            }
        }
    }

    override fun getItemCount(): Int = featureList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        var current = featureList[position]
        var currentAttribute = current.attributes
        var currentField = current.featureTable.fields

        when (holder.itemViewType) {
            TYPE_WEATHER_HAZARD -> {
                val name: String = currentAttribute[currentField[1].name].toString()
                val severity: String = currentAttribute[currentField[2].name].toString()
                val location: String = currentAttribute[currentField[12].name].toString()

                (holder as WeatherHazardViewHolder).textViewName.text = name
                holder.textViewName.setTextColor(
                    getSeverityColor(
                        severity,
                        holder.textViewName.context
                    )
                )
                holder.textViewLocation.text = location

            }

            TYPE_WILDFIRE -> {
                val name = currentAttribute[currentField[4].name].toString()
                val activeState = currentAttribute[currentField[11].name].toString()
                val area = currentAttribute[currentField[18].name].toString()
                val area_measurement = currentAttribute[currentField[3].name].toString()

                (holder as WildFireViewHolder).textViewName.text = "$name Fire"
                if (activeState.equals("Y")) {
                    holder.textViewActive.text = "Active"
                } else {
                    holder.textViewActive.text = "Inactive"
                }
                holder.textViewArea.text = "$area $area_measurement"
            }

            TYPE_EARTHQUAKE -> {
                val magnitude = currentAttribute[currentField[3].name].toString()
                val type = currentAttribute[currentField[31].name].toString()
                val location = currentAttribute[currentField[6].name].toString()

                (holder as EarthquakeViewHolder).textViewName.text =
                    "$magnitude ${type.capitalize()}"
                holder.textViewLocation.text = location

            }

            TYPE_UNKNOWN -> {

            }
        }
    }

    /**
     * Returns [R.color] value according to severity of weather hazard
     */
    fun getSeverityColor(severity: String, context: Context): Int {
        if (severity.toLowerCase().equals("minor") || severity.toLowerCase().equals("green")) {
            return ContextCompat.getColor(context, R.color.severityMinor)

        } else if (severity.toLowerCase().equals("moderate") || severity.toLowerCase().equals("yellow") || severity.toLowerCase().equals(
                "orange"
            )
        ) {
            return ContextCompat.getColor(context, R.color.severityModerate)

        } else if (severity.toLowerCase().equals("severe") || severity.toLowerCase().equals("red")) {
            return ContextCompat.getColor(context, R.color.severitySevere)

        } else {
            return ContextCompat.getColor(context, R.color.material_on_background_disabled)
        }
    }

    /**
     * Returns [viewType] based off of the table name.
     */
    override fun getItemViewType(position: Int): Int {
        /** Weather Hazards */
        // change to featuretable.name and check with that
        if (featureList[position].featureTable.tableName.equals(
                "events ordered by size and severity",
                true
            )
        ) {
            return TYPE_WEATHER_HAZARD
        }
        /** Wild Fires */
        else if (featureList[position].featureTable.tableName.equals("active fire report", true)) {
            return TYPE_WILDFIRE
        }
        /** Earthquakes */
        else if (featureList[position].featureTable.tableName.equals("shake intensity", true)) {
            return TYPE_EARTHQUAKE
        } else {
            return TYPE_UNKNOWN
        }
    }

    open class ViewHolder(
        itemView: View,
        onFeatureResultItemClickListener: OnFeatureResultItemClickListener
    ) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        var onFeatureResultItemClickListener: OnFeatureResultItemClickListener

        init {
            itemView.setOnClickListener(this)
            this.onFeatureResultItemClickListener = onFeatureResultItemClickListener
        }

        override fun onClick(v: View?) {
            onFeatureResultItemClickListener.onFeatureResultItemClick(adapterPosition)
        }
    }

    class WeatherHazardViewHolder(
        itemView: View,
        onFeatureResultItemClickListener: OnFeatureResultItemClickListener
    ) : ViewHolder(itemView, onFeatureResultItemClickListener) {
        var textViewName: TextView
        var textViewLocation: TextView

        init {
            textViewName = itemView.text_view_name
            textViewLocation = itemView.text_view_location
        }
    }

    class WildFireViewHolder(
        itemView: View,
        onFeatureResultItemClickListener: OnFeatureResultItemClickListener
    ) : ViewHolder(itemView, onFeatureResultItemClickListener) {
        var textViewName: TextView
        var textViewActive: TextView
        var textViewArea: TextView

        init {
            textViewName = itemView.text_view_name
            textViewActive = itemView.text_view_location
            textViewArea = itemView.text_view_area
        }
    }

    class EarthquakeViewHolder(
        itemView: View,
        onFeatureResultItemClickListener: OnFeatureResultItemClickListener
    ) : ViewHolder(itemView, onFeatureResultItemClickListener) {
        var textViewName: TextView
        var textViewLocation: TextView

        init {
            textViewName = itemView.text_view_name
            textViewLocation = itemView.text_view_location
        }
    }

    class UnknownViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    }

    interface OnFeatureResultItemClickListener {
        fun onFeatureResultItemClick(position: Int)
    }

}