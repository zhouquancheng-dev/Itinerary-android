package com.example.profile.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.common.util.ext.ClickExt.isFastClick
import com.example.profile.R
import com.example.profile.state.DialogType
import com.example.profile.vm.UploadImageViewModel
import com.example.ui.dialog.IndicatorDialog
import com.example.ui.dialog.ProgressDialog
import com.example.ui.theme.primaryLight
import com.smarttoolfactory.cropper.ImageCropper
import com.smarttoolfactory.cropper.model.OutlineType
import com.smarttoolfactory.cropper.model.RectCropShape
import com.smarttoolfactory.cropper.model.aspectRatios
import com.smarttoolfactory.cropper.settings.CropDefaults
import com.smarttoolfactory.cropper.settings.CropOutlineProperty
import com.smarttoolfactory.cropper.settings.CropType

@Composable
fun CropImage(
    uploadVm: UploadImageViewModel,
    imagePath: String,
    onBack: () -> Unit,
    onCropStart: () -> Unit,
    onCropImageResult: (ImageBitmap) -> Unit
) {
    val imageBitmap = remember(imagePath) { BitmapFactory.decodeFile(imagePath)?.asImageBitmap() }
    var confirmCrop by remember { mutableStateOf(false) }
    var rotationAngle by remember { mutableFloatStateOf(0f) }

    val animatedRotationAngle by animateFloatAsState(
        targetValue = rotationAngle,
        animationSpec = tween(durationMillis = 50, easing = LinearOutSlowInEasing),
        label = "rotatable"
    )
    val rotatedImage by remember(animatedRotationAngle, imageBitmap) {
        derivedStateOf {
            imageBitmap?.let { rotateImage(it, animatedRotationAngle) }
        }
    }

    val handleSize = LocalDensity.current.run { 15.dp.toPx() }
    val cropProperties by remember {
        mutableStateOf(
            CropDefaults.properties(
                cropType = CropType.Static,
                handleSize = handleSize,
                maxZoom = 4f,
                contentScale = ContentScale.Inside,
                cropOutlineProperty = CropOutlineProperty(
                    OutlineType.Rect,
                    RectCropShape(0, "rect")
                ),
                aspectRatio = aspectRatios[3].aspectRatio,
                overlayRatio = 0.9f
            )
        )
    }
    val cropStyle by remember {
        mutableStateOf(CropDefaults.style(drawOverlay = true, drawGrid = false, strokeWidth = 2.dp))
    }

    val dialog by uploadVm.dialogType.collectAsStateWithLifecycle()
    val uploadProgress by uploadVm.uploadProgress.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        rotatedImage?.let { image ->
            ImageCropper(
                modifier = Modifier.matchParentSize(),
                imageBitmap = image,
                contentDescription = null,
                cropStyle = cropStyle,
                cropProperties = cropProperties,
                crop = confirmCrop,
                onCropStart = onCropStart,
                onCropSuccess = onCropImageResult,
            )
        }

        Row(
            modifier = Modifier
                .background(Color.Black)
                .padding(start = 16.dp, end = 16.dp, bottom = 20.dp, top = 15.dp)
                .height(56.dp)
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            TextButton(
                onClick = { onBack() },
                colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
            ) {
                Text(
                    text = stringResource(R.string.cancel_btn),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    if (!isFastClick() && rotationAngle != 0f && rotationAngle != -360f) {
                        rotationAngle = 0f
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_history_24dp),
                    contentDescription = null,
                    modifier = Modifier.size(28.dp),
                    tint = Color.White
                )
            }

            Spacer(Modifier.weight(1f))
            IconButton(
                onClick = {
                    if (!isFastClick()) {
                        rotationAngle = (rotationAngle - 90f) % 360f
                        if (rotationAngle > 0f) {
                            rotationAngle -= 360f
                        }
                    }
                }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_rotate),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp),
                    tint = Color.White
                )
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = { confirmCrop = true },
                shape = MaterialTheme.shapes.small,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryLight,
                    contentColor = Color.White
                ),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = stringResource(R.string.confirm_btn),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }

    IndicatorDialog(
        showDialog = dialog == DialogType.CROP || dialog == DialogType.SETTING,
        dialogText = stringResource(dialog.dialogText)
    )
    ProgressDialog(
        showDialog = dialog == DialogType.UPLOADING,
        progress = uploadProgress,
        dialogText = stringResource(dialog.dialogText)
    )
}

fun rotateImage(bitmap: ImageBitmap, angle: Float): ImageBitmap {
    val matrix = Matrix().apply {
        postRotate(angle)
    }
    val rotatedBitmap = Bitmap.createBitmap(
        bitmap.asAndroidBitmap(),
        0, 0,
        bitmap.width,
        bitmap.height,
        matrix,
        true
    )
    return rotatedBitmap.asImageBitmap()
}

@Preview
@Composable
private fun CropImageButtonPreview() {
    Row(
        modifier = Modifier
            .height(56.dp)
            .background(Color.Black)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextButton(
            onClick = {},
            colors = ButtonDefaults.textButtonColors(contentColor = Color.White)
        ) {
            Text(
                text = stringResource(R.string.cancel_btn),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.weight(1f))
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.ic_history_24dp),
                contentDescription = null,
                modifier = Modifier.size(28.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))
        IconButton(onClick = {}) {
            Icon(
                painter = painterResource(R.drawable.ic_rotate),
                contentDescription = null,
                modifier = Modifier.size(24.dp),
                tint = Color.White
            )
        }

        Spacer(Modifier.weight(1f))
        Button(
            onClick = {},
            shape = MaterialTheme.shapes.small,
            colors = ButtonDefaults.buttonColors(
                containerColor = primaryLight,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp)
        ) {
            Text(
                text = stringResource(R.string.confirm_btn),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
