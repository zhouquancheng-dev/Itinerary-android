package com.example.profile.ui

import android.Manifest
import android.os.Build
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.fragment.app.FragmentActivity
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.blankj.utilcode.util.LogUtils
import com.example.common.util.GlideEngine
import com.example.common.util.permissionUtil.AllowPermissionUseCase
import com.example.profile.R
import com.example.profile.vm.ProfileViewModel
import com.example.ui.coil.LoadAsyncImage
import com.example.ui.components.StandardCenterTopAppBar
import com.example.ui.theme.JetItineraryTheme
import com.luck.picture.lib.basic.PictureSelector
import com.luck.picture.lib.config.SelectMimeType
import com.luck.picture.lib.entity.LocalMedia
import com.luck.picture.lib.interfaces.OnResultCallbackListener

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileInfo(onBackClick: () -> Unit) {
    val profileVm = hiltViewModel<ProfileViewModel>()
    val context = LocalContext.current
    val permissions = remember { mutableListOf<String>() }
    val profileInfo by profileVm.profile.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        profileVm.getUserInfo()
    }

    Scaffold(
        topBar = {
            StandardCenterTopAppBar(
                title = stringResource(R.string.profile_info_title),
                textStyle = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Bold),
                iconSize = DpSize(21.dp, 21.dp),
                onPressClick = onBackClick
            )
        },
        contentColor = MaterialTheme.colorScheme.onBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            InfoItem(
                onClick = {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        permissions.add(Manifest.permission.READ_MEDIA_IMAGES)
                    } else {
                        permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE)
                    }
                    AllowPermissionUseCase.requestMultiPermission(context as FragmentActivity, permissions) {
                        PictureSelector.create(context)
                            .openGallery(SelectMimeType.ofImage())
                            .setImageEngine(GlideEngine.createGlideEngine())
                            .setMaxSelectNum(1)
                            .forResult(object : OnResultCallbackListener<LocalMedia> {
                                override fun onResult(result: ArrayList<LocalMedia>?) {
                                    LogUtils.d(result?.firstOrNull()?.realPath)
                                }

                                override fun onCancel() {

                                }
                            })
                    }
                },
                label = "头像",
                modifier = Modifier.height(75.dp)
            ) {
                LoadAsyncImage(
                    model = profileInfo.firstOrNull()?.faceUrl ?: R.drawable.ic_default_face,
                    modifier = Modifier
                        .size(55.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }
            InfoItem(
                onClick = {},
                label = "昵称",
                modifier = Modifier.height(56.dp)
            ) {

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
        onClick = { onClick() }
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
                modifier = Modifier.constrainAs(contentItem) {
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
                }
            )
        }
    }
}

@PreviewLightDark
@Composable
private fun ProfileInfoPreview() {
    JetItineraryTheme {
        ProfileInfo {}
    }
}