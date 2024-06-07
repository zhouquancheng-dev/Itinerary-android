package com.example.login

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Phone
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.navArgument
import com.example.common.nav.AppDestinations
import com.example.login.nav.LoginPageDestinations
import com.example.login.nav.NavLoginActions
import com.example.login.vm.UserViewModel
import com.example.network.captcha.MyGTCaptcha4Client
import com.example.network.key.LOGIN_CAPTCHA_ID

@ExperimentalMaterial3Api
@ExperimentalLayoutApi
@ExperimentalComposeUiApi
fun NavGraphBuilder.loginGraph(
    navController: NavController,
    loginActions: NavLoginActions
) {
    navigation(
        startDestination = LoginPageDestinations.LoginPage.route,
        route = AppDestinations.LoginScreen.route,
        enterTransition = {
            when (initialState.destination.route) {
                else -> slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            }
        },
        exitTransition = {
            when (targetState.destination.route) {
                else -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Left)
            }
        },
        popEnterTransition = {
            when (initialState.destination.route) {
                else -> slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        },
        popExitTransition = {
            when (targetState.destination.route) {
                else -> slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Right)
            }
        }
    ) {
        composable(LoginPageDestinations.LoginPage.route) {
            val userViewModel = hiltViewModel<UserViewModel>()
            val openAlertDialog by userViewModel.openAlertDialog.collectAsStateWithLifecycle()
            val context = LocalContext.current

            LoginPage(
                openAlertDialog = openAlertDialog,
                onLoginClick = { username, password ->
                    MyGTCaptcha4Client.initCaptcha(context, LOGIN_CAPTCHA_ID)
                    userViewModel.safetyNetCaptcha(username, password) {
                        loginActions.loginPageToMainRoute()
                    }
                },
                navigateRegisterClick = {
                    loginActions.loginPageToRoute(LoginPageDestinations.RegisterPage.route)
                },
                navigateForgetPwdClick = {
                    loginActions.loginPageToRoute(LoginPageDestinations.ForgetPasswordPage.route)
                }
            ) {
                loginActions.loginPageToRoute(LoginPageDestinations.PhoneNumberLoginPage.route)
            }
        }

        composable(LoginPageDestinations.RegisterPage.route) {
            RegisterPage(
                onBackClick = {
                    loginActions.upPress()
                },
                onSendClick = {

                },
                onRegisterClick = { username, code, password, confirmPassword ->

                }
            )
        }

        composable(LoginPageDestinations.PhoneNumberLoginPage.route) { backStackEntry ->
            EnterPhoneNumberPage(
                title = stringResource(R.string.phoneLogin_text),
                contentText = stringResource(R.string.inputPhone_text),
                route = backStackEntry.destination.route,
                prefixIcon = Icons.Rounded.Phone,
                onBackClick = {
                    loginActions.upPress()
                },
                onNavigateClick = { countryCode, phoneNumber ->
                    loginActions.phonePageToVerifyCodeRoute(countryCode, phoneNumber)
                }
            )
        }

        composable(
            route = LoginPageDestinations.VerifyCodePage.route,
            arguments = listOf(navArgument("phoneNumber") { type = NavType.StringType })
        ) { backStackEntry ->
            val userViewModel = hiltViewModel<UserViewModel>()

            val countryCode = backStackEntry.arguments?.getString("countryCode") ?: ""
            val phoneNumber = backStackEntry.arguments?.getString("phoneNumber") ?: ""
            var newBizId by remember { mutableStateOf("") }

            val countryPhoneNumber = "$countryCode $phoneNumber"

            val previousRoute = navController.previousBackStackEntry?.destination?.route

            VerifyCodePage(
                phoneNumber = countryPhoneNumber,
                onBackClick = {
                    loginActions.upPress()
                },
                onSendClick = {
//                    userViewModel.sendSmsCode(phoneNumber) { biz ->
//                        newBizId = biz
//                    }
                },
                onVerifyClick = { smsCode ->
//                    userViewModel.verifySmsCode(phoneNumber, smsCode, newBizId) {
//                        loginActions.currentPageToMainRoute()
//                    }
                    if (previousRoute == LoginPageDestinations.PhoneNumberLoginPage.route) {
                        loginActions.loginPageToMainRoute()
                    } else {
                        loginActions.loginPageToRoute(LoginPageDestinations.ResetPasswordPage.route)
                    }
                }
            )
        }

        composable(LoginPageDestinations.ForgetPasswordPage.route) { backStackEntry ->
            EnterPhoneNumberPage(
                title = stringResource(R.string.forgetPassword_text1),
                contentText = stringResource(R.string.inputAccount_text),
                route = backStackEntry.destination.route,
                prefixIcon = Icons.Rounded.AccountCircle,
                onBackClick = {
                    loginActions.upPress()
                },
                onNavigateClick = { countryCode, phoneNumber ->
                    loginActions.phonePageToVerifyCodeRoute(countryCode, phoneNumber)
                }
            )
        }

        composable(LoginPageDestinations.ResetPasswordPage.route) {
            ResetPasswordPage {
                loginActions.upPress()
            }
        }
    }
}