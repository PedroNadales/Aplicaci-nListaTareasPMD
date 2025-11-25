package com.example.myapplication

import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.data.Note
import java.util.Locale

class NoteAdapter(
    private var items: List<Note>,
    private val onClick: (Note) -> Unit,
    private val onLongClick: (Note) -> Unit = {}
) : RecyclerView.Adapter<NoteAdapter.VH>() {

    inner class VH(view: View) : RecyclerView.ViewHolder(view) {
        val tvTitle: TextView = view.findViewById(R.id.tvNoteTitle)
        val tvDate: TextView = view.findViewById(R.id.tvNoteDate)
        val tvContent: TextView = view.findViewById(R.id.tvNoteContent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.item_note, parent, false)

        // aplicar ripple/selectable background
        val typedValue = TypedValue()
        val resolved = parent.context.theme.resolveAttribute(android.R.attr.selectableItemBackground, typedValue, true)
        if (resolved) {
            v.setBackgroundResource(typedValue.resourceId)
        } else {
            v.setBackgroundColor(0xFFFFFFFF.toInt())
        }

        // padding y elevación ligera para efecto "card"
        val pad = (parent.context.resources.displayMetrics.density * 12).toInt()
        v.setPadding(pad, pad, pad, pad)
        v.elevation = 4f

        return VH(v)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val n = items[position]
        holder.tvTitle.text = if (n.important) "★ ${n.title}" else n.title
        holder.tvDate.text = n.date
        val ratingText = if (n.rating > 0f) String.format(Locale.getDefault(), "Rating: %.1f", n.rating) else ""
        val priorityText = "Prioridad: ${n.priority}"
        // mostrar contenido + rating + prioridad
        holder.tvContent.text = listOf(n.content, ratingText, priorityText).filter { it.isNotEmpty() }.joinToString("  •  ")

        // alternar color de fondo suave por posición para mejor lectura
        val bg = if (position % 2 == 0) 0xFFFDFDFD.toInt() else 0xFFF6F7FB.toInt()
        holder.itemView.setBackgroundColor(bg)

        holder.itemView.setOnClickListener { onClick(n) }
        holder.itemView.setOnLongClickListener {
            onLongClick(n)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    fun update(list: List<Note>) {
        this.items = list
        notifyDataSetChanged()
    }
}
