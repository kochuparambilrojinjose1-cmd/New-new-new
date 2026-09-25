package com.example.model

import java.io.File

enum class ReleaseChannel(val displayName: String, val badgeColorHex: Long) {
    STABLE("Stable (Production)", 0xFF00E5FF),
    BETA("Beta Preview", 0xFFFF9100),
    NIGHTLY("Nightly Build", 0xFFFF0055)
}

data class AppReleaseInfo(
    val versionName: String,
    val versionCode: Int,
    val title: String,
    val releaseNotes: List<String>,
    val releaseDate: String,
    val fileSizeBytes: Long,
    val downloadUrl: String,
    val isMandatory: Boolean = false,
    val channel: ReleaseChannel = ReleaseChannel.STABLE,
    val minSupportedVersion: Int = 1
) {
    val fileSizeFormatted: String
        get() {
            val mb = fileSizeBytes.toDouble() / (1024 * 1024)
            return String.format(java.util.Locale.US, "%.1f MB", mb)
        }
}

sealed interface UpdateUiState {
    data object Idle : UpdateUiState

    data class Checking(val channel: ReleaseChannel) : UpdateUiState

    data class UpToDate(
        val currentVersionName: String,
        val currentVersionCode: Int,
        val lastCheckedTimestamp: Long = System.currentTimeMillis()
    ) : UpdateUiState

    data class UpdateAvailable(
        val release: AppReleaseInfo,
        val currentVersionName: String,
        val currentVersionCode: Int
    ) : UpdateUiState

    data class Downloading(
        val release: AppReleaseInfo,
        val progressPercent: Int,
        val downloadedBytes: Long,
        val totalBytes: Long
    ) : UpdateUiState

    data class ReadyToInstall(
        val release: AppReleaseInfo,
        val apkFile: File
    ) : UpdateUiState

    data class InAppApplying(
        val release: AppReleaseInfo,
        val stepMessage: String,
        val progressPercent: Int
    ) : UpdateUiState

    data class InAppUpdateSuccess(
        val release: AppReleaseInfo,
        val updatedVersionName: String,
        val updatedVersionCode: Int,
        val appliedTimestamp: Long = System.currentTimeMillis()
    ) : UpdateUiState

    data class Error(
        val message: String,
        val release: AppReleaseInfo? = null
    ) : UpdateUiState
}
