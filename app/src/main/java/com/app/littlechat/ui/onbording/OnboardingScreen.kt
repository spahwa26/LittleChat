package com.app.littlechat.ui.onbording

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.app.littlechat.R
import com.app.littlechat.ui.commoncomposables.LottieAnimationOnboarding
import com.app.littlechat.ui.commoncomposables.CommonAlertDialog
import com.app.littlechat.utility.getColors


@Composable
fun OnboardingScreen(onLoginClick: () -> Unit, onSignupClick: () -> Unit) {
    ParentView(onLoginClick, onSignupClick)
}


@Composable
fun ParentView(onLoginClick: () -> Unit = {}, onSignupClick: () -> Unit = {}) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LottieAnimationOnboarding(modifier = Modifier.size(width = 300.dp, height = 350.dp), anim = R.raw.robot_hello)



        BottomView(onLoginClick, onSignupClick)
    }
}

@Composable
fun BottomView(onLoginClick: () -> Unit = {}, onSignupClick: () -> Unit = {}) {
    val context = LocalContext.current
    val checkedState = remember {
        mutableStateOf(false)
    }
    val showAlert = remember { mutableStateOf(false) }
    TermsAlert(showAlert)
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .padding(bottom = 30.dp)
            .padding(horizontal = 40.dp)
    ) {

        Image(
            painter = painterResource(R.drawable.ic_chat),
            contentDescription = null,
            modifier = Modifier.padding(bottom = 10.dp),
            colorFilter = ColorFilter.tint(getColors().primary)
        )

        Text(
            modifier = Modifier.padding(bottom = 40.dp),
            text = stringResource(id = R.string.welcome_to),
            fontSize = 16.sp
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 40.dp)
        ) {
            Button(
                modifier = Modifier
                    .padding(end = 5.dp)
                    .fillMaxWidth(0.5f),
                onClick = {
                    if (checkedState.value)
                        onLoginClick.invoke()
                    else showAlert.value = true
                },
            ) {
                Text(text = stringResource(id = R.string.login))
            }
            Button(
                modifier = Modifier.fillMaxWidth(),
                onClick = {
                    if (checkedState.value)
                        onSignupClick.invoke()
                    else showAlert.value = true
                }
            ) {
                Text(text = stringResource(id = R.string.signup))
            }
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(
                modifier = Modifier
                    .size(40.dp),
                checked = checkedState.value,
                onCheckedChange = {
                    checkedState.value = it
                }
            )
            Text(text = stringResource(id = R.string.I_agree))
            Text(
                modifier = Modifier
                    .padding(start = 2.dp)
                    .clickable {
                        val url = "http://www.google.com"
                        val i = Intent(Intent.ACTION_VIEW)
                        i.data = Uri.parse(url)
                        context.startActivity(i)
                    },
                text = stringResource(id = R.string.terms_and_conditions),
                color = colorResource(id = R.color.colorPrimary),
                style = TextStyle(textDecoration = TextDecoration.Underline),
            )
        }
    }
}

@Composable
fun TermsAlert(showAlert: MutableState<Boolean>) {
    if (showAlert.value)
        CommonAlertDialog(
            onDismissRequest = {
                showAlert.value = false
            },
            onConfirmation = { showAlert.value = false },
            dialogTitle = stringResource(id = R.string.alert),
            dialogText = stringResource(id = R.string.please_indicate),
            dismissText = null
        )
}


@Preview(showBackground = true)
@Composable
private fun ParentViewPrev() {
    ParentView()
}