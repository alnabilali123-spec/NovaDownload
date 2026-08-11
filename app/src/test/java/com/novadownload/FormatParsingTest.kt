package com.novadownload

import org.junit.Test
import org.junit.Assert.*
import org.json.JSONObject

class FormatParsingTest {
    @Test fun testParseSampleJson() {
        val json = """
        {
            "id": "test123",
            "title": "Test Video",
            "extractor_key": "Youtube",
            "duration": 120,
            "thumbnail": "https://example.com/thumb.jpg",
            "formats": [
                {"format_id": "137", "ext": "mp4", "height": 1080, "vcodec": "avc1", "acodec": "none", "filesize": 10000000},
                {"format_id": "140", "ext": "m4a", "vcodec": "none", "acodec": "mp4a", "abr": 128},
                {"format_id": "22", "ext": "mp4", "height": 720, "vcodec": "avc1", "acodec": "mp4a"}
            ]
        }
        """.trimIndent()
        val obj = JSONObject(json)
        assertEquals(3, obj.getJSONArray("formats").length())
        assertEquals("Test Video", obj.getString("title"))
    }
}
