package com.metrolist.music.eq.data

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Saved EQ Profile with metadata
 */
@Serializable
data class SavedEQProfile(
    val id: String,                       // Unique identifier
    val name: String,                     // Display name
    val deviceModel: String,              // e.g., "Sony WH-1000XM4"
    val bands: List<ParametricEQBand>,    // EQ bands
    val preamp: Double = 0.0,             // Preamp gain in dB
    val isCustom: Boolean = false,        // Whether this is a custom imported profile
    val isActive: Boolean = false,        // Whether this profile is currently active
    val addedTimestamp: Long = System.currentTimeMillis()
)

/**
 * Repository for managing EQ profiles
 * Handles saving, loading, and activating EQ profiles
 */
@Singleton
class EQProfileRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "nanosonic_eq_profiles",
        Context.MODE_PRIVATE
    )

    private val json = Json {
        ignoreUnknownKeys = true
        prettyPrint = true
    }

    private val _profiles = MutableStateFlow<List<SavedEQProfile>>(emptyList())
    val profiles: StateFlow<List<SavedEQProfile>> = _profiles.asStateFlow()

    private val _activeProfile = MutableStateFlow<SavedEQProfile?>(null)
    val activeProfile: StateFlow<SavedEQProfile?> = _activeProfile.asStateFlow()

    companion object {
        private const val KEY_PROFILES = "eq_profiles"
        private const val KEY_ACTIVE_PROFILE_ID = "active_profile_id"
    }

    init {
        loadProfiles()
    }

    /**
     * Load all saved profiles from SharedPreferences
     */
    private fun loadProfiles() {
        try {
            val profilesJson = prefs.getString(KEY_PROFILES, null)
            if (profilesJson != null) {
                val loadedProfiles = json.decodeFromString<List<SavedEQProfile>>(profilesJson)
                _profiles.value = if (loadedProfiles.isEmpty()) getDefaultPresets() else loadedProfiles

                // Load active profile
                val activeId = prefs.getString(KEY_ACTIVE_PROFILE_ID, null)
                _activeProfile.value = _profiles.value.find { it.id == activeId }
            } else {
                val defaults = getDefaultPresets()
                _profiles.value = defaults
                saveDefaultProfilesSilently(defaults)
            }
        } catch (e: Exception) {
            println("Error loading EQ profiles: ${e.message}")
            val defaults = getDefaultPresets()
            _profiles.value = defaults
            _activeProfile.value = null
        }
    }

    private fun saveDefaultProfilesSilently(defaults: List<SavedEQProfile>) {
        try {
            val profilesJson = json.encodeToString<List<SavedEQProfile>>(defaults)
            prefs.edit { putString(KEY_PROFILES, profilesJson) }
        } catch (_: Exception) {}
    }

    fun getDefaultPresets(): List<SavedEQProfile> {
        return listOf(
            SavedEQProfile(
                id = "preset_flat",
                name = "Flat",
                deviceModel = "Default Preset",
                preamp = 0.0,
                bands = listOf(
                    ParametricEQBand(60.0, 0.0),
                    ParametricEQBand(250.0, 0.0),
                    ParametricEQBand(1000.0, 0.0),
                    ParametricEQBand(4000.0, 0.0),
                    ParametricEQBand(12000.0, 0.0)
                ),
                isCustom = false
            ),
            SavedEQProfile(
                id = "preset_chill_sleep_beats",
                name = "Chill Sleep Beats",
                deviceModel = "Sleep Recommended",
                preamp = -2.0,
                bands = listOf(
                    ParametricEQBand(80.0, 3.0),
                    ParametricEQBand(250.0, 2.0),
                    ParametricEQBand(1000.0, 0.0),
                    ParametricEQBand(4000.0, -2.0),
                    ParametricEQBand(8000.0, -4.0)
                ),
                isCustom = false
            ),
            SavedEQProfile(
                id = "preset_bass_boost",
                name = "Bass Boost",
                deviceModel = "Default Preset",
                preamp = -3.0,
                bands = listOf(
                    ParametricEQBand(60.0, 5.0),
                    ParametricEQBand(150.0, 3.5),
                    ParametricEQBand(400.0, 1.5),
                    ParametricEQBand(2000.0, 0.0),
                    ParametricEQBand(8000.0, 0.0)
                ),
                isCustom = false
            ),
            SavedEQProfile(
                id = "preset_vocal_enhancement",
                name = "Vocal Enhancement",
                deviceModel = "Default Preset",
                preamp = -2.0,
                bands = listOf(
                    ParametricEQBand(100.0, -1.5),
                    ParametricEQBand(500.0, 1.0),
                    ParametricEQBand(1000.0, 3.0),
                    ParametricEQBand(3000.0, 4.0),
                    ParametricEQBand(8000.0, 1.0)
                ),
                isCustom = false
            )
        )
    }

    /**
     * Save a new EQ profile
     */
    suspend fun saveProfile(profile: SavedEQProfile) = withContext(Dispatchers.IO) {
        val currentProfiles = _profiles.value.toMutableList()

        // Check if profile with same ID already exists
        val existingIndex = currentProfiles.indexOfFirst { it.id == profile.id }

        if (existingIndex >= 0) {
            // Update existing profile
            currentProfiles[existingIndex] = profile
        } else {
            // Add new profile
            currentProfiles.add(profile)
        }

        // Save to SharedPreferences
        val profilesJson = json.encodeToString<List<SavedEQProfile>>(currentProfiles)
        prefs.edit { putString(KEY_PROFILES, profilesJson) }

        _profiles.value = currentProfiles
    }

    /**
     * Delete a profile
     */
    suspend fun deleteProfile(profileId: String) = withContext(Dispatchers.IO) {
        val currentProfiles = _profiles.value.toMutableList()
        currentProfiles.removeAll { it.id == profileId }

        val profilesJson = json.encodeToString<List<SavedEQProfile>>(currentProfiles)
        prefs.edit { putString(KEY_PROFILES, profilesJson) }

        // If deleted profile was active, clear active profile
        if (_activeProfile.value?.id == profileId) {
            _activeProfile.value = null
            prefs.edit { remove(KEY_ACTIVE_PROFILE_ID) }
        }

        _profiles.value = currentProfiles
    }

    /**
     * Set a profile as active (only one profile can be active at a time)
     * Pass null to deactivate all profiles
     */
    suspend fun setActiveProfile(profileId: String?) = withContext(Dispatchers.IO) {
        val currentProfiles = _profiles.value

        if (profileId == null) {
            // Deactivate all profiles
            _activeProfile.value = null
            prefs.edit { remove(KEY_ACTIVE_PROFILE_ID) }
        } else {
            val profile = currentProfiles.find { it.id == profileId }
            _activeProfile.value = profile
            prefs.edit { putString(KEY_ACTIVE_PROFILE_ID, profileId) }
        }
    }

    /**
     * Get all saved profiles
     */
    fun getAllProfiles(): List<SavedEQProfile> {
        return _profiles.value
    }

    /**
     * Get active profile
     */
    fun getActiveProfile(): SavedEQProfile? {
        return _activeProfile.value
    }

    /**
     * Import a custom EQ profile from ParametricEQ data
     */
    suspend fun importCustomProfile(
        name: String,
        parametricEQ: ParametricEQ
    ) = withContext(Dispatchers.IO) {
        // Generate unique ID for custom profile
        val id = "custom_${System.currentTimeMillis()}_${name.hashCode()}"

        val customProfile = SavedEQProfile(
            id = id,
            name = name,
            deviceModel = name,
            bands = parametricEQ.bands,  // Already ParametricEQBand
            preamp = parametricEQ.preamp,
            isActive = false,
            isCustom = true // Ensure this flag is set!
        )

        saveProfile(customProfile)
    }

    /**
     * Get profiles sorted by type: AutoEQ first, then custom profiles
     * Within each group, sort by timestamp (newest first)
     */
    fun getSortedProfiles(): List<SavedEQProfile> {
        val defaultPresets = _profiles.value.filter { !it.isCustom }
        val customProfiles = _profiles.value.filter { it.isCustom }.sortedByDescending { it.addedTimestamp }
        return defaultPresets + customProfiles
    }
}
