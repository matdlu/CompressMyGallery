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

package pl.ascendit.compressmygallery.data.app.entity

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import pl.ascendit.compressmygallery.data.app.AppDb

@Entity
data class DirItem (
    @PrimaryKey(autoGenerate = false)
    var path : String = "", // required
    @Ignore
    var selected: Boolean = false
) {
    companion object {
        fun create(path: String): DirItem {
            val dirItem = DirItem(path = path)
            AppDb.insertDirItem(dirItem)
            return dirItem
        }
    }
}
