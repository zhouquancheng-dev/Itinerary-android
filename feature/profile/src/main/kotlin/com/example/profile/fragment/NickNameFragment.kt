package com.example.profile.fragment

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.profile.R
import com.example.profile.vm.ProfileViewModel
import com.example.ui.components.StandardCenterTopAppBar
import com.example.ui.dialog.IndicatorDialog
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.utils.AbstractComposeFragment

class NickNameFragment : AbstractComposeFragment() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ComposeContent() {
        JetItineraryTheme {
            val initialNickName = arguments?.getString("key_nickname") ?: ""
            var nickNameValue by remember { mutableStateOf(initialNickName) }

            val profileVm = hiltViewModel<ProfileViewModel>()
            val loading by profileVm.loading.collectAsStateWithLifecycle()

            Scaffold(
                topBar = {
                    StandardCenterTopAppBar(
                        title = stringResource(R.string.nickName_fg_title),
                        textStyle = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Bold),
                        iconSize = DpSize(21.dp, 21.dp),
                        actions = {
                            Button(
                                onClick = {
                                    profileVm.setSelfInfo(nickname = nickNameValue) {
                                        requireActivity().onBackPressedDispatcher.onBackPressed()
                                    }
                                },
                                modifier = Modifier.padding(end = 16.dp),
                                enabled = nickNameValue.isNotEmpty() && nickNameValue != initialNickName,
                                shape = MaterialTheme.shapes.small,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(text = stringResource(R.string.save_btn))
                            }
                        },
                        onPressClick = {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    )
                },
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { paddingValues ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                ) {
                    TextField(
                        value = nickNameValue,
                        onValueChange = {
                            if (it.length <= 16) {
                                nickNameValue = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent
                        ),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.None)
                    )
                }
            }

            IndicatorDialog(
                showDialog = loading,
                dialogText = stringResource(R.string.save_dialog_title)
            )
        }
    }

}