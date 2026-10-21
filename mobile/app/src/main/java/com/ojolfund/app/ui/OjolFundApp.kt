package com.ojolfund.app.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ojolfund.app.ui.components.OjolBottomBar
import com.ojolfund.app.ui.screens.AddExpenseScreen
import com.ojolfund.app.ui.screens.HomeScreen
import com.ojolfund.app.ui.screens.LoginScreen
import com.ojolfund.app.ui.screens.OtpScreen
import com.ojolfund.app.ui.screens.PhoneLoginScreen
import com.ojolfund.app.ui.screens.ProfileScreen
import com.ojolfund.app.ui.screens.RegisterScreen
import com.ojolfund.app.ui.screens.ShiftScreen
import com.ojolfund.app.ui.screens.TransactionsScreen
import com.ojolfund.app.ui.screens.VehicleScreen
import com.ojolfund.app.ui.screens.WelcomeScreen
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.viewmodel.AuthViewModel
import com.ojolfund.app.viewmodel.LedgerViewModel
import com.ojolfund.app.viewmodel.OtpAuthViewModel

object Routes {
    const val WELCOME = "welcome"
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val PHONE_LOGIN = "phone_login"
    const val OTP = "otp"
    const val HOME = "home"
    const val TRANSACTIONS = "transactions"
    const val SHIFT = "shift"
    const val VEHICLE = "vehicle"
    const val PROFILE = "profile"
    const val ADD_EXPENSE = "add_expense"

    val mainTabs = setOf(HOME, TRANSACTIONS, SHIFT, VEHICLE, PROFILE)
}

@Composable
fun OjolFundApp() {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val otpVm: OtpAuthViewModel = viewModel()
    val ledgerVm: LedgerViewModel = viewModel()

    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    Scaffold(
        containerColor = OjolColors.Canvas,
        bottomBar = {
            if (currentRoute in Routes.mainTabs) {
                OjolBottomBar(currentRoute = currentRoute) { route -> nav.navigateTab(route) }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.WELCOME,
            modifier = Modifier
                .padding(padding)
                .background(OjolColors.Canvas)
        ) {
            composable(Routes.WELCOME) {
                val activity = LocalActivity.current
                WelcomeScreen(
                    onStart = {
                        authVm.resetForm()
                        nav.navigate(Routes.REGISTER)
                    },
                    onLogin = {
                        authVm.resetForm()
                        nav.navigate(Routes.LOGIN)
                    },
                    onSkip = { nav.navigateToMain() },
                    onBack = { activity?.finish() }
                )
            }
            composable(Routes.LOGIN) {
                LoginScreen(
                    vm = authVm,
                    onBack = { nav.popBackStack() },
                    onLoggedIn = { nav.navigateToMain() },
                    onRegister = {
                        authVm.resetForm()
                        nav.navigate(Routes.REGISTER) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    },
                    onPhoneLogin = { nav.navigate(Routes.PHONE_LOGIN) }
                )
            }
            composable(Routes.REGISTER) {
                RegisterScreen(
                    vm = authVm,
                    onBack = { nav.popBackStack() },
                    // Email terisi otomatis + banner sukses di layar Masuk.
                    onRegistered = { nav.navigate(Routes.LOGIN) { popUpTo(Routes.REGISTER) { inclusive = true } } },
                    onLogin = {
                        authVm.resetForm()
                        nav.navigate(Routes.LOGIN) { popUpTo(Routes.REGISTER) { inclusive = true } }
                    }
                )
            }
            composable(Routes.PHONE_LOGIN) {
                PhoneLoginScreen(
                    vm = otpVm,
                    onBack = { nav.popBackStack() },
                    onOtpSent = { nav.navigate(Routes.OTP) },
                    onGoogle = { nav.navigateToMain() }
                )
            }
            composable(Routes.OTP) {
                OtpScreen(
                    vm = otpVm,
                    onBack = { nav.popBackStack() },
                    onVerified = { nav.navigateToMain() }
                )
            }
            composable(Routes.HOME) {
                HomeScreen(
                    ledgerVm = ledgerVm,
                    onAddExpense = { nav.navigate(Routes.ADD_EXPENSE) },
                    onStartShift = { nav.navigateTab(Routes.SHIFT) },
                    onSeeAll = { nav.navigateTab(Routes.TRANSACTIONS) },
                    onProfile = { nav.navigateTab(Routes.PROFILE) }
                )
            }
            composable(Routes.TRANSACTIONS) {
                TransactionsScreen(
                    ledgerVm = ledgerVm,
                    onAddTransaction = { nav.navigate(Routes.ADD_EXPENSE) },
                    onProfile = { nav.navigateTab(Routes.PROFILE) }
                )
            }
            composable(Routes.SHIFT) {
                ShiftScreen(onProfile = { nav.navigateTab(Routes.PROFILE) })
            }
            composable(Routes.VEHICLE) {
                VehicleScreen(
                    onAddFuel = { nav.navigate(Routes.ADD_EXPENSE) },
                    onProfile = { nav.navigateTab(Routes.PROFILE) }
                )
            }
            composable(Routes.PROFILE) {
                ProfileScreen(
                    onLogout = {
                        authVm.logout()
                        nav.navigate(Routes.WELCOME) {
                            popUpTo(nav.graph.id) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.ADD_EXPENSE) {
                AddExpenseScreen(
                    onBack = { nav.popBackStack() },
                    onSave = { amount, category, note ->
                        ledgerVm.addExpense(amount, category, note)
                        nav.popBackStack()
                    }
                )
            }
        }
    }
}

private fun NavHostController.navigateTab(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = false }
        launchSingleTop = true
    }
}

private fun NavHostController.navigateToMain() {
    navigate(Routes.HOME) {
        popUpTo(graph.id) { inclusive = true }
        launchSingleTop = true
    }
}
