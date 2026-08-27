package com.magic.haptic.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.magic.haptic.R
import com.magic.haptic.data.SpeechLogBuffer
import com.magic.haptic.data.SpeechLogEntry
import com.magic.haptic.databinding.ItemSpeechLogBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SpeechLogAdapter : RecyclerView.Adapter<SpeechLogAdapter.ViewHolder>() {
    private val buffer = SpeechLogBuffer()
    private val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())

    fun addEntry(entry: SpeechLogEntry) {
        buffer.add(entry)
        notifyItemInserted(0)
    }

    fun snapshot(): List<SpeechLogEntry> = entries.toList()
    fun clear() {
        val count = buffer.size
        buffer.clear()
        notifyItemRangeRemoved(0, count)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int,
    ): ViewHolder {
        val binding = ItemSpeechLogBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int,
    ) {
        val entry = buffer.get(position)
        holder.binding.tvTimestamp.text = sdf.format(Date(entry.timestamp))
        holder.binding.tvText.text = entry.text

        val color = if (entry.isMatch) android.R.color.holo_green_dark else android.R.color.darker_gray
        holder.binding.tvText.setTextColor(ContextCompat.getColor(holder.itemView.context, color))
    }

    override fun getItemCount(): Int = buffer.size

    class ViewHolder(val binding: ItemSpeechLogBinding) : RecyclerView.ViewHolder(binding.root)
}
