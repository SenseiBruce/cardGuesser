package com.magic.haptic.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.magic.haptic.R
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticPattern
import com.magic.haptic.haptic.HapticPlayer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReferenceFragment : Fragment() {
    private var rvReference: RecyclerView? = null
    private lateinit var hapticPlayer: HapticPlayer

    private val cardViewModel: CardViewModel by viewModels {
        CardViewModel.Factory(AppDataStore(requireContext()))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        val view = inflater.inflate(R.layout.fragment_reference, container, false)
        rvReference = view.findViewById(R.id.rvReference)
        return view
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        hapticPlayer = HapticPlayer(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            cardViewModel.currentDeck.collectLatest { deck ->
                val config = cardViewModel.hapticConfig.value
                val mappedItems =
                    deck.mapIndexed { index, cardName ->
                        val pattern = cardViewModel.patternFor(cardName, config)
                        ReferenceItem(index + 1, cardName, pattern?.description ?: "--", pattern)
                    }
                rvReference?.adapter = ReferenceAdapter(mappedItems)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        rvReference = null
    }

    data class ReferenceItem(val position: Int, val cardName: String, val patternDesc: String, val patternObj: HapticPattern?)

    inner class ReferenceAdapter(private val items: List<ReferenceItem>) :
        RecyclerView.Adapter<ReferenceAdapter.ViewHolder>() {
        inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val tvPosition: TextView = view.findViewById(R.id.tvPosition)
            val tvCardName: TextView = view.findViewById(R.id.tvCardName)
            val tvPattern: TextView = view.findViewById(R.id.tvPattern)
        }

        override fun onCreateViewHolder(
            parent: ViewGroup,
            viewType: Int,
        ): ViewHolder {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_reference, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(
            holder: ViewHolder,
            position: Int,
        ) {
            val item = items[position]
            holder.tvPosition.text = String.format("#%02d", item.position)
            holder.tvCardName.text = item.cardName
            holder.tvPattern.text = item.patternDesc

            holder.itemView.setOnClickListener {
                item.patternObj?.let { pattern ->
                    hapticPlayer.vibrate(pattern)
                }
            }
        }

        override fun getItemCount() = items.size
    }
}
