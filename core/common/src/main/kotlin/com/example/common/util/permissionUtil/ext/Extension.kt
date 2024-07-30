package com.example.common.util.permissionUtil.ext

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import com.permissionx.guolindev.PermissionX

fun Fragment.requestPermission(
    permissions: List<String>,
    explainMessage: String = "需要您同意以下权限才能正常使用",
    requestReasonDiaLeftButtonStr: String? = "拒绝",
    requestReasonDiaRightButtonStr: String = "允许",
    forwardToSettingMessage: String = "请在设置中允许必要权限",
    forwardToSettingLeftButtonStr: String? = "取消",
    forwardToSettingRightButtonStr: String = "去设置",
    deniedBlock: (() -> Unit)? = null,
    grantedBlock: (() -> Unit)? = null
) {
    PermissionX.init(this)
        .permissions(permissions)
        .explainReasonBeforeRequest()
        .onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                explainMessage,
                requestReasonDiaRightButtonStr,
                requestReasonDiaLeftButtonStr
            )
        }
        .onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                forwardToSettingMessage,
                forwardToSettingRightButtonStr,
                forwardToSettingLeftButtonStr
            )
        }
        .request { allGranted, _, _ ->
            if (allGranted) {
                grantedBlock?.invoke()
            } else {
                deniedBlock?.invoke()
            }
        }
}

fun FragmentActivity.requestPermission(
    permissions: List<String>,
    explainMessage: String = "需要您同意以下权限才能正常使用",
    requestReasonDiaLeftButtonStr: String? = "拒绝",
    requestReasonDiaRightButtonStr: String = "允许",
    forwardToSettingMessage: String = "请在设置中允许必要权限",
    forwardToSettingLeftButtonStr: String? = "取消",
    forwardToSettingRightButtonStr: String = "去设置",
    deniedBlock: (() -> Unit)? = null,
    grantedBlock: (() -> Unit)? = null
) {
    PermissionX.init(this)
        .permissions(permissions)
        .explainReasonBeforeRequest()
        .onExplainRequestReason { scope, deniedList ->
            scope.showRequestReasonDialog(
                deniedList,
                explainMessage,
                requestReasonDiaRightButtonStr,
                requestReasonDiaLeftButtonStr
            )
        }
        .onForwardToSettings { scope, deniedList ->
            scope.showForwardToSettingsDialog(
                deniedList,
                forwardToSettingMessage,
                forwardToSettingRightButtonStr,
                forwardToSettingLeftButtonStr
            )
        }
        .request { allGranted, _, _ ->
            if (allGranted) {
                grantedBlock?.invoke()
            } else {
                deniedBlock?.invoke()
            }
        }
}