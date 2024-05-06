package com.app.littlechat.ui.onbording

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.littlechat.R
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.data.network.OnboardingRepository
import com.app.littlechat.ui.commoncomposables.LottieAnimationOnboarding
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.ui.commoncomposables.EmailField
import com.app.littlechat.utility.showToast

@Composable
fun SignupScreen(
    onBackPress: () -> Boolean?,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val emailString = remember { mutableStateOf("") }
    val passwordString = remember { mutableStateOf("") }
    val confirmPasswordString = remember { mutableStateOf("") }
    val state = viewModel.uiState.value
    if (state is OnboardingViewModel.OnboardingState.Error) {
        context.showToast(txt = state.e)
    }
    if (state is OnboardingViewModel.OnboardingState.LocalError) {
        context.showToast(state.e)
    }
    EmailAlert(viewModel, onBackPress)
    Column {
        CustomToolbar(
            title = stringResource(id = R.string.signup),
            onBackPress = onBackPress
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.verticalScroll(
                rememberScrollState()
            )
        ) {
            LottieAnimationOnboarding(
                modifier = Modifier
                    .padding(vertical = 30.dp)
                    .size(200.dp),
                anim = R.raw.login_simple
            )
            EmailField(modifier = Modifier.padding(bottom = 15.dp), emailString)
            PasswordField(
                modifier = Modifier.padding(bottom = 15.dp),
                passwordString,
                action = ImeAction.Next
            )
            PasswordField(
                modifier = Modifier.padding(bottom = 50.dp),
                confirmPasswordString,
                R.string.confirm_password,
                onDone = {
                    viewModel.signupUser(
                        emailString.value,
                        passwordString.value,
                        confirmPasswordString.value
                    )
                }
            )
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                if (state is OnboardingViewModel.OnboardingState.Loading)
                    CircularProgressIndicator()
                else
                    Button(
                        onClick = {
                            viewModel.signupUser(
                                emailString.value,
                                passwordString.value,
                                confirmPasswordString.value
                            )
                        }) {
                        Text(text = stringResource(id = R.string.signup))
                    }
            }
            OrOptionSeparator()
            SocialLoginIcons(context)
        }
    }

}


@Preview
@Composable
fun SignupScreenPreview() {
    SignupScreen(
        onBackPress = { false }, OnboardingViewModel(
            OnboardingRepository(UserPreferences(LocalContext.current))
        )
    )
}