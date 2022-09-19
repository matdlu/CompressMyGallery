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

package pl.ascendit.compressmygallery.data.log

import androidx.room.*
import pl.ascendit.compressmygallery.data.app.entity.DirItem
import pl.ascendit.compressmygallery.data.log.entity.LogItem

@Dao
interface LogDao {
    /* DirItem */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insertLogItem(logItem: LogItem)

    @Query("SELECT * FROM LogItem")
    fun getLogItems() : List<LogItem>
}
