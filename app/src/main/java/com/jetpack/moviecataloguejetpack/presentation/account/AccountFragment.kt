package com.jetpack.moviecataloguejetpack.presentation.account

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.jetpack.moviecataloguejetpack.R
import com.jetpack.moviecataloguejetpack.databinding.FragmentAccountBinding
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AccountViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeLanguage()

        binding.btnSettingLanguage.setOnClickListener {
            showLanguageDialog()
        }
    }

    private fun observeLanguage() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentLanguage.collect { code ->
                    binding.tvCurrentLanguage.text = viewModel.getLanguageDisplayName(code)
                }
            }
        }
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            getString(R.string.lang_indonesian),
            getString(R.string.lang_english),
            "Pengaturan Bahasa Perangkat"
        )
        val languageCodes = arrayOf("id", "en", "system")

        val currentCode = viewModel.currentLanguage.value
        val checkedItem = when (currentCode.lowercase()) {
            "en" -> 1
            else -> 0
        }

        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_language)
            .setSingleChoiceItems(languages, checkedItem) { dialog, which ->
                dialog.dismiss()
                if (which == 2) {
                    try {
                        startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "Tidak dapat membuka pengaturan perangkat", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    val selectedCode = languageCodes[which]
                    viewModel.setLanguage(selectedCode)
                    Toast.makeText(requireContext(), R.string.lang_changed_toast, Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
