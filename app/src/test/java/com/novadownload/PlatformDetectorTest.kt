package com.novadownload

import com.novadownload.core.media.PlatformDetector
import com.novadownload.core.model.Platform
import org.junit.Assert.*
import org.junit.Test

class PlatformDetectorTest {
    @Test fun testYoutube() {
        assertEquals(Platform.YOUTUBE, PlatformDetector.detect("https://www.youtube.com/watch?v=dQw4w9WgXcQ"))
        assertEquals(Platform.YOUTUBE, PlatformDetector.detect("https://youtu.be/dQw4w9WgXcQ"))
    }
    @Test fun testTikTok() {
        assertEquals(Platform.TIKTOK, PlatformDetector.detect("https://www.tiktok.com/@user/video/123456"))
    }
    @Test fun testInstagram() {
        assertEquals(Platform.INSTAGRAM, PlatformDetector.detect("https://www.instagram.com/reel/abc123/"))
    }
    @Test fun testValidUrl() {
        assertTrue(PlatformDetector.isValidUrl("https://www.youtube.com/watch?v=abc"))
        assertFalse(PlatformDetector.isValidUrl("not a url"))
    }
    @Test fun testAutoDetectUnknown() {
        assertEquals(Platform.UNKNOWN, PlatformDetector.detect("https://example.com/video"))
    }
}
