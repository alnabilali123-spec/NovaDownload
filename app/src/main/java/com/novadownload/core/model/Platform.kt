package com.novadownload.core.model

enum class Platform(val displayName: String, val domains: List<String>) {
    YOUTUBE("YouTube", listOf("youtube.com", "youtu.be", "youtube-nocookie.com")),
    TIKTOK("TikTok", listOf("tiktok.com", "vt.tiktok.com")),
    INSTAGRAM("Instagram", listOf("instagram.com", "instagr.am")),
    FACEBOOK("Facebook", listOf("facebook.com", "fb.watch", "fb.com")),
    TWITTER("X / Twitter", listOf("twitter.com", "x.com", "t.co")),
    REDDIT("Reddit", listOf("reddit.com", "redd.it")),
    VIMEO("Vimeo", listOf("vimeo.com")),
    DAILYMOTION("Dailymotion", listOf("dailymotion.com", "dai.ly")),
    TWITCH("Twitch", listOf("twitch.tv", "clips.twitch.tv")),
    PINTEREST("Pinterest", listOf("pinterest.com", "pin.it")),
    SNAPCHAT("Snapchat", listOf("snapchat.com")),
    SOUNDCLOUD("SoundCloud", listOf("soundcloud.com")),
    BILIBILI("Bilibili", listOf("bilibili.com", "b23.tv")),
    UNKNOWN("Unknown", emptyList());

    companion object {
        fun detect(url: String): Platform {
            val lower = url.lowercase()
            return entries.firstOrNull { p -> p != UNKNOWN && p.domains.any { lower.contains(it) } } ?: UNKNOWN
        }
    }
}
