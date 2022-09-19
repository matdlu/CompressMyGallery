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

package pl.ascendit.compressmygallery.data.comp

import androidx.room.*
import pl.ascendit.compressmygallery.data.comp.entity.CompDir
import pl.ascendit.compressmygallery.data.comp.entity.CompFull
import pl.ascendit.compressmygallery.data.comp.entity.CompImg
import pl.ascendit.compressmygallery.data.comp.CompStatus

@Dao
internal interface CompDao {
    /* CompFull */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompFull(compFull: CompFull)

    @Query("SELECT * FROM CompFull")
    fun getCompFulls() : List<CompFull>

    @Query("SELECT count(1) FROM CompFull WHERE status = :status")
    fun countCompFulls(status: CompStatus): Int

    /* CompDir */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompDir(compDir: CompDir)

    @Query("SELECT * FROM CompDir")
    fun getCompDirs() : List<CompDir>

    @Query("SELECT * FROM CompDir WHERE compFullId = :compFullId")
    fun getCompDirs(compFullId: String) : List<CompDir>

    /* CompImg */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCompImg(compImg: CompImg)

    @Query("SELECT * FROM CompImg")
    fun getCompImgs() : List<CompImg>

    @Query("SELECT * FROM CompImg WHERE compDirId = :compDirId")
    fun getCompImgs(compDirId: String) : List<CompImg>

    @Query("SELECT count(1) FROM CompImg WHERE status = :status")
    fun countCompImgs(status: CompStatus): Int

    @Query("SELECT sum(sizeAfter) FROM CompImg WHERE status = :status")
    fun sizeAfterSum(status: CompStatus): Long

    @Query("SELECT sum(sizeBefore) FROM CompImg WHERE status = :status")
    fun sizeBeforeSum(status: CompStatus): Long
}
