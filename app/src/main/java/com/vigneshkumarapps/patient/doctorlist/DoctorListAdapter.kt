package com.vigneshkumarapps.patient.doctorlist

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vigneshkumarapps.patient.AppConstant
import com.vigneshkumarapps.patient.Models
import com.vigneshkumarapps.patient.R
import com.vigneshkumarapps.patient.databinding.ItemDoctorListBinding

class DoctorListAdapter(
    val context: Context,
    val data: ArrayList<Models.DoctorData>
) : RecyclerView.Adapter<DoctorListAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemDoctorListBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {

        return ViewHolder(
            binding =
            ItemDoctorListBinding.inflate(
                LayoutInflater.from(context),
                parent,
                false
            )
        )

    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {

        holder.binding.doctorName.text = data.get(position).name

        var specialty = context.getString(R.string.label_speciality)

        for (i in data.get(position).specialty){

            specialty += i.name + AppConstant.EMPTYSPACE

        }

        holder.binding.specialty.text = specialty

    }

    override fun getItemCount(): Int {

        return data.size

    }
}