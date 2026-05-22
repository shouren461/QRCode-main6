package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

class CreateYoutubeModel : CreateResultModel(CreateFormat.Youtube) {

    enum class TYPE { VIDEO, CHANNEL, URL }

    fun setVideoId(videoId: String) {
        if (!CreateUtil.isOnlySpace(videoId)) {
            super.result = if (CreateUtil.isYoutubeLink(videoId)) {
                videoId
            } else {
                "https://www.youtube.com/watch?v=$videoId"
            }
        }
    }

    fun setChannelId(channelId: String) {
        if (!CreateUtil.isOnlySpace(channelId)) {
            super.result = if (CreateUtil.isYoutubeLink(channelId)) {
                channelId
            } else {
                "https://www.youtube.com/channel/$channelId"
            }
        }
    }

    fun setUrl(url: String) {
        if (!CreateUtil.isOnlySpace(url)) {
            super.result = if (CreateUtil.isYoutubeLink(url)) {
                url
            } else {
                "https://www.youtube.com/watch?v=$url"
            }
        }
    }

    override fun formatResult() {

    }
}