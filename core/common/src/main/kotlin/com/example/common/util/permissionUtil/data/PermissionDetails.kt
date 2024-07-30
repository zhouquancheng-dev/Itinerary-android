package com.example.common.util.permissionUtil.data

data class PermissionDetails(
    val name: String,
    val description: String,
    val icon: Int,
    val permissions: List<String>
)