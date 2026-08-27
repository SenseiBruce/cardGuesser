package com.magic.haptic.ui

import android.content.Intent
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.magic.haptic.R
import com.magic.haptic.card.ReferenceDeckFormatter
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticPattern
import com.magic.haptic.haptic.HapticPlayer
import com.magic.haptic.util.ReferenceDeckFormatter
import com.magic.haptic.util.ReferenceDeckRow
import com.magic.haptic.util.ReferenceRowCopy
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import com.magic.haptic.data.AppDataStore
import com.magic.haptic.data.HapticPattern
import com.magic.haptic.haptic.HapticPlayer
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class ReferenceFragment : Fragment() {
    private var rvReference: RecyclerView? = null
    private lateinit var hapticPlayer: HapticPlayer
    private var latestDeckName: String = "DEFAULT"
    private var latestCards: List<String> = emptyList()
    private var latestRows: List<ReferenceDeckRow> = emptyList()

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
        view.findViewById<Button>(R.id.btnShareDeck).setOnClickListener {
            shareCurrentDeck()
        }

        view.findViewById<Button>(R.id.btnCopyReference).setOnClickListener {
            copyReferenceDeck()
        }

        viewLifecycleOwner.lifecycleScope.launch {
            combine(cardViewModel.currentDeck, cardViewModel.currentDeckId) { deck, deckId ->
                deck to deckId
            }.collectLatest { (deck, deckId) ->
                latestCards = deck
                latestDeckName = deckId
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            cardViewModel.currentDeck.collectLatest { deck ->
                val config = cardViewModel.hapticConfig.value
                val mappedItems =
                    deck.mapIndexed { index, cardName ->
                        val pattern = cardViewModel.patternFor(cardName, config)
                        ReferenceItem(index + 1, cardName, pattern?.description ?: "--", pattern)
                    }
                latestRows =
                    mappedItems.map { item ->
                        ReferenceDeckRow(item.position, item.cardName, item.patternDesc)
                    }
                rvReference?.adapter = ReferenceAdapter(mappedItems)
            }
        }
    }

    private fun shareCurrentDeck() {
        val text = ReferenceDeckFormatter.format(latestDeckName, latestCards)
        val send =
            Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_deck_subject, latestDeckName))
            }
        startActivity(Intent.createChooser(send, getString(R.string.share_deck_chooser_title)))
    }

    private fun copyReferenceDeck() {
        val text = ReferenceDeckFormatter.format(latestRows)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("cheat-sheet", text))
        Toast.makeText(requireContext(), getString(R.string.reference_copied), Toast.LENGTH_SHORT)
            .show()
    }

    private fun copyReferenceRow(item: ReferenceItem) {
        val label = ReferenceRowCopy.clipboardText(item.position, item.cardName, item.patternDesc)
        val clipboard =
            requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText("cheat-sheet-row", label))
        Toast.makeText(requireContext(), "Copied cheat-sheet row", Toast.LENGTH_SHORT).show()
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
            holder.itemView.setOnLongClickListener {
                copyReferenceRow(item)
                true
            }
        }

        override fun getItemCount() = items.size
    }
}
