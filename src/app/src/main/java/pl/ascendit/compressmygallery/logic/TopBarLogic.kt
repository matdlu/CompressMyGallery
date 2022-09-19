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

package pl.ascendit.compressmygallery.logic

import com.google.android.material.appbar.MaterialToolbar
import pl.ascendit.compressmygallery.R

object TopBarLogic {
    lateinit var topBar: MaterialToolbar

    fun updateTitle(str: String) {
        topBar.title = str
    }

    fun hasBackButton(value: Boolean) {
        if ( value ) {
            topBar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24)
            topBar.navigationContentDescription = "go back"
        } else {
            topBar.navigationIcon = null
        }
    }
}