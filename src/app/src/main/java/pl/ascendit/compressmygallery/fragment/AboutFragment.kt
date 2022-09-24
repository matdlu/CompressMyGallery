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
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import pl.ascendit.compressmygallery.R
import pl.ascendit.compressmygallery.databinding.FragmentAboutBinding
import pl.ascendit.compressmygallery.helper.IntentHelper

class AboutFragment : Fragment() {
    private lateinit var binding: FragmentAboutBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return FragmentAboutBinding.inflate(inflater, container, false).also {
            binding = it
        }.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val mailSubject = getString(R.string.mail_subject)
        val mailAddress = getString(R.string.mail_address)
        val websiteUrl = getString(R.string.website_url)
        val githubUrl = getString(R.string.github_url)
        val linkedinUrl = getString(R.string.linkedin_url)

        val pkgName = context?.packageName
        val pm = context?.getPackageManager()
        val pkgInfo = pkgName?.let { pm?.getPackageInfo(it, 0) }
        val versionInfo = "${getString(R.string.version_info)}, ${getString(R.string.version)} ${pkgInfo?.versionName}"

        binding.tvVersion.text = versionInfo
        binding.tvLicence.setMovementMethod(LinkMovementMethod.getInstance())

        binding.ivMail.setOnClickListener {
            IntentHelper.sendMail(requireContext(), mailAddress, mailSubject)
        }

        binding.ivLinkedin.setOnClickListener {
            IntentHelper.startBrowser(requireContext(), linkedinUrl)
        }

        binding.ivGithub.setOnClickListener {
            IntentHelper.startBrowser(requireContext(), githubUrl)
        }

        binding.ivWebsite.setOnClickListener {
            IntentHelper.startBrowser(requireContext(), websiteUrl)
        }
    }

}
