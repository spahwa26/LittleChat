package com.app.littlechat.ui.onbording

import android.content.Context
import android.content.Intent
import android.support.annotation.StringRes
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.littlechat.R
import com.app.littlechat.data.network.OnboardingRepository
import com.app.littlechat.ui.commoncomposables.LottieAnimationOnboarding
import com.app.littlechat.ui.commoncomposables.CommonAlertDialog
import com.app.littlechat.ui.commoncomposables.CustomToolbar
import com.app.littlechat.data.UserPreferences
import com.app.littlechat.ui.commoncomposables.EmailField
import com.app.littlechat.ui.home.HomeActivity
import com.app.littlechat.utility.finishActivity
import com.app.littlechat.utility.showToast

@Composable
fun LoginScreen(
    onHomeClick: () -> Boolean,
    onBackPress: () -> Boolean?,
    viewModel: OnboardingViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val forgotIndication = remember { MutableInteractionSource() }
    val emailString = remember { mutableStateOf("") }
    val passwordString = remember { mutableStateOf("") }
    val state = viewModel.uiState.value
    if (state is OnboardingViewModel.OnboardingState.Error) {
        context.showToast(txt = state.e)
    }
    if (state is OnboardingViewModel.OnboardingState.LocalError) {
        context.showToast(state.e)
    }
    if (state is OnboardingViewModel.OnboardingState.Success) {
        context.startActivity(Intent(context, HomeActivity::class.java))
        context.finishActivity()
    }
    EmailAlert(viewModel)
    Column {
        CustomToolbar(
            title = stringResource(id = R.string.login),
            onBackPress = onBackPress
        )
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.animateContentSize()
        ) {
            LottieAnimationOnboarding(modifier = Modifier.padding(vertical = 30.dp).size(200.dp), anim = R.raw.login_simple)
            EmailField(modifier = Modifier.padding(bottom = 15.dp), emailString)
            PasswordField(
                modifier = Modifier.padding(bottom = 50.dp),
                passwordString,
                label = R.string.password
            )
            Box(
                contentAlignment = Alignment.Center, modifier = Modifier
                    .padding(bottom = 50.dp)
                    .height(45.dp)
            ) {
                if (state is OnboardingViewModel.OnboardingState.Loading)
                    CircularProgressIndicator()
                else
                    Button(onClick = {
                        viewModel.loginUser(emailString.value, passwordString.value)
                    }) {
                        Text(text = stringResource(id = R.string.login))
                    }
            }
            OrOptionSeparator()
            SocialLoginIcons(context)
            Box(
                modifier = Modifier
                    .height(30.dp)
                    .clickable(
                        indication = rememberRipple(),
                        interactionSource = forgotIndication
                    ) {

                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    color = Color.Gray,
                    text = stringResource(id = R.string.forgot_password),
                )
            }
        }
    }

}

@Composable
fun PasswordField(
    modifier: Modifier = Modifier,
    passwordString: MutableState<String>,
    @StringRes label: Int = R.string.password,
    action: ImeAction? = null
) {
    TextField(
        modifier = modifier,
        value = passwordString.value,
        singleLine = true,
        visualTransformation = PasswordVisualTransformation(),
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = action ?: ImeAction.Done
        ),
        label = { Text(text = stringResource(id = label)) },
        onValueChange = {
            passwordString.value = it
        }
    )
}

@Composable
fun OrOptionSeparator() {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(50.dp)
    ) {
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
        Text(
            modifier = Modifier.padding(horizontal = 5.dp),
            text = stringResource(id = R.string.or),
            color = Color.Gray
        )
        HorizontalDivider(modifier = Modifier.weight(1f), color = Color.Gray)
    }
}

@Composable
fun SocialLoginIcons(context: Context) {
    val googleInteractionSource = remember { MutableInteractionSource() }
    val facebookInteractionSource = remember { MutableInteractionSource() }
    Row {
        Image(
            modifier = Modifier
                .clickable(
                    indication = rememberRipple(),
                    interactionSource = facebookInteractionSource
                ) {
                    context.showToast(R.string.Implementation_in_progress)
                }
                .size(70.dp)
                .padding(4.dp),
            painter = painterResource(id = R.drawable.ic_google),
            contentDescription = stringResource(
                id = R.string.google_icon
            )
        )
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            modifier = Modifier
                .clickable(
                    indication = rememberRipple(),
                    interactionSource = googleInteractionSource
                ) {
                    context.showToast(R.string.Implementation_in_progress)
                }
                .size(70.dp),
            painter = painterResource(id = R.drawable.ic_facebook),
            contentDescription = stringResource(
                id = R.string.google_icon
            )
        )
    }
}

@Composable
fun EmailAlert(viewModel: OnboardingViewModel, onDismiss:()->Boolean?={false}) {
    val state = viewModel.uiState.value
    if (state is OnboardingViewModel.OnboardingState.EmailSent) {
        val emailString = stringResource(id = R.string.email_sent, state.email)
        CommonAlertDialog(
            onDismissRequest = {
                viewModel.setIdle()
                onDismiss.invoke()
            },
            onConfirmation = {
                viewModel.setIdle()
                onDismiss.invoke()
            },
            dialogTitle = stringResource(id = R.string.alert),
            dialogText = emailString,
            dismissText = null
        )
    }
}

@Preview
@Composable
fun LoginLayout() {
    LoginScreen(onHomeClick = { false }, onBackPress = { false }, OnboardingViewModel(
        OnboardingRepository(UserPreferences(LocalContext.current))
    )
    )
}