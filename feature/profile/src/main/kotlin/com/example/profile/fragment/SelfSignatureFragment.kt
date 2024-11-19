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
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.profile.R
import com.example.profile.vm.ProfileViewModel
import com.example.ui.components.StandardCenterTopAppBar
import com.example.ui.components.resolveColor
import com.example.ui.dialog.IndicatorDialog
import com.example.ui.theme.JetItineraryTheme
import com.example.ui.theme.navigationBarDarkColor
import com.example.ui.theme.navigationBarLightColor
import com.example.ui.utils.AbstractComposeFragment

class SelfSignatureFragment : AbstractComposeFragment() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ComposeContent() {
        JetItineraryTheme {
            val initialSelfSignature = arguments?.getString("key_self_signature") ?: ""
            var selfSignatureValue by remember { mutableStateOf(initialSelfSignature) }
            val selfSignatureValueLength = remember(selfSignatureValue) {
                mutableIntStateOf(30 - selfSignatureValue.length)
            }

            val profileVm = hiltViewModel<ProfileViewModel>()
            val loading by profileVm.loading.collectAsStateWithLifecycle()

            Scaffold(
                topBar = {
                    StandardCenterTopAppBar(
                        title = stringResource(R.string.self_signature_fg_title),
                        actions = {
                            Button(
                                onClick = {
                                    profileVm.setSelfInfo(selfSignature = selfSignatureValue) {
                                        requireActivity().onBackPressedDispatcher.onBackPressed()
                                    }
                                },
                                modifier = Modifier.padding(end = 16.dp),
                                enabled = selfSignatureValue.isNotEmpty() && selfSignatureValue != initialSelfSignature,
                                shape = MaterialTheme.shapes.small,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(text = stringResource(R.string.save_btn))
                            }
                        },
                        colors = {
                            TopAppBarDefaults.topAppBarColors(
                                containerColor = resolveColor(navigationBarLightColor, navigationBarDarkColor),
                                titleContentColor = MaterialTheme.colorScheme.onBackground,
                                navigationIconContentColor = MaterialTheme.colorScheme.onBackground
                            )
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
                        value = selfSignatureValue,
                        onValueChange = {
                            if (it.length <= 30) {
                                selfSignatureValue = it
                            }
                        },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                        suffix = {
                            Text(
                                text = selfSignatureValueLength.intValue.toString(),
                                fontSize = 14.sp
                            )
                        },
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