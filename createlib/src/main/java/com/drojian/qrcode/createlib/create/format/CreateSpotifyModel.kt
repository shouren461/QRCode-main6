package com.drojian.qrcode.createlib.create.format

import com.drojian.qrcode.createlib.create.CreateFormat
import com.drojian.qrcode.createlib.create.CreateResultModel

data class CreateSpotifyModel(var artist: String = "", var song: String = "") : CreateResultModel(CreateFormat.Spotify) {


    override fun formatResult() {
        super.result =  "spotify:search:$artist;$song"
    }

}