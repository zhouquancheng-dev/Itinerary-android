package com.example.profile.fragment

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Done
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
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
import com.tencent.imsdk.v2.V2TIMUserFullInfo.V2TIM_GENDER_FEMALE
import com.tencent.imsdk.v2.V2TIMUserFullInfo.V2TIM_GENDER_MALE

class GenderFragment : AbstractComposeFragment() {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun ComposeContent() {
        JetItineraryTheme {
            val initialGender = arguments?.getInt("key_gender") ?: 0
            var genderValue by remember { mutableStateOf(initialGender) }

            val profileVm = hiltViewModel<ProfileViewModel>()
            val loading by profileVm.loading.collectAsStateWithLifecycle()

            Scaffold(
                topBar = {
                    StandardCenterTopAppBar(
                        title = stringResource(R.string.gender_fg_title),
                        textStyle = TextStyle(fontSize = 19.sp, fontWeight = FontWeight.Bold),
                        iconSize = DpSize(21.dp, 21.dp),
                        actions = {
                            Button(
                                onClick = {
                                    profileVm.setSelfInfo(gender = genderValue) {
                                        requireActivity().onBackPressedDispatcher.onBackPressed()
                                    }
                                },
                                modifier = Modifier.padding(end = 16.dp),
                                shape = MaterialTheme.shapes.small,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(text = stringResource(R.string.done_btn))
                            }
                        },
                        onPressClick = {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }
                    )
                },
                contentColor = MaterialTheme.colorScheme.onBackground
            ) { paddingValues ->
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(paddingValues)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable {
                                if (genderValue != V2TIM_GENDER_MALE) {
                                    genderValue = V2TIM_GENDER_MALE
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "男",
                            modifier = Modifier.padding(start = 24.dp),
                            fontSize = 18.sp
                        )
                        if (genderValue == V2TIM_GENDER_MALE) {
                            Icon(
                                imageVector = Icons.Rounded.Done,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 16.dp)
                                    .size(25.dp)
                            )
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(start = 21.dp),
                        thickness = (0.5).dp
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(64.dp)
                            .clickable {
                                if (genderValue != V2TIM_GENDER_FEMALE) {
                                    genderValue = V2TIM_GENDER_FEMALE
                                }
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "女",
                            modifier = Modifier.padding(start = 24.dp),
                            fontSize = 18.sp
                        )
                        if (genderValue == V2TIM_GENDER_FEMALE) {
                            Icon(
                                imageVector = Icons.Rounded.Done,
                                contentDescription = null,
                                modifier = Modifier
                                    .padding(end = 21.dp)
                                    .size(25.dp)
                            )
                        }
                    }
                }
            }

            IndicatorDialog(
                showDialog = loading,
                dialogText = stringResource(R.string.save_dialog_title)
            )
        }
    }

}