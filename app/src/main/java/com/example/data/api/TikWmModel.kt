package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class TikWmResponse(
    @Json(name = "code") val code: Int,
    @Json(name = "msg") val msg: String?,
    @Json(name = "data") val data: TikWmVideoData?
)

@JsonClass(generateAdapter = true)
data class TikWmVideoData(
    @Json(name = "id") val id: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "cover") val cover: String?,
    @Json(name = "play") val play: String?,
    @Json(name = "wmplay") val wmplay: String?,
    @Json(name = "music") val music: String?,
    @Json(name = "music_info") val music_info: TikWmMusicInfo?,
    @Json(name = "author") val author: TikWmAuthor?,
    @Json(name = "duration") val duration: Int?,
    @Json(name = "play_count") val play_count: Int?,
    @Json(name = "digg_count") val digg_count: Int?,
    @Json(name = "comment_count") val comment_count: Int?,
    @Json(name = "share_count") val share_count: Int?,
    @Json(name = "download_count") val download_count: Int?
)

@JsonClass(generateAdapter = true)
data class TikWmMusicInfo(
    @Json(name = "id") val id: String?,
    @Json(name = "title") val title: String?,
    @Json(name = "play") val play: String?,
    @Json(name = "author") val author: String?
)

@JsonClass(generateAdapter = true)
data class TikWmAuthor(
    @Json(name = "id") val id: String?,
    @Json(name = "unique_id") val unique_id: String?,
    @Json(name = "nickname") val nickname: String?,
    @Json(name = "avatar") val avatar: String?
)
