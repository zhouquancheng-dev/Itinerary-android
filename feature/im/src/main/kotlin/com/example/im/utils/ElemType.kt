package com.example.im.utils

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.im.R
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_CUSTOM
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_FACE
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_FILE
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_GROUP_TIPS
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_IMAGE
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_LOCATION
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_MERGER
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_SOUND
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_TEXT
import com.tencent.imsdk.v2.V2TIMMessage.V2TIM_ELEM_TYPE_VIDEO
import com.tencent.imsdk.v2.V2TIMTextElem

data class ElemType(
    @StringRes val desc: Int? = null,
    val text: String? = null,
    val isResource: Boolean = false
)

@Composable
fun getLastMessageContent(type: Int, v2TIMElem: V2TIMTextElem?): String {
    val context = LocalContext.current
    EmojiUtils.init(context)

    if (type != V2TIM_ELEM_TYPE_TEXT) {
        return elemMap[type]?.let { stringResource(it.desc ?: return "") } ?: ""
    }

    val text = v2TIMElem?.text ?: return ""
    return EmojiUtils.replaceEmojis(text)
}

private val elemMap: Map<Int, ElemType> = mapOf(
    V2TIM_ELEM_TYPE_TEXT to ElemType(R.string.text_elem),
    V2TIM_ELEM_TYPE_CUSTOM to ElemType(R.string.custom_elem),
    V2TIM_ELEM_TYPE_IMAGE to ElemType(R.string.image_elem),
    V2TIM_ELEM_TYPE_SOUND to ElemType(R.string.sound_elem),
    V2TIM_ELEM_TYPE_VIDEO to ElemType(R.string.video_elem),
    V2TIM_ELEM_TYPE_FILE to ElemType(R.string.file_elem),
    V2TIM_ELEM_TYPE_LOCATION to ElemType(R.string.location_elem),
    V2TIM_ELEM_TYPE_FACE to ElemType(R.string.face_elem),
    V2TIM_ELEM_TYPE_MERGER to ElemType(R.string.relay_elem),
    V2TIM_ELEM_TYPE_GROUP_TIPS to ElemType(R.string.group_tips_elem),
)
