package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.database.NexoraDatabase
import com.example.data.repository.AuthRepository
import com.example.ui.screens.AccountNameScreen
import com.example.ui.screens.AccountSuccessScreen
import com.example.ui.screens.EmailAuthScreen
import com.example.ui.screens.GoogleAuthScreen
import com.example.ui.screens.HandleSelectionScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MainScreen
import com.example.ui.screens.NexoraPasswordScreen
import com.example.ui.screens.PhoneAuthScreen
import com.example.ui.screens.PolicyScreen
import com.example.ui.screens.ProfilePrivacyScreen
import com.example.ui.screens.ProfileSetupScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VerificationCodeScreen
import com.example.ui.screens.WelcomeScreen
import com.example.ui.theme.NexoraTheme
import com.example.ui.update.AdminUpdateDialog
import com.example.ui.update.UpdateScreen
import com.example.ui.update.UpdateViewModel
import com.example.ui.update.UpdateViewModelFactory
import com.example.ui.viewmodel.AuthViewModel
import com.example.ui.viewmodel.AuthViewModelFactory
import com.example.update.model.RemoteVersionConfig
import com.example.update.model.UpdateCheckResult
import com.example.update.model.UpdateUrgency
import com.example.update.repository.UpdateRepository
import com.example.update.service.UpdateServiceFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        com.example.ui.theme.NexoraThemeManager.initialize(applicationContext)

        val database = NexoraDatabase.getInstance(applicationContext)
        val repository = AuthRepository(database)
        val viewModelFactory = AuthViewModelFactory(repository)

        setContent {
            NexoraTheme {
                val viewModel: AuthViewModel = viewModel(factory = viewModelFactory)
                val uiState by viewModel.uiState.collectAsStateWithLifecycle()
                val navController = rememberNavController()
                val snackbarHostState = remember { SnackbarHostState() }

                // Update Architecture Integration
                val updateRepository = remember { UpdateRepository(applicationContext) }
                val updateService = remember { UpdateServiceFactory.create(applicationContext, updateRepository) }
                val updateViewModelFactory = remember { UpdateViewModelFactory(updateRepository, updateService) }
                val updateViewModel: UpdateViewModel = viewModel(factory = updateViewModelFactory)

                val updateCheckResult by updateViewModel.updateCheckResult.collectAsStateWithLifecycle()
                val downloadState by updateViewModel.downloadState.collectAsStateWithLifecycle()
                val activeConfig by updateViewModel.activeConfig.collectAsStateWithLifecycle()
                val isMandatory by updateViewModel.isMandatory.collectAsStateWithLifecycle()
                val currentScenario by updateViewModel.currentScenario.collectAsStateWithLifecycle()
                val isAdminAuthorized by updateViewModel.isAdminAuthorized.collectAsStateWithLifecycle()

                var showAdminDialog by remember { mutableStateOf(false) }

                // Mandatory Update Interceptor: locks user out if a mandatory update is active
                LaunchedEffect(updateCheckResult) {
                    val result = updateCheckResult
                    if (result is UpdateCheckResult.UpdateAvailable && result.urgency == UpdateUrgency.MANDATORY) {
                        val currentRoute = navController.currentBackStackEntry?.destination?.route
                        if (currentRoute != "update_screen" && currentRoute != "splash") {
                            navController.navigate("update_screen")
                        }
                    }
                }

                LaunchedEffect(uiState.errorMessage) {
                    uiState.errorMessage?.let { errorMsg ->
                        snackbarHostState.showSnackbar(errorMsg)
                        viewModel.clearError()
                    }
                }

                LaunchedEffect(uiState.successMessage) {
                    uiState.successMessage?.let { successMsg ->
                        snackbarHostState.showSnackbar(successMsg)
                        viewModel.clearSuccess()
                    }
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = "splash",
                        modifier = Modifier.padding(innerPadding),
                        enterTransition = { fadeIn(animationSpec = tween(300)) },
                        exitTransition = { fadeOut(animationSpec = tween(300)) }
                    ) {
                        // 1. Splash Screen with Startup Version Check
                        composable("splash") {
                            LaunchedEffect(Unit) {
                                updateViewModel.checkForUpdates()
                            }

                            SplashScreen(
                                onSplashFinished = {
                                    val result = updateCheckResult
                                    if (result is UpdateCheckResult.UpdateAvailable && result.urgency == UpdateUrgency.MANDATORY) {
                                        navController.navigate("update_screen") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    } else if (uiState.activeUser != null) {
                                        navController.navigate("main") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    } else {
                                        navController.navigate("welcome") {
                                            popUpTo("splash") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        // 2. Welcome Screen
                        composable("welcome") {
                            WelcomeScreen(
                                onContinueGoogle = { navController.navigate("auth_google") },
                                onContinueEmail = { navController.navigate("auth_email") },
                                onContinuePhone = { navController.navigate("auth_phone") },
                                onNavigateLogin = { navController.navigate("login") }
                            )
                        }

                        // 3. Login Screen
                        composable("login") {
                            LoginScreen(
                                isLoading = uiState.isLoading,
                                errorMessage = uiState.errorMessage,
                                onBack = { navController.popBackStack() },
                                onNavigateSignUp = {
                                    navController.navigate("welcome") {
                                        popUpTo("login") { inclusive = true }
                                    }
                                },
                                onAttemptLogin = { provider, identifier, password ->
                                    viewModel.startLogin(
                                        provider = provider,
                                        identifier = identifier,
                                        passwordInput = password,
                                        onRequires2FA = {
                                            navController.navigate("verify_code")
                                        },
                                        onDirectSuccess = {
                                            navController.navigate("main") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        }
                                    )
                                }
                            )
                        }

                        // 4. Google Auth Flow
                        composable("auth_google") {
                            GoogleAuthScreen(
                                onBack = { navController.popBackStack() },
                                onAccountSelected = { name, email ->
                                    viewModel.startGoogleAuth(name, email) {
                                        navController.navigate("setup_policy")
                                    }
                                }
                            )
                        }

                        // 5. Email Auth Flow
                        composable("auth_email") {
                            EmailAuthScreen(
                                isLoading = uiState.isLoading,
                                errorMessage = uiState.errorMessage,
                                onBack = { navController.popBackStack() },
                                onSubmitEmail = { email ->
                                    viewModel.startEmailRegistration(email) {
                                        navController.navigate("verify_code")
                                    }
                                }
                            )
                        }

                        // 6. Phone Auth Flow
                        composable("auth_phone") {
                            PhoneAuthScreen(
                                isLoading = uiState.isLoading,
                                errorMessage = uiState.errorMessage,
                                onBack = { navController.popBackStack() },
                                onSubmitPhone = { phone ->
                                    viewModel.startPhoneRegistration(phone) {
                                        navController.navigate("verify_code")
                                    }
                                }
                            )
                        }

                        // 7. 6-Digit OTP Verification Screen
                        composable("verify_code") {
                            VerificationCodeScreen(
                                targetIdentifier = uiState.verificationTarget,
                                verificationType = uiState.verificationType,
                                codePreview = uiState.plainVerificationCodePreview,
                                resendCountdown = uiState.resendCountdown,
                                attemptsRemaining = uiState.verificationAttemptsRemaining,
                                isLoading = uiState.isLoading,
                                errorMessage = uiState.errorMessage,
                                onBack = { navController.popBackStack() },
                                onVerifyCode = { code ->
                                    viewModel.verifyCode(code) {
                                        if (uiState.verificationType == "LOGIN_2FA") {
                                            navController.navigate("main") {
                                                popUpTo("welcome") { inclusive = true }
                                            }
                                        } else {
                                            navController.navigate("setup_policy")
                                        }
                                    }
                                },
                                onResendCode = { viewModel.resendVerificationCode() }
                            )
                        }

                        // 8. Progressive Account Policy Step (Step 1A Requirement)
                        composable("setup_policy") {
                            PolicyScreen(
                                onBack = { navController.popBackStack() },
                                onAgreeAndContinue = {
                                    viewModel.acceptPolicy {
                                        navController.navigate("setup_name")
                                    }
                                }
                            )
                        }

                        // 9. Account Name Setup Screen (Account Name = Main Personal Profile Name)
                        composable("setup_name") {
                            AccountNameScreen(
                                initialName = uiState.regAccountName,
                                isChecking = uiState.isNameChecking,
                                isAvailable = uiState.isNameAvailable,
                                validationMessage = uiState.nameValidationMessage,
                                errorMessage = uiState.errorMessage,
                                onBack = { navController.popBackStack() },
                                onNameChanged = { viewModel.checkNameAvailability(it) },
                                onSubmitName = { name ->
                                    viewModel.setAccountName(name) {
                                        navController.navigate("setup_password")
                                    }
                                }
                            )
                        }

                        // 10. NEXORA Password Setup Screen
                        composable("setup_password") {
                            NexoraPasswordScreen(
                                authProvider = uiState.regProvider,
                                errorMessage = uiState.errorMessage,
                                onBack = { navController.popBackStack() },
                                onSubmitPasswords = { password, confirm ->
                                    viewModel.setNexoraPassword(password, confirm) {
                                        navController.navigate("setup_handle")
                                    }
                                }
                            )
                        }

                        // 11. Handle / Username Selection Screen
                        composable("setup_handle") {
                            HandleSelectionScreen(
                                currentHandle = uiState.regHandle,
                                accountName = uiState.regAccountName,
                                isChecking = uiState.isHandleChecking,
                                isAvailable = uiState.isHandleAvailable,
                                validationMessage = uiState.handleValidationMessage,
                                suggestions = uiState.handleSuggestions,
                                onBack = { navController.popBackStack() },
                                onHandleChanged = { viewModel.checkHandleAvailability(it) },
                                onSelectSuggestion = { viewModel.selectSuggestedHandle(it) },
                                onConfirmHandle = {
                                    viewModel.confirmHandle {
                                        navController.navigate("setup_privacy")
                                    }
                                }
                            )
                        }

                        // 12. Profile Privacy Screen (PUBLIC vs PRIVATE PROFILE)
                        composable("setup_privacy") {
                            ProfilePrivacyScreen(
                                initialIsPrivate = uiState.regIsPrivateProfile,
                                onBack = { navController.popBackStack() },
                                onSelectPrivacy = { isPrivate ->
                                    viewModel.setRegistrationProfilePrivacy(isPrivate) {
                                        navController.navigate("setup_profile")
                                    }
                                }
                            )
                        }

                        // 13. Profile Setup Screen (Avatar & Bio & Privacy Badge)
                        composable("setup_profile") {
                            ProfileSetupScreen(
                                accountName = uiState.regAccountName,
                                handle = uiState.regHandle,
                                isPrivateProfile = uiState.regIsPrivateProfile,
                                isLoading = uiState.isLoading,
                                errorMessage = uiState.errorMessage,
                                onBack = { navController.popBackStack() },
                                onCompleteProfile = { bio, avatarIndex ->
                                    viewModel.finalizeAccountCreation(bio, avatarIndex) {
                                        navController.navigate("account_success") {
                                            popUpTo("welcome") { inclusive = true }
                                        }
                                    }
                                }
                            )
                        }

                        // 14. Account Success Celebration Screen
                        composable("account_success") {
                            AccountSuccessScreen(
                                user = uiState.activeUser,
                                onEnterHome = {
                                    navController.navigate("main") {
                                        popUpTo("account_success") { inclusive = true }
                                    }
                                }
                            )
                        }

                        // 15. Main NEXORA Shell (Step 1A: Home, Shorts, +, Subscriptions, You)
                        composable("main") {
                            val user = uiState.activeUser
                            if (user != null) {
                                MainScreen(
                                    activeUser = user,
                                    currentTab = uiState.currentBottomTab,
                                    onTabSelected = { viewModel.switchBottomTab(it) },
                                    onCreateChannel = { name, handle, bio ->
                                        viewModel.createUserChannel(name, handle, bio) {}
                                    },
                                    onTogglePrivacy = { isPrivate ->
                                        viewModel.toggleUserPrivacy(isPrivate)
                                    },
                                    onUpdateProfile = { name, bio, avatar ->
                                        viewModel.updateUserProfile(name, bio, avatar) {}
                                    },
                                    onLogout = {
                                        viewModel.logout {
                                            navController.navigate("welcome") {
                                                popUpTo("main") { inclusive = true }
                                            }
                                        }
                                    },
                                    installedVersionName = updateViewModel.getInstalledVersionName(),
                                    installedVersionCode = updateViewModel.getInstalledVersionCode(),
                                    updateCheckResult = updateCheckResult,
                                    onCheckForUpdates = {
                                        updateViewModel.checkForUpdates { result ->
                                            when (result) {
                                                is UpdateCheckResult.UpdateAvailable -> {
                                                    navController.navigate("update_screen")
                                                }
                                                is UpdateCheckResult.UpToDate -> {
                                                    // Already handled or snackbar
                                                }
                                                is UpdateCheckResult.Error -> {
                                                    // Error handled in state
                                                }
                                            }
                                        }
                                    },
                                    onOpenUpdateScreen = {
                                        navController.navigate("update_screen")
                                    },
                                    onOpenAdminDialog = {
                                        showAdminDialog = true
                                    }
                                )
                            } else {
                                LaunchedEffect(Unit) {
                                    navController.navigate("welcome") {
                                        popUpTo("main") { inclusive = true }
                                    }
                                }
                            }
                        }

                        // 16. NEXORA Update Screen (Mandatory & Optional Update Flow)
                        composable("update_screen") {
                            val config = activeConfig ?: RemoteVersionConfig.nextRelease(isMandatory = isMandatory)
                            UpdateScreen(
                                config = config,
                                isMandatory = isMandatory,
                                installedVersionName = updateViewModel.getInstalledVersionName(),
                                installedVersionCode = updateViewModel.getInstalledVersionCode(),
                                downloadState = downloadState,
                                onStartDownload = {
                                    updateViewModel.startDownload(config)
                                },
                                onCancelDownload = {
                                    updateViewModel.cancelDownload()
                                },
                                onResetDownload = {
                                    updateViewModel.resetDownloadState()
                                },
                                onInstallApk = { activity ->
                                    val result = updateViewModel.installUpdate(activity)
                                    if (result.isFailure) {
                                        // Error reported to user
                                    }
                                },
                                onDismissOptional = {
                                    if (!isMandatory) {
                                        navController.popBackStack()
                                    }
                                }
                            )
                        }

                        // Alias route for backwards compatibility
                        composable("home") {
                            LaunchedEffect(Unit) {
                                navController.navigate("main") {
                                    popUpTo("home") { inclusive = true }
                                }
                            }
                        }
                    }

                    // Admin & Tester Simulation Dialog
                    if (showAdminDialog) {
                        AdminUpdateDialog(
                            isAuthorized = isAdminAuthorized,
                            currentScenario = currentScenario,
                            installedVersionName = updateViewModel.getInstalledVersionName(),
                            installedVersionCode = updateViewModel.getInstalledVersionCode(),
                            onAuthorize = { passcode ->
                                updateViewModel.authorizeAdmin(passcode)
                            },
                            onSelectScenario = { scenario ->
                                updateViewModel.setAdminScenario(scenario)
                            },
                            onDismiss = { showAdminDialog = false }
                        )
                    }
                }
            }
        }
    }
}
