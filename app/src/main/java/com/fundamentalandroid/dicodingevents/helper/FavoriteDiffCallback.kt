package com.fundamentalandroid.dicodingevents.helper

import androidx.recyclerview.widget.DiffUtil
import com.fundamentalandroid.dicodingevents.data.local.entity.FavoriteEntity

class FavoriteDiffCallback(
    private val oldFavoriteList: List<FavoriteEntity>,
    private val newFavoriteList: List<FavoriteEntity>
) : DiffUtil.Callback() {
    override fun getOldListSize(): Int = oldFavoriteList.size
    override fun getNewListSize(): Int = newFavoriteList.size
    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldFavoriteList[oldItemPosition].id == newFavoriteList[newItemPosition].id
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val oldNote = oldFavoriteList[oldItemPosition]
        val newNote = newFavoriteList[newItemPosition]
        return oldNote.name == newNote.name && oldNote.mediaCover == newNote.mediaCover
    }
}