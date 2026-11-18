package com.jetpack.moviecataloguejetpack.presentation.account

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AccountViewModel(application: Application) : AndroidViewModel(application) {

    private val prefs = application.getSharedPreferences("movie_catalogue_prefs", Context.MODE_PRIVATE)

    private val _currentLanguage = MutableStateFlow(getCurrentLanguageCode())
    val currentLanguage: StateFlow<String> = _currentLanguage.asStateFlow()

    private fun getCurrentLanguageCode(): String {
        val appLocales = AppCompatDelegate.getApplicationLocales()
        if (!appLocales.isEmpty) {
            val tag = appLocales.get(0)?.language
            if (!tag.isNullOrEmpty()) return tag
        }
        return prefs.getString("selected_language", "id") ?: "id"
    }

    fun setLanguage(languageCode: String) {
        prefs.edit().putString("selected_language", languageCode).apply()
        _currentLanguage.value = languageCode
        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)
    }

    fun getLanguageDisplayName(code: String): String {
        return when (code.lowercase()) {
            "id", "in" -> "Bahasa Indonesia (ID)"
            "en" -> "English (US)"
            else -> "Bahasa Indonesia (ID)"
        }
    }
}
