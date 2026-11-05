package com.ojolfund.app.ui

import androidx.activity.compose.LocalActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.ojolfund.app.ui.components.OjolBottomBar
import com.ojolfund.app.ui.screens.AddExpenseScreen
import com.ojolfund.app.ui.screens.AddOrderScreen
import com.ojolfund.app.ui.screens.CommunityChatScreen
import com.ojolfund.app.ui.screens.CommunityScreen
import com.ojolfund.app.ui.screens.FuelScreen
import com.ojolfund.app.ui.screens.HomeScreen
import com.ojolfund.app.ui.screens.LoginScreen
import com.ojolfund.app.ui.screens.OtpScreen
import com.ojolfund.app.ui.screens.PhoneLoginScreen
import com.ojolfund.app.ui.screens.ProfileScreen
import com.ojolfund.app.ui.screens.RegisterScreen
import com.ojolfund.app.ui.screens.ReportScreen
import com.ojolfund.app.ui.screens.ShiftScreen
import com.ojolfund.app.ui.screens.SplashScreen
import com.ojolfund.app.ui.screens.SummaryScreen
import com.ojolfund.app.ui.screens.TargetScreen
import com.ojolfund.app.ui.screens.TransactionsScreen
import com.ojolfund.app.ui.screens.VehicleScreen
import com.ojolfund.app.ui.screens.WelcomeScreen
import com.ojolfund.app.ui.theme.OjolColors
import com.ojolfund.app.viewmodel.AuthViewModel
import com.ojolfund.app.viewmodel.CommunityViewModel
import com.ojolfund.app.viewmodel.LedgerViewModel
import com.ojolfund.app.viewmodel.OtpAuthViewModel
import com.ojolfund.app.viewmodel.PlanViewModel

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
    const val ADD_ORDER = "add_order"
    const val FUEL = "fuel"
    const val SUMMARY = "summary"
    const val REPORT = "report"
    const val TARGET = "target"
    const val COMMUNITY = "community"
    const val COMMUNITY_CHAT = "community_chat"

    val mainTabs = setOf(HOME, TRANSACTIONS, SHIFT, VEHICLE, PROFILE)

    /** Layar detail yang tetap menampilkan bottom bar (tidak ada tab yang terpilih). */
    val withBottomBar = mainTabs + setOf(SUMMARY, REPORT, TARGET)
}

@Composable
fun OjolFundApp() {
    val nav = rememberNavController()
    val authVm: AuthViewModel = viewModel(factory = AuthViewModel.Factory)
    val otpVm: OtpAuthViewModel = viewModel()
    val ledgerVm: LedgerViewModel = viewModel()
    val planVm: PlanViewModel = viewModel()
    val communityVm: CommunityViewModel = viewModel()

    val backStack by nav.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    var showSplash by rememberSaveable { mutableStateOf(true) }

    Box(Modifier.fillMaxSize()) {
        AppScaffold(nav, authVm, otpVm, ledgerVm, planVm, communityVm, currentRoute)
        AnimatedVisibility(visible = showSplash, exit = fadeOut(tween(SPLASH_FADE_MS))) {
            SplashScreen(
                onFinished = { showSplash = false },
                // Menelan sentuhan agar layar di bawah splash tidak bisa ditekan.
                modifier = Modifier.clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = {}
                )
            )
        }
    }
}

@Composable
private fun AppScaffold(
    nav: NavHostController,
    authVm: AuthViewModel,
    otpVm: OtpAuthViewModel,
    ledgerVm: LedgerViewModel,
    planVm: PlanViewModel,
    communityVm: CommunityViewModel,
    currentRoute: String?
) {
    Scaffold(
        containerColor = OjolColors.Canvas,
        bottomBar = {
            if (currentRoute in Routes.withBottomBar) {
                OjolBottomBar(currentRoute = currentRoute) { route -> nav.navigateTab(route) }
            }
        }
    ) { padding ->
        NavHost(
            navController = nav,
            startDestination = Routes.WELCOME,
            modifier = Modifier
                .padding(padding)
                .background(OjolColors.Canvas),
            enterTransition = { screenEnter() },
            exitTransition = { screenExit() },
            popEnterTransition = { screenPopEnter() },
            popExitTransition = { screenPopExit() }
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
                    onAddOrder = { nav.navigate(Routes.ADD_ORDER) },
                    onFuel = { nav.navigate(Routes.FUEL) },
                    onStartShift = { nav.navigateTab(Routes.SHIFT) },
                    onSeeAll = { nav.navigateTab(Routes.TRANSACTIONS) },
                    onSummary = { nav.navigate(Routes.SUMMARY) },
                    onReport = { nav.navigate(Routes.REPORT) },
                    onCommunity = { nav.navigate(Routes.COMMUNITY) },
                    onTarget = { nav.navigate(Routes.TARGET) },
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
                    onAddFuel = { nav.navigate(Routes.FUEL) },
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
            composable(Routes.ADD_ORDER) {
                val trips by ledgerVm.tripsToday.collectAsState()
                AddOrderScreen(
                    tripsToday = trips,
                    onBack = { nav.popBackStack() },
                    onSave = { platform, gross, pct, route, method, note ->
                        ledgerVm.addOrder(platform, gross, pct, route, method, note)
                        nav.popBackStack()
                    }
                )
            }
            composable(Routes.FUEL) {
                FuelScreen(
                    onBack = { nav.popBackStack() },
                    onSave = { label, liters, amount, method, station ->
                        ledgerVm.addFuel(label, liters, amount, method, station)
                    },
                    onDone = { nav.popBackStack() }
                )
            }
            composable(Routes.SUMMARY) {
                SummaryScreen(
                    onBack = { nav.popBackStack() },
                    onSeeTransactions = { nav.navigateTab(Routes.TRANSACTIONS) }
                )
            }
            composable(Routes.REPORT) {
                ReportScreen(onBack = { nav.popBackStack() })
            }
            composable(Routes.TARGET) {
                TargetScreen(vm = planVm, onBack = { nav.popBackStack() })
            }
            composable(Routes.COMMUNITY) {
                CommunityScreen(
                    vm = communityVm,
                    onBack = { nav.popBackStack() },
                    onOpenChat = { nav.navigate(Routes.COMMUNITY_CHAT) }
                )
            }
            composable(Routes.COMMUNITY_CHAT) {
                CommunityChatScreen(vm = communityVm, onBack = { nav.popBackStack() })
            }
        }
    }
}

private const val SPLASH_FADE_MS = 450
private const val SCREEN_MS = 300
private const val TAB_MS = 200

/** Perpindahan antar tab utama memakai fade saja; layar detail bergeser halus dari kanan. */
private fun AnimatedContentTransitionScope<NavBackStackEntry>.isTabSwitch(): Boolean =
    initialState.destination.route in Routes.mainTabs && targetState.destination.route in Routes.mainTabs

private fun AnimatedContentTransitionScope<NavBackStackEntry>.screenEnter(): EnterTransition = if (isTabSwitch()) {
    fadeIn(tween(TAB_MS))
} else {
    slideInHorizontally(tween(SCREEN_MS)) { it / 6 } + fadeIn(tween(SCREEN_MS))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.screenExit(): ExitTransition = if (isTabSwitch()) {
    fadeOut(tween(TAB_MS))
} else {
    slideOutHorizontally(tween(SCREEN_MS)) { -it / 8 } + fadeOut(tween(SCREEN_MS))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.screenPopEnter(): EnterTransition = if (isTabSwitch()) {
    fadeIn(tween(TAB_MS))
} else {
    slideInHorizontally(tween(SCREEN_MS)) { -it / 8 } + fadeIn(tween(SCREEN_MS))
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.screenPopExit(): ExitTransition = if (isTabSwitch()) {
    fadeOut(tween(TAB_MS))
} else {
    slideOutHorizontally(tween(SCREEN_MS)) { it / 6 } + fadeOut(tween(SCREEN_MS))
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
