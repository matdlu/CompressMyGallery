/*
 * Copyright (c) 2022 Ascendit Sp. z o. o.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see https://www.gnu.org/licenses/
 *
 */

package pl.ascendit.compressmygallery.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pl.ascendit.compressmygallery.data.app.AppDb
import pl.ascendit.compressmygallery.data.app.entity.DirItem
import pl.ascendit.compressmygallery.databinding.DirItemBinding


class DirItemViewHolder(val binding: DirItemBinding)
    : RecyclerView.ViewHolder(binding.root) {

    fun bind(dir: DirItem) {
        binding.tvPath.text = dir.path
        binding.checkBox.isChecked = dir.selected
    }
}

class DirItemAdapter : RecyclerView.Adapter<DirItemViewHolder>() {
    val ltag = "DirItemAdapter"
    var data = mutableListOf<DirItem>()
    var onAdded:  (dir : DirItem) -> Unit = {}
    var onRemoved: (dir : DirItem) -> Unit = {}
    var onAllReplaced = {}

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DirItemViewHolder {
        val binding = DirItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DirItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DirItemViewHolder, position: Int) {
        holder.bind(data[position])
        holder.itemView.setOnClickListener {
            data[position].selected = ! data[position].selected
            holder.binding.checkBox.isChecked = data[position].selected
            Log.v(ltag, "item clicked ${data[position]}")
        }
        holder.binding.checkBox.setOnClickListener {
            data[position].selected = ! data[position].selected
            Log.v(ltag, "checkbox clicked ${data[position]}")
        }
    }

    override fun getItemCount(): Int = data.size

    fun getSelected() : List<DirItem> {
        val selected = mutableListOf<DirItem>()
        for (dir in data) {
            if (dir.selected) {
                selected.add(dir)
            }
        }
        Log.v(ltag, "selected dirs ${selected}")
        return selected;
    }

    fun removeSelected() {
        val toDelete = getSelected()
        for (dir in toDelete) {
            remove(dir)
        }
    }

    fun replace(dirs: List<DirItem>) {
        data.clear()
        data.addAll(dirs)
        onAllReplaced()
        notifyDataSetChanged()
    }

    fun add(dir: DirItem) {
        if ( ! data.contains(dir) ) {
            data.add(dir)
            onAdded(dir)
            notifyDataSetChanged()
        }
    }

    fun remove(dir: DirItem) {
        AppDb.deleteDirItem(dir)
        data.remove(dir)
        onRemoved(dir)
        notifyDataSetChanged()
    }
}
