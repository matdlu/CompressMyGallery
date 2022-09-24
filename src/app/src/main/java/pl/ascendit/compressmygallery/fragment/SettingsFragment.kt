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

package pl.ascendit.compressmygallery.fragment

import android.os.Bundle
import android.text.InputType
import android.widget.Toast
import androidx.preference.*
import pl.ascendit.compressmygallery.R
import pl.ascendit.compressmygallery.helper.CompHelper
import pl.ascendit.compressmygallery.helper.ValidationHelper
import pl.ascendit.compressmygallery.logic.SettingsLogic

class SettingsFragment : PreferenceFragmentCompat() {
    lateinit var changeFormatPreference: SwitchPreference
    lateinit var extensionPreference: ListPreference
    lateinit var changeDimensionPreference: SwitchPreference
    lateinit var widthPreference: EditTextPreference
    lateinit var qualityPreference: EditTextPreference
    lateinit var themePreference: ListPreference

    class PreferenceChangeListenerWrapper(val onPreferenceChangeFun: (preference: Preference, newValue: Any?) -> Boolean) : Preference.OnPreferenceChangeListener {
        override fun onPreferenceChange(preference: Preference, newValue: Any?): Boolean {
            return onPreferenceChangeFun(preference, newValue)
        }
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        val context = preferenceManager.context
        val screen = preferenceManager.createPreferenceScreen(context)
        val validation = ValidationHelper(onError = { str, message -> errorToast(message) })

        changeFormatPreference = SwitchPreference(context).apply {
            key = "changeFormat"
            title = getString(R.string.settings_change_format)
            summary = getString(R.string.settings_change_format_summary)
            setIcon(R.drawable.ic_baseline_insert_photo_24)
            setDefaultValue(SettingsLogic.Default.isAllowedToChangeFormat)
            onPreferenceChangeListener = PreferenceChangeListenerWrapper { preference, newValue ->
                CompHelper.pref.isAllowedToChangeFormat = newValue as Boolean
                extensionPreference.isEnabled = newValue as Boolean
                true
            }
        }

        extensionPreference = ListPreference(context).apply {
            key = "extension"
            title = getString(R.string.settings_extension)
            summary = SettingsLogic.getFormat()
            entries = arrayOf("JPEG", "WEBP")
            entryValues = arrayOf("jpg", "webp")
            dialogTitle = getString(R.string.settings_extension_dialog_title)
            setDefaultValue(SettingsLogic.Default.extension)
            isEnabled = CompHelper.pref.isAllowedToChangeFormat
            onPreferenceChangeListener = PreferenceChangeListenerWrapper { preference, newValue ->
                summary = SettingsLogic.extensionToFormat(newValue as String)
                true
            }
        }

        changeDimensionPreference = SwitchPreference(context).apply {
            key = "changeDimension"
            title = getString(R.string.settings_change_dimension)
            summary = getString(R.string.settings_change_dimension_summary)
            setIcon(R.drawable.ic_baseline_photo_size_select_large_24)
            setDefaultValue(SettingsLogic.Default.isAllowedToChangeDimension)
            onPreferenceChangeListener = PreferenceChangeListenerWrapper { preference, newValue ->
                CompHelper.pref.isAllowedToChangeDimension = newValue as Boolean
                widthPreference.isEnabled = newValue as Boolean
                true
            }
        }

        widthPreference = EditTextPreference(context).apply {
            key = "width"
            title = getString(R.string.settings_width)
            summary = SettingsLogic.getWidth().toString()
            dialogTitle = getString(R.string.settings_width_dialog_title)
            dialogMessage = getString(R.string.settings_width_summary)
            setDefaultValue(SettingsLogic.Default.width)
            isEnabled = CompHelper.pref.isAllowedToChangeDimension
            dialogLayoutResource = R.layout.dialog_edit
            onPreferenceChangeListener = PreferenceChangeListenerWrapper { preference, newValue ->
                preference.summary = newValue as String?
                validation.positiveNumber(newValue)
            }
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        qualityPreference = EditTextPreference(context).apply {
            key = "quality"
            title = getString(R.string.settings_quality_level)
            summary = SettingsLogic.getQuality().toString()
            dialogTitle = getString(R.string.settings_quality_dialog_title)
            dialogMessage = getString(R.string.settings_quality_level_summary)
            setDefaultValue(SettingsLogic.Default.quality)
            setIcon(R.drawable.ic_baseline_photo_filter_24)
            dialogLayoutResource = R.layout.dialog_edit
            onPreferenceChangeListener = PreferenceChangeListenerWrapper { preference, newValue ->
                preference.summary = newValue as String?
                validation.positiveNumberBetween(newValue, 0, 100)
            }
            setOnBindEditTextListener { editText ->
                editText.inputType = InputType.TYPE_CLASS_NUMBER
            }
        }

        themePreference = ListPreference(context).apply {
            key = "theme"
            title = getString(R.string.settings_theme)
            summary = SettingsLogic.themeToUiEntry(context, SettingsLogic.getTheme())
            entries = arrayOf(getString(R.string.settings_theme_auto),
                getString(R.string.settings_theme_light),
                getString(R.string.settings_theme_dark))
            entryValues = arrayOf("system", "light", "dark")
            dialogTitle = getString(R.string.settings_theme_dialog_title)
            setDefaultValue(SettingsLogic.Default.theme)
            setIcon(R.drawable.ic_baseline_color_lens_24)
            onPreferenceChangeListener = PreferenceChangeListenerWrapper { preference, newValue ->
                SettingsLogic.changeTheme(newValue as String)
                true
            }
        }

        screen.apply {
            addPreference(changeFormatPreference)
            addPreference(extensionPreference)
            addPreference(changeDimensionPreference)
            addPreference(widthPreference)
            addPreference(qualityPreference)
            addPreference(themePreference)
        }

        preferenceScreen = screen
    }

    override fun onPause() {
        super.onPause()
        SettingsLogic.loadPrefsToObjects(requireContext())
    }

    fun errorToast(errStr: String) {
        requireActivity().runOnUiThread {
            Toast.makeText(context, errStr, Toast.LENGTH_LONG).show()
        }
    }
}
