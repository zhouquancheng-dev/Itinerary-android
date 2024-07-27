package com.example.im.utils

import android.content.Context
import com.example.im.R

object EmojiUtils {
    private lateinit var emojiMap: Map<String, String>

    fun init(context: Context) {
        if (::emojiMap.isInitialized) return
        val emojiKeys = context.resources.getStringArray(R.array.chat_emoji_key)
        val emojiNames = context.resources.obtainTypedArray(R.array.chat_emoji_name)
        emojiMap = emojiKeys.mapIndexed { index, key ->
            val emojiNameResId = emojiNames.getResourceId(index, 0)
            val emojiName = context.getString(emojiNameResId)
            key to emojiName
        }.toMap()
        emojiNames.recycle()
    }

    fun replaceEmojis(text: String): String {
        if (!::emojiMap.isInitialized) return text
        val pattern = Regex(emojiMap.keys.joinToString("|") { Regex.escape(it) })
        return pattern.replace(text) { matchResult ->
            emojiMap[matchResult.value] ?: matchResult.value
        }
    }
}
