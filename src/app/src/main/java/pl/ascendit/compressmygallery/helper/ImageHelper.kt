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

package pl.ascendit.compressmygallery.helper

import android.graphics.BitmapFactory
import android.widget.ImageView
import coil.load
import coil.request.CachePolicy
import coil.size.Dimension
import coil.size.Scale
import coil.size.Size
import coil.transform.RoundedCornersTransformation
import com.afollestad.materialdialogs.utils.MDUtil.waitForHeight

object ImageHelper {
    fun setImg(iv: ImageView, path: String) {
        iv.waitForHeight {
            iv.load(path) {
                size(Dimension.Undefined, Dimension(height))
                scale(Scale.FIT)
                diskCachePolicy(CachePolicy.DISABLED)
                networkCachePolicy(CachePolicy.DISABLED)
                memoryCachePolicy(CachePolicy.DISABLED)
                transformations(RoundedCornersTransformation(16.0f))
            }
        }
    }
}