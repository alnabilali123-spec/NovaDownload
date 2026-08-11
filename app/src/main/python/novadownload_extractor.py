"""
Real yt-dlp bridge for Chaquopy.
This file is executed inside Android Python runtime.
"""
import yt_dlp
import json
import os

def extract_info_json(url, cookiefile):
    ydl_opts = {
        'quiet': True,
        'no_warnings': True,
        'skip_download': True,
        'noplaylist': True,
        'extract_flat': False,
    }
    if cookiefile and os.path.exists(cookiefile):
        ydl_opts['cookiefile'] = cookiefile
    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=False)
        return json.dumps(info, default=str)

def get_direct_url(url, format_id):
    ydl_opts = {
        'quiet': True,
        'no_warnings': True,
        'skip_download': True,
        'noplaylist': True,
    }
    with yt_dlp.YoutubeDL(ydl_opts) as ydl:
        info = ydl.extract_info(url, download=False)
        formats = info.get('formats', [])
        target = None
        if format_id and format_id != 'best':
            for f in formats:
                if f.get('format_id') == format_id:
                    target = f
                    break
        if not target:
            ydl_opts2 = {'format': format_id if format_id != 'best' else 'bestvideo+bestaudio/best', 'quiet': True, 'skip_download': True}
            with yt_dlp.YoutubeDL(ydl_opts2) as ydl2:
                info2 = ydl2.extract_info(url, download=False)
                if 'requested_formats' in info2:
                    req = info2['requested_formats']
                    video = next((x for x in req if x.get('vcodec') != 'none'), None)
                    audio = next((x for x in req if x.get('acodec') != 'none' and x.get('vcodec') == 'none'), None)
                    return json.dumps({
                        'url': video.get('url') if video else '',
                        'audio_url': audio.get('url') if audio else None,
                        'is_separate': True,
                        'ext': info2.get('ext', 'mp4')
                    })
                target = info2
        if target:
            return json.dumps({
                'url': target.get('url', ''),
                'audio_url': None,
                'is_separate': False,
                'ext': target.get('ext', 'mp4')
            })
        return json.dumps({
            'url': info.get('url', ''),
            'audio_url': None,
            'is_separate': False,
            'ext': info.get('ext', 'mp4')
        })
