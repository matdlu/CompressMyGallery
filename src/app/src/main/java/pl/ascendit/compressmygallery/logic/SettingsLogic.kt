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

import android.app.UiModeManager
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.text.isDigitsOnly
import androidx.preference.PreferenceManager
import pl.ascendit.compressmygallery.R
import pl.ascendit.compressmygallery.helper.CompHelper
import java.time.LocalDateTime

object SettingsLogic {
    val ltag = "SettingsLogic"

    lateinit var preferences: SharedPreferences

    object Default {
        val isAllowedToChangeFormat = false
        val isAllowedToChangeDimension = false
        val extension = "jpg" // valid: jpg, webp
        val width = "1920"
        val quality = "80"
        val theme = "system"
    }

    fun isFirstRun(): Boolean {
        return preferences.getBoolean("firstRun", true)
    }

    fun setFirstRun(value: Boolean) {
        preferences.edit().putBoolean("firstRun", value).commit()
    }

    fun getIsAllowedToChangeFormat(): Boolean {
        return preferences.getBoolean("changeFormat", Default.isAllowedToChangeFormat)
    }

    fun getIsAllowedToChangeDimension(): Boolean {
        return preferences.getBoolean("changeDimension", Default.isAllowedToChangeDimension)
    }

    fun getExtension(): String {
        return preferences.getString("extension", Default.extension)!!
    }

    fun getFormat(): String {
        return extensionToFormat(getExtension())
    }

    fun extensionToFormat(extension: String): String {
        return when (extension.lowercase()) {
            "webp" -> "WEBP"
            else -> "JPEG"
        }
    }


    fun getTheme(): String {
        return preferences.getString("theme", Default.theme)!!
    }

    private fun themeToUiMode(theme: String): Int {
        return when(theme.lowercase()) {
            "light" -> UiModeManager.MODE_NIGHT_NO
            "dark" -> UiModeManager.MODE_NIGHT_YES
            else -> UiModeManager.MODE_NIGHT_AUTO
        }
    }

    fun changeTheme(theme: String) {
        AppCompatDelegate.setDefaultNightMode(themeToUiMode(theme))
    }

    fun themeToUiEntry(context: Context, theme: String): String {
        return when(theme.lowercase()) {
            "light" -> context.getString(R.string.settings_theme_light)
            "dark" -> context.getString(R.string.settings_theme_dark)
            else -> context.getString(R.string.settings_theme_auto)
        }
    }

    fun getWidth(): Int {
        return Integer.parseInt(preferences.getString("width", Default.width)!!)
    }

    fun getQuality(): Int {
        return Integer.parseInt(preferences.getString("quality", Default.quality)!!)
    }

    fun loadPrefsToObjects(context: Context) {
        Log.d(ltag, "loading preferences")

        preferences = PreferenceManager.getDefaultSharedPreferences(context)

        changeTheme(getTheme())

        // CompHelper
        CompHelper.pref = CompHelper.Pref(
            isAllowedToChangeFormat = getIsAllowedToChangeFormat(),
            isAllowedToChangeDimension = getIsAllowedToChangeDimension(),
            extension = getExtension(),
            width = getWidth(),
            quality = getQuality(),
        )

        Log.d(ltag,"loaded preferences")
    }
}