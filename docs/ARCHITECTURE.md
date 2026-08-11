# Architecture - Nova Download

## Layers
- Presentation: Jetpack Compose, Material 3, MVVM
- Domain: UseCases (AnalyzeUrlUseCase, SmartDownloadUseCase)
- Data: Room (downloads), DataStore (settings), OkHttp (network)
- Core Engine: YtDlpEngine, MediaExtractionManager, FallbackExtractor, EngineUpdateManager, ExtractorHealthMonitor, MediaProcessor

## Why Chaquopy + yt-dlp is REAL
yt-dlp is Python. Android Kotlin cannot import Python directly. Chaquopy embeds CPython 3.11 into APK as native libs (libpython). Gradle plugin downloads Python stdlib and pip packages at build time. Python code in src/main/python/ is bundled into APK's assets and loaded via Python.getModule(). This is production approach used by many apps.

Alternative considered: Termux binary + Runtime.exec() - fragile, requires storage permission, no version management.

## Extraction Flow
1. User pastes URL -> PlatformDetector (regex + domain list) -> UI shows platform chip
2. YtDlpEngine.extractInfo(url, cookieFile?) calls novadownload_extractor.extract_info_json via Chaquopy
3. JSON parsed in Kotlin to MediaInfo with sorted MediaFormat list
4. UI displays real formats: no fake 4K if not present. Uses height, vcodec, acodec, filesize
5. User selects format -> DownloadWorker gets direct URL via get_direct_url() Python call
6. If separate video+audio, downloads both via OkHttp with Range header for resume
7. FFmpegKit merges: -c:v copy -c:a aac -shortest
8. File saved to /Android/data/com.novadownload/files/NovaDownload/ -> MediaScanner + Room entry

## Update System
EngineUpdateManager:
- getVersion() calls yt_dlp.version.__version__ via Python
- checkForUpdate() GET https://api.github.com/repos/yt-dlp/yt-dlp/releases/latest
- Compare tags
- performUpdate() triggers pip install --upgrade yt-dlp inside Chaquopy env (Chaquopy supports runtime pip)
- Stores last attempt in SharedPreferences
- Rollback: if new version fails to import, Chaquopy falls back to bundled version (previous pip install remains in data dir)

## Health Monitor
Map<Platform, ExtractorStatus>. Reports:
- OPERATIONAL on success
- UNSUPPORTED_URL, AUTH_REQUIRED, REGION_RESTRICTED, REQUIRES_UPDATE, EXTRACTION_FAILED on error parsing
- UI screen SupportedPlatforms shows list with color dots

## Download Resilience
- OkHttp with timeouts 30s, retry interceptor
- 403 -> try with different user-agent, report AUTH_REQUIRED
- 429 -> exponential backoff
- 5xx -> retry with WorkManager backoff
- Storage errors caught and reported as INSUFFICIENT_STORAGE
- FFmpeg errors caught and reported as MEDIA_PROCESSING_FAILED

## Security
- No arbitrary executable download. Only PyPI (pip) and GitHub releases API for version check
- Cookies: optional user-provided Netscape format file, stored in EncryptedSharedPreferences (planned) or private files dir, never sent to third parties except original platform via yt-dlp cookiefile option
- Diagnostic copy redacts cookie paths

## Smart Download
Settings: preferred quality (2160,1440,1080,720,480...), format (mp4/webm), audio quality, wifi-only, max concurrent (WorkManager limited to 3)
SmartDownloadUseCase selects best matching combined format, or highest available <= preferred.

## Clipboard + Share
- MainActivity intent-filter for SEND text/plain -> receives URL
- Clipboard detection: WorkManager periodic check (if enabled) reads clipboard, shows snackbar "Media link detected" with Download/Ignore

## Browser
Optional internal browser using WebView: detects downloadable media via shouldInterceptRequest + yt-dlp extraction attempt on page URL. Does NOT bypass DRM.

## Testing Strategy
- Unit: PlatformDetectorTest, FormatParsingTest with sample yt-dlp JSONs
- Integration: YtDlpEngineTest with public test video https://www.youtube.com/watch?v=jNQXAC9IVRw (requires Python env)
- Instrumented: DownloadWorker test with MockWebServer for Range resume

## Known Limitations
- Instagram/Facebook may require login for some content -> app asks for cookie file, does not steal credentials
- YouTube age-restricted or DRM (widevine) not downloadable -> reports AUTH_REQUIRED or EXTRACTION_FAILED clearly
- APK size large due to Python + FFmpeg (~120-150MB debug). Release with abi split can reduce.
