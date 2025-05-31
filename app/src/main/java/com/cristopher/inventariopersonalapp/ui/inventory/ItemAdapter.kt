package com.cristopher.inventariopersonalapp.ui.inventory.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cristopher.inventariopersonalapp.R
import com.cristopher.inventariopersonalapp.data.model.Item
import com.cristopher.inventariopersonalapp.databinding.ItemInventoryBinding

class ItemAdapter(
    private val onEditClick: (Item) -> Unit,
    private val onDeleteClick: (String) -> Unit
) : ListAdapter<Item, ItemAdapter.ItemViewHolder>(ItemDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        val binding = ItemInventoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
    }

    inner class ItemViewHolder(private val binding: ItemInventoryBinding) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnEdit.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val item = getItem(position)
                    onEditClick(item)
                }
            }

            binding.btnDelete.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val itemId = getItem(position).id
                    onDeleteClick(itemId)
                }
            }
        }

        fun bind(item: Item) {
            binding.textName.text = item.nombre
            binding.textDescription.text = "Categoría: ${item.categoria}"
            binding.textQuantity.text = "Cantidad: ${item.cantidad}"

            if (item.imagenUrl.isNotEmpty()) {
                binding.imageItem.visibility = View.VISIBLE
                Glide.with(binding.imageItem.context)
                    .load(item.imagenUrl)
                    .into(binding.imageItem)
            } else {
                binding.imageItem.visibility = View.GONE
            }
        }
    }

    class ItemDiffCallback : DiffUtil.ItemCallback<Item>() {
        override fun areItemsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Item, newItem: Item): Boolean {
            return oldItem == newItem
        }
    }
}

