package com.example.common.util.media

data class MediaInfo(
    val uid: Long,
    val path: String,
    val displayName: String,
    val dateAdded: String,
    val size: String,
    val type: MediaType
)

enum class MediaType {
    IMAGE, VIDEO, AUDIO
}