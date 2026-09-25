package com.example.creator.model

import org.json.JSONArray
import org.json.JSONObject
import java.util.UUID

data class ChannelSocialLink(
    val id: String = UUID.randomUUID().toString(),
    val platform: String, // "Instagram", "Facebook", "X", "Telegram", "Discord", "Website", "Other"
    val title: String,
    val url: String
)

object SocialLinksJsonHelper {
    fun fromJson(jsonStr: String?): List<ChannelSocialLink> {
        if (jsonStr.isNullOrBlank()) return emptyList()
        val list = mutableListOf<ChannelSocialLink>()
        try {
            val array = JSONArray(jsonStr)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    ChannelSocialLink(
                        id = obj.optString("id", UUID.randomUUID().toString()),
                        platform = obj.optString("platform", "Website"),
                        title = obj.optString("title", ""),
                        url = obj.optString("url", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    fun toJson(links: List<ChannelSocialLink>): String {
        val array = JSONArray()
        links.forEach { link ->
            val obj = JSONObject()
            obj.put("id", link.id)
            obj.put("platform", link.platform)
            obj.put("title", link.title)
            obj.put("url", link.url)
            array.put(obj)
        }
        return array.toString()
    }
}

enum class UploadStatusStep {
    IDLE,
    PREPARING,
    UPLOADING,
    PROCESSING,
    CHECKING,
    PUBLISHING,
    COMPLETED,
    FAILED
}

data class VideoEditConfig(
    val trimStartPercent: Float = 0f,
    val trimEndPercent: Float = 1f,
    val rotationDegrees: Int = 0,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val isMuted: Boolean = false,
    val filterApplied: String = "Normal",
    val audioTrack: String? = null,
    val voiceOverUri: String? = null,
    val textOverlay: String? = null,
    val sticker: String? = null,
    val captionsEnabled: Boolean = false
)

val PRESET_AUDIO_TRACKS = listOf(
    "None",
    "Nexora Beat (Original)",
    "Cyber Synth Pulse",
    "Chill Lo-Fi Sunset",
    "Electronic Waves",
    "Gaming Hype Bass"
)

val PRESET_STICKERS = listOf("🔥", "⚡", "🎮", "🚀", "❤️", "💯", "✨", "🏆", "🎯", "👑")

val PRESET_FILTERS = listOf("Normal", "Vibrant", "Noir", "Cyber", "Warm", "Cool", "Vintage")
