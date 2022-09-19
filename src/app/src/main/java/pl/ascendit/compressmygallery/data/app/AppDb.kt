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

package pl.ascendit.compressmygallery.data.app

import android.content.Context
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import pl.ascendit.compressmygallery.data.app.entity.DirItem

// Db for persisting UI stuff
object AppDb {
    val coroutineScope = CoroutineScope(Dispatchers.Main)
    private lateinit var database: AppDatabase
    private lateinit var dao: AppDao

    fun init(context: Context) {
        database = AppDatabase.getInstance(context)
        dao = database.dao
    }

    /* DirItem */
    fun insertDirItem(dirItem: DirItem) {
        coroutineScope.launch {
            dao.insertDirItem(dirItem)
        }
    }

    fun deleteDirItem(dirItem: DirItem) {
        coroutineScope.launch {
            dao.deleteDirItem(dirItem)
        }
    }

    fun getDirItems() : List<DirItem> {
        return dao.getDirItems()
    }

    fun getDirItem(path: String) : DirItem {
        return dao.getDirItem(path)
    }
}

