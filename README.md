# NOVA DOWNLOAD - Production Universal Media Downloader

Package: com.novadownload
Architecture: Clean Architecture + MVVM + Modular Extraction Engine
UI: Jetpack Compose + Material 3
Engine: yt-dlp (embedded via Chaquopy) + FFmpeg (ffmpeg-kit)
Min SDK 24, Target SDK 34

## Core Concept
Android UI (Compose) -> DownloadManager -> MediaExtractionManager -> Primary Extractor (YtDlpEngine via Chaquopy Python) -> Fallback (Cobalt-compatible) -> FFmpeg -> File

## What Makes This REAL
- No fake progress. Progress comes from OkHttp content-length and bytes written, plus FFmpegKit session logs.
- No placeholder format list. Formats parsed from yt-dlp JSON (formats[] array) with vcodec/acodec/filesize detection.
- Real Python runtime: Chaquopy embeds CPython 3.11. pip installs yt-dlp==2026.07.21. Python file src/main/python/novadownload_extractor.py exposes extract_info_json() and get_direct_url().
- Real merging: ffmpeg-kit-full executes: -i video -i audio -c:v copy -c:a aac -shortest output.mp4
- Real background: WorkManager + ForegroundService (dataSync) + pause/resume via HTTP Range.
- Real update manager: checks https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest, compares __version__, triggers pip install --upgrade yt-dlp.

## Supported Platforms
YouTube, TikTok, Instagram, Facebook, X/Twitter, Reddit, Vimeo, Dailymotion, Twitch, Pinterest, Snapchat public, SoundCloud, Bilibili + 1800+ others via yt-dlp. Detection is via PlatformDetector regex + yt-dlp extractor_key.

## Legal
This tool is for content you own or have permission to save and publicly accessible content where the platform provides it. No DRM bypass, no paywall bypass, no private content access. If extraction fails it reports clearly.

## Build
See BUILD.md - Requires Android Studio Hedgehog+, JDK17, NDK 25+, first sync installs Chaquopy Python.

## Structure
/app/src/main/java/com/novadownload/core/engine/YtDlpEngine.kt - REAL yt-dlp integration
/app/src/main/python/novadownload_extractor.py - Python bridge
/app/src/main/java/com/novadownload/core/media/MediaProcessor.kt - FFmpeg merging
/app/src/main/java/com/novadownload/core/download/DownloadWorker.kt - Real downloader with Range
/docs/ARCHITECTURE.md - Detailed architecture

## Limitations
- yt-dlp may require cookies for some Instagram/Facebook private/public content. App provides optional cookie file import stored securely, never transmitted.
- Some platforms change HTML frequently -> ExtractorHealthMonitor reports REQUIRES_UPDATE and UpdateManager can upgrade engine.
