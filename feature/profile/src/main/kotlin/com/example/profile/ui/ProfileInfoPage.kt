package com.example.profile.ui

import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.util.GlideEngine
import com.example.common.util.permissionUtil.AllowPermissionUseCase
import com.example.common.util.permissionUtil.ext.Constant.CAMERA
import com.example.common.util.permissionUtil.ext.Constant.READ_EXTERNAL_STORAGE
import com.example.common.util.permissionUtil.ext.Constant.READ_MEDIA_IMAGES
import com.example.common.util.permissionUtil.ext.Constant.READ_MEDIA_VIDEO
import com.example.common.util.startAcWithBundle
import com.example.profile.R
import com.example.profile.activity.CropImageActivity
import com.example.profile.vm.ProfileViewModel
import com.example.ui.coil.LoadAsyncImage
import com.example.ui.components.StandardCenterTopAppBar
import com.example.ui.components.bounceScrollEffect
import com.example.ui.components.resolveColor
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.theme.navigationBarDarkColor
import com.example.ui.theme.navigationBarLightColor
import com.example.ui.theme.tableDarkColor
import com.example.ui.theme.tableLightColor
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.config.SelectModeConfig
import com.luck.picture.lib.engine.CompressFileEngine
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener
import com.tencent.imsdk.v2.V2TIMUserFullInfo.V2TIM_GENDER_FEMALE
import com.tencent.imsdk.v2.V2TIMUserFullInfo.V2TIM_GENDER_MALE
import com.tencent.imsdk.v2.V2TIMUserFullInfo.V2TIM_GENDER_UNKNOWN
import top.zibin.luban.Luban
import top.zibin.luban.OnNewCompressListener
import java.io.File
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfoPage(
    onBack: () -> Unit,
    onNickName: (String) -> Unit,
    onGender: (Int) -> Unit,
    onSelfSignature: (String) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val profileVm = hiltViewModel<ProfileViewModel>()
    val profileInfo by profileVm.profile.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_CREATE) {
        profileVm.getUserInfo(lifecycleOwner)
    }
    val profileFirstOrNull = profileInfo.firstOrNull()

    val coroutineScope = rememberCoroutineScope()
    val maxOffsetY by remember { mutableFloatStateOf(with(density) { 300.dp.toPx() }) }
    val animatedOffsetY = remember { Animatable(0f) }

    Scaffold(
        topBar = {
            StandardCenterTopAppBar(
                title = stringResource(R.string.profile_info_title),
                textStyle = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Bold),
                iconSize = DpSize(21.dp, 21.dp),
                colors = {
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = resolveColor(navigationBarLightColor, navigationBarDarkColor),
                        titleContentColor = MaterialTheme.colorScheme.onBackground,
                        navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                    )
                },
                onPressClick = onBack
            )
        },
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .bounceScrollEffect(maxOffsetY, coroutineScope, animatedOffsetY)
        ) {
            Column(
                modifier = Modifier
                    .matchParentSize()
                    .verticalScroll(rememberScrollState())
                    .offset { IntOffset(x = 0, y = animatedOffsetY.value.roundToInt()) },
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                InfoItem(
                    onClick = {
                        selectPicture(context) { result ->
                            val extra = Bundle().apply {
                                putString("compressPath", result?.firstOrNull()?.compressPath)
                            }
                            startAcWithBundle<CropImageActivity>(context, extra)
                        }
                    },
                    label = "头像",
                    modifier = Modifier.height(76.dp),
                    dividerEnabled = true
                ) {
                    LoadAsyncImage(
                        model = profileFirstOrNull?.faceUrl ?: R.drawable.ic_default_face,
                        modifier = Modifier
                            .size(55.dp)
                            .clip(RoundedCornerShape(8.dp)),
                    )
                }
                InfoItem(
                    onClick = { onNickName(profileFirstOrNull?.nickName ?: "") },
                    label = "昵称",
                    modifier = Modifier.height(58.dp),
                    dividerEnabled = true
                ) {
                    Text(
                        text = profileFirstOrNull?.nickName ?: "",
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )
                }
                InfoItem(
                    onClick = { onGender(profileFirstOrNull?.gender ?: V2TIM_GENDER_UNKNOWN) },
                    label = "性别",
                    modifier = Modifier.height(58.dp),
                    dividerEnabled = true
                ) {
                    val genderStr = when (profileFirstOrNull?.gender) {
                        V2TIM_GENDER_MALE -> "男"
                        V2TIM_GENDER_FEMALE -> "女"
                        V2TIM_GENDER_UNKNOWN -> ""
                        else -> ""
                    }
                    Text(
                        text = genderStr,
                        color = Color.Gray,
                        textAlign = TextAlign.End
                    )
                }
                InfoItem(
                    onClick = { onSelfSignature(profileFirstOrNull?.selfSignature ?: "") },
                    label = "个性签名",
                    modifier = Modifier.height(58.dp)
                ) {
                    Text(
                        text = profileFirstOrNull?.selfSignature ?: "",
                        modifier = Modifier.widthIn(max = 240.dp, min = 150.dp),
                        color = Color.Gray,
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun InfoItem(
    onClick: () -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    dividerEnabled: Boolean = false,
    content: @Composable () -> Unit
) {
    Surface(
        onClick = { onClick() },
        color = resolveColor(tableLightColor, tableDarkColor)
    ) {
        ConstraintLayout(
            modifier = modifier.fillMaxWidth()
        ) {
            val (labelItem, contentItem, iconItem) = createRefs()
            val divider = createRef()

            Text(
                text = label,
                modifier = Modifier.constrainAs(labelItem) {
                    start.linkTo(parent.start, 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                fontSize = 18.sp
            )

            Box(
                modifier = Modifier
                    .constrainAs(contentItem) {
                        end.linkTo(iconItem.start, 12.dp)
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                    },
                contentAlignment = Alignment.Center
            ) {
                content()
            }

            Icon(
                painter = painterResource(R.drawable.chevron_right_24dp),
                contentDescription = null,
                modifier = Modifier.constrainAs(iconItem) {
                    end.linkTo(parent.end, 16.dp)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                },
                tint = Color.LightGray
            )

            if (dividerEnabled) {
                HorizontalDivider(
                    modifier = Modifier.constrainAs(divider) {
                        start.linkTo(parent.start, 32.dp)
                        end.linkTo(parent.end)
                        bottom.linkTo(parent.bottom)
                    },
                    thickness = (0.5).dp
                )
            }
        }
    }
}

private fun selectPicture(
    context: Context,
    onResult: (ArrayList<LocalMedia>?) -> Unit
) {
    val permissions = mutableListOf<String>()
    permissions.add(CAMERA)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        permissions.add(READ_MEDIA_IMAGES)
    } else {
        permissions.add(READ_EXTERNAL_STORAGE)
    }
    AllowPermissionUseCase.requestMultiPermission(context as FragmentActivity, permissions) {
        PictureSelector.create(context)
            .openGallery(SelectMimeType.ofImage())
            .setImageEngine(GlideEngine.createGlideEngine())
            .setSelectionMode(SelectModeConfig.SINGLE)
            .setCompressEngine(CompressFileEngine { context, source, call ->
                Luban.with(context).load(source).ignoreBy(100)
                    .setCompressListener(object : OnNewCompressListener {
                        override fun onStart() {

                        }

                        override fun onSuccess(
                            source: String?,
                            compressFile: File?
                        ) {
                            if (compressFile != null) {
                                call?.onCallback(source, compressFile.absolutePath)
                            }
                        }

                        override fun onError(source: String?, e: Throwable?) {
                            call.onCallback(source, null)
                        }
                    }).launch()
            })
            .forResult(object : OnResultCallbackListener<LocalMedia> {
                override fun onResult(result: ArrayList<LocalMedia>?) {
                    onResult(result)
                }

                override fun onCancel() {

                }
            })
    }
}

@PreviewLightDark
@Composable
private fun ProfileInfoPreview() {
    JetItineraryTheme {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoItem(
                onClick = {},
                label = "昵称",
                modifier = Modifier.height(58.dp),
                dividerEnabled = true
            ) {
                Text(
                    text = "用户昵称",
                    color = Color.Gray,
                    textAlign = TextAlign.End
                )
            }
            InfoItem(
                onClick = {},
                label = "个性签名",
                modifier = Modifier.height(58.dp),
                dividerEnabled = true
            ) {
                Text(
                    text = "用户个性签名",
                    color = Color.Gray,
                    textAlign = TextAlign.End
                )
            }
        }
    }
}