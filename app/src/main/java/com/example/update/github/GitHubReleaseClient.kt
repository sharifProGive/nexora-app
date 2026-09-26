package com.example.update.github

import com.example.update.model.RemoteVersionConfig
import com.example.update.repository.SemVerUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

data class GitHubAsset(
    val name: String,
    val sizeBytes: Long,
    val browserDownloadUrl: String,
    val contentType: String?
)

data class GitHubRelease(
    val tagName: String,
    val name: String,
    val body: String,
    val publishedAt: String,
    val htmlUrl: String,
    val assets: List<GitHubAsset>
) {
    val cleanVersion: String
        get() = tagName.trim().removePrefix("v").removePrefix("V")

    val apkAsset: GitHubAsset?
        get() = assets.firstOrNull { it.name.endsWith(".apk", ignoreCase = true) }
            ?: assets.firstOrNull { it.contentType?.contains("android.package-archive", ignoreCase = true) == true }
}

sealed class GitHubReleaseResult {
    data class Success(val release: GitHubRelease) : GitHubReleaseResult()
    object NoReleasesFound : GitHubReleaseResult()
    data class Error(val message: String, val statusCode: Int = -1) : GitHubReleaseResult()
}

class GitHubReleaseClient(
    private val client: OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()
) {

    /**
     * Queries the latest release from the repository via GitHub Releases REST API.
     * Default target: https://api.github.com/repos/sharifProGive/nexora-app/releases/latest
     */
    suspend fun fetchLatestRelease(
        owner: String = "sharifProGive",
        repo: String = "nexora-app"
    ): GitHubReleaseResult = withContext(Dispatchers.IO) {
        val url = "https://api.github.com/repos/$owner/$repo/releases/latest"
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "nexora-app-updater")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                when (response.code) {
                    200 -> {
                        val bodyString = response.body?.string()
                        if (bodyString.isNullOrBlank()) {
                            return@withContext GitHubReleaseResult.NoReleasesFound
                        }
                        val release = parseReleaseJson(bodyString)
                        GitHubReleaseResult.Success(release)
                    }
                    404 -> {
                        // Check if repository has any releases at all
                        fetchFirstAvailableRelease(owner, repo)
                    }
                    else -> {
                        GitHubReleaseResult.Error(
                            message = "GitHub API returned HTTP ${response.code}: ${response.message}",
                            statusCode = response.code
                        )
                    }
                }
            }
        } catch (e: Exception) {
            GitHubReleaseResult.Error(
                message = e.localizedMessage ?: "Failed to connect to GitHub Releases API."
            )
        }
    }

    /**
     * Fallback to /releases endpoint in case /releases/latest returns 404 (e.g. if only prereleases exist)
     */
    private fun fetchFirstAvailableRelease(owner: String, repo: String): GitHubReleaseResult {
        val url = "https://api.github.com/repos/$owner/$repo/releases"
        val request = Request.Builder()
            .url(url)
            .header("Accept", "application/vnd.github.v3+json")
            .header("User-Agent", "nexora-app-updater")
            .build()

        return try {
            client.newCall(request).execute().use { response ->
                if (response.code == 200) {
                    val bodyString = response.body?.string()
                    if (!bodyString.isNullOrBlank()) {
                        val array = JSONArray(bodyString)
                        if (array.length() > 0) {
                            val firstReleaseJson = array.getJSONObject(0).toString()
                            return GitHubReleaseResult.Success(parseReleaseJson(firstReleaseJson))
                        }
                    }
                }
                GitHubReleaseResult.NoReleasesFound
            }
        } catch (e: Exception) {
            GitHubReleaseResult.NoReleasesFound
        }
    }

    private fun parseReleaseJson(jsonString: String): GitHubRelease {
        val obj = JSONObject(jsonString)
        val tagName = obj.optString("tag_name", "v1.0.0")
        val name = obj.optString("name", tagName)
        val body = obj.optString("body", "")
        val publishedAt = obj.optString("published_at", "")
        val htmlUrl = obj.optString("html_url", "https://github.com/sharifProGive/nexora-app")

        val assetsList = mutableListOf<GitHubAsset>()
        val assetsArray = obj.optJSONArray("assets")
        if (assetsArray != null) {
            for (i in 0 until assetsArray.length()) {
                val assetObj = assetsArray.getJSONObject(i)
                assetsList.add(
                    GitHubAsset(
                        name = assetObj.optString("name", "app-debug.apk"),
                        sizeBytes = assetObj.optLong("size", 0L),
                        browserDownloadUrl = assetObj.optString("browser_download_url", ""),
                        contentType = assetObj.optString("content_type", null)
                    )
                )
            }
        }

        return GitHubRelease(
            tagName = tagName,
            name = name,
            body = body,
            publishedAt = publishedAt,
            htmlUrl = htmlUrl,
            assets = assetsList
        )
    }

    /**
     * Converts a GitHubRelease to nexora's RemoteVersionConfig
     */
    fun toRemoteVersionConfig(
        release: GitHubRelease,
        installedVersionName: String,
        installedVersionCode: Int
    ): RemoteVersionConfig {
        val cleanVersion = release.cleanVersion
        // Derive approximate versionCode from semver if not explicitly given
        val derivedVersionCode = deriveVersionCode(cleanVersion).coerceAtLeast(installedVersionCode + 1)
        val apkAsset = release.apkAsset

        val sizeMb = if (apkAsset != null && apkAsset.sizeBytes > 0) {
            apkAsset.sizeBytes / (1024.0 * 1024.0)
        } else {
            28.5
        }

        val downloadUrl = apkAsset?.browserDownloadUrl?.ifBlank { release.htmlUrl }
            ?: release.htmlUrl

        val lines = release.body
            .lines()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .map { line ->
                line.removePrefix("- ").removePrefix("* ").removePrefix("# ").removePrefix("## ")
            }
            .filter { it.isNotBlank() }

        val releaseNotes = if (lines.isNotEmpty()) {
            lines
        } else {
            listOf("Version $cleanVersion released on GitHub with latest fixes and improvements.")
        }

        val formattedDate = formatReleaseDate(release.publishedAt)
        val isMandatory = release.body.contains("[MANDATORY]", ignoreCase = true) ||
                release.body.contains("MANDATORY_UPDATE", ignoreCase = true)

        return RemoteVersionConfig(
            latestVersion = cleanVersion,
            latestVersionCode = derivedVersionCode,
            minimumSupportedVersion = if (isMandatory) cleanVersion else "1.0.0",
            minimumSupportedVersionCode = if (isMandatory) derivedVersionCode else 1,
            updateRequired = isMandatory,
            updateDownloadUrl = downloadUrl,
            releaseNotes = releaseNotes,
            releaseDate = formattedDate,
            packageChecksum = null,
            packageSizeMb = String.format(Locale.US, "%.1f", sizeMb).toDoubleOrNull() ?: 28.5,
            packageIdentifier = "com.sharif.nexora"
        )
    }

    private fun deriveVersionCode(version: String): Int {
        val parts = version.split(".").mapNotNull { it.toIntOrNull() }
        return when (parts.size) {
            1 -> parts[0] * 10000
            2 -> parts[0] * 10000 + parts[1] * 100
            3 -> parts[0] * 10000 + parts[1] * 100 + parts[2]
            else -> 100
        }
    }

    private fun formatReleaseDate(raw: String): String {
        return try {
            if (raw.isBlank()) return "September 2026"
            // "2026-09-25T15:30:00Z"
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US)
            val date = inputFormat.parse(raw)
            if (date != null) {
                val outputFormat = SimpleDateFormat("MMM d, yyyy", Locale.US)
                outputFormat.format(date)
            } else {
                "Recent"
            }
        } catch (_: Exception) {
            "Recent Release"
        }
    }
}
