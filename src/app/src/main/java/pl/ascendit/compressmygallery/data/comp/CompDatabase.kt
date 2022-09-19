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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import pl.ascendit.compressmygallery.data.comp.entity.CompDir
import pl.ascendit.compressmygallery.data.comp.entity.CompFull
import pl.ascendit.compressmygallery.data.comp.entity.CompImg
import pl.ascendit.compressmygallery.data.converter.ListStringConverter
import pl.ascendit.compressmygallery.data.converter.LocalDateTimeConverter

@Database(
    entities = [
        CompFull::class,
        CompDir::class,
        CompImg::class,
    ],
    version = 1
)
@TypeConverters(
    LocalDateTimeConverter::class,
    ListStringConverter::class,
)
internal abstract class CompDatabase : RoomDatabase() {
    abstract val dao: CompDao

    companion object {
        @Volatile
        private var INSTANCE: CompDatabase? = null

        fun getInstance(context: Context): CompDatabase {
            synchronized(this) {
                return INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    CompDatabase::class.java,
                    "comp.db"
                ).build().also {
                    INSTANCE = it
                }
            }
        }
    }
}
