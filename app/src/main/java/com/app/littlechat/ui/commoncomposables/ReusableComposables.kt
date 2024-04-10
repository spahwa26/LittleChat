package com.app.littlechat.ui.commoncomposables

import android.Manifest
import android.content.Context
import android.net.Uri
import android.os.Build
import android.support.annotation.RawRes
import android.widget.Toast
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import androidx.core.content.PermissionChecker
import coil.compose.AsyncImage
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.app.littlechat.R
import com.app.littlechat.utility.Constants
import com.app.littlechat.utility.getColors
import com.app.littlechat.utility.gotoApplicationSettings

@Composable
fun CustomToolbar(
    title: String,
    onBackPress: (() -> Boolean?)? = null,
    onRightBtnTap: (() -> Unit)? = null,
    rightButtonIcon: Int = R.drawable.ic_home,
    content: (@Composable ColumnScope.() -> Unit)? = null,
    mutableState: MutableState<Boolean>? = null
) {
    val backInteractionSource = remember { MutableInteractionSource() }
    val homeInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp)
            .background(color = MaterialTheme.colorScheme.primary)
            .clipToBounds()
    ) {
        onBackPress?.let {
            Image(
                modifier = Modifier
                    .clickable(
                        indication = rememberRipple(),
                        interactionSource = backInteractionSource
                    ) {
                        it.invoke()
                    }
                    .size(50.dp)
                    .padding(13.dp)
                    .align(Alignment.CenterStart),
                painter = painterResource(id = R.drawable.back),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                contentDescription = stringResource(
                    id = R.string.back_icon
                )
            )
        }
        Text(
            modifier = Modifier.align(Alignment.Center),
            text = title,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.inversePrimary
        )
        onRightBtnTap?.let {
            Box (modifier = Modifier.align(Alignment.CenterEnd)){
                Image(
                    modifier = Modifier
                        .clickable(
                            indication = rememberRipple(),
                            interactionSource = homeInteractionSource
                        ) {
                            it.invoke()
                        }
                        .size(50.dp)
                        .padding(10.dp),
                    painter = painterResource(rightButtonIcon),
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                    contentDescription = stringResource(
                        id = R.string.back_icon
                    )
                )
                if (content != null && mutableState != null) {
                    DropdownMenu(
                        modifier = Modifier.background(getColors().inversePrimary),
                        expanded = mutableState.value,
                        offset = DpOffset(x = (-3).dp, y=(-2).dp),
                        onDismissRequest = { mutableState.value = false }
                    ) {
                        content(this)
                    }
                }
            }
        }
    }
}

@Composable
fun ProgressDialog(state: MutableState<Boolean>) {
    if (state.value) {
        Dialog(
            onDismissRequest = {},
            DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(100.dp)
                    .background(getColors().inversePrimary, shape = RoundedCornerShape(8.dp))
            ) {
                CircularProgressIndicator()
            }
        }
    }
}


@Composable
fun LottieAnimationOnboarding(modifier: Modifier = Modifier, @RawRes anim: Int) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(anim))
    val progress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    LottieAnimation(
        modifier = modifier,
        composition = composition,
        progress = { progress },
        maintainOriginalImageBounds = true,
        contentScale = ContentScale.FillBounds,
    )
}


@Composable
fun CommonAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    onDismissClick: () -> Unit = onDismissRequest,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector? = null,
    resetEmailTxt : MutableState<String>?=null,
    confirmText: String = stringResource(id = R.string.ok),
    dismissText: String? = stringResource(id = R.string.cancel),
) {
    AlertDialog(
        icon = {
            icon?.let {
                Icon(it, contentDescription = "Example Icon")
            }
        },
        title = {
            Text(text = dialogTitle, fontSize = 16.sp)
        },
        text = {
            Column {
                if(resetEmailTxt!=null)
                {
                    EmailField(emailString = resetEmailTxt)
                }
                Text(text = dialogText, fontSize = 14.sp)
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            dismissText?.let {
                TextButton(
                    onClick = {
                        onDismissClick.invoke()
                    }
                ) {
                    Text(it)
                }
            }
        }
    )
}


@Composable
fun EmailField(modifier: Modifier = Modifier, emailString: MutableState<String>) {
    TextField(
        modifier = modifier,
        value = emailString.value,
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = ImeAction.Next
        ),
        label = { Text(text = stringResource(id = R.string.email)) },
        onValueChange = {
            emailString.value = it
        }
    )
}


@Composable
fun ChatText(msg: String, modifier: Modifier, color: Color) {
    Text(
        modifier = modifier
            .background(
                color = color,
                shape = RoundedCornerShape(10.dp)
            )
            .padding(5.dp),
        textAlign = TextAlign.Start,
        text = msg,
        color = MaterialTheme.colorScheme.onPrimary
    )
}

@Composable
fun ProfileImage(modifier: Modifier, imageUrl: Any, name: String, onSuccess:(()->Unit)?=null) {
    AsyncImage(
        modifier = modifier
            .aspectRatio(1 / 1f)
            .clip(CircleShape)
            .border(
                2.dp,
                MaterialTheme.colorScheme.primary,
                CircleShape
            ),
        model = imageUrl,
        contentDescription = name,
        contentScale = ContentScale.Crop,
        placeholder = painterResource(id = R.drawable.ic_person),
        onSuccess = {
            onSuccess?.invoke()
        },

    )
}


@Preview(showBackground = true)
@Composable
private fun ToolbarPreview() {
    val context = LocalContext.current
    CustomToolbar(title = "Title", onRightBtnTap = {
        Toast.makeText(context, "Home Clicked!", Toast.LENGTH_SHORT).show()
    })
}

@Composable
fun ToggleCard(
    modifier: Modifier = Modifier,
    text: String,
    toggle: Boolean,
    onChange: (Boolean) -> Unit
) {
    Box(modifier = modifier.padding(10.dp)) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 75.dp)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = text)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = text, fontSize = 20.sp)
                }
                Switch(checked = toggle, onCheckedChange = {
                    onChange.invoke(it)
                })
            }
        }
    }
}


@Composable
fun SettingsTextOption(
    modifier: Modifier = Modifier,
    text: String,
    onClick: () -> Unit
) {
    Box(modifier = modifier.padding(10.dp)) {
        Card(
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    onClick.invoke()
                },
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 75.dp)
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Settings, contentDescription = text)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(text = text, fontSize = 20.sp)
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = text
                )
            }
        }
    }
}

@Composable
fun NoDataView(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.cloud_download),
            contentDescription = stringResource(
                id = R.string.no_requests
            ),
            modifier = Modifier.size(100.dp),
            colorFilter = ColorFilter.tint(getColors().primary)
        )
        Text(
            text = text,
            fontSize = 22.sp,
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                shadow = Shadow(offset = Offset(1f, 1f), blurRadius = 3.5f),
                color = getColors().primary
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 30.dp)
        )
    }
}

@Composable
fun PermissionComposable(state: MutableState<Boolean>, uriCallback: (uri: Uri) -> Unit) {
    val context = LocalContext.current

    val showPermissionSettingsAlert = remember { mutableStateOf(false) }


    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uriCallback.invoke(uri)
        }
    }


    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { pRes ->
        val res = pRes.values.find { !it }
        if (res == null) {
            launcher.launch(Constants.IMAGE_MIME)
        } else {
            showPermissionSettingsAlert.value = true
        }
    }

    if (state.value) {
        checkAndRequestPermission(
            context,
            launcher,
            galleryPermissionLauncher
        )
        state.value = false
    }

    if (showPermissionSettingsAlert.value)
        CommonAlertDialog(
            onDismissRequest = {},
            onDismissClick = {
                showPermissionSettingsAlert.value = false
            },
            onConfirmation = {
                showPermissionSettingsAlert.value = false
                context.gotoApplicationSettings()
            },
            dialogTitle = stringResource(id = R.string.require_Storage_permission),
            dialogText = stringResource(id = R.string.allow_permission_msg),
            confirmText = stringResource(id = R.string.yes),
            dismissText = stringResource(id = R.string.cancel)
        )
}

fun checkAndRequestPermission(
    context: Context,
    galleryLauncher: ManagedActivityResultLauncher<String, Uri?>,
    permissionLauncher: ManagedActivityResultLauncher<Array<String>, Map<String, Boolean>>,
) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
        // Partial access on Android 14 (API level 34) or higher
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            ) == PermissionChecker.PERMISSION_GRANTED
        )
            galleryLauncher.launch(Constants.IMAGE_MIME)
        else permissionLauncher.launch(
            arrayOf(
                Manifest.permission.READ_MEDIA_IMAGES,
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
            )
        )
    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        // Full access on Android 13 (API level 33) or higher
        if ((ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PermissionChecker.PERMISSION_GRANTED)
        )
            galleryLauncher.launch(Constants.IMAGE_MIME)
        else permissionLauncher.launch(arrayOf(Manifest.permission.READ_MEDIA_IMAGES))
    } else if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PermissionChecker.PERMISSION_GRANTED
    ) {
        galleryLauncher.launch(Constants.IMAGE_MIME)
    } else {
        permissionLauncher.launch(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE))
    }
}

//@Composable
//fun <T> NavController.GetOnceResult(keyResult: String, onResult: (T) -> Unit){
//    val valueScreenResult =  currentBackStackEntry
//        ?.savedStateHandle
//        ?.get<T>(keyResult)
//
//    valueScreenResult?.let {
//        currentBackStackEntry
//            ?.savedStateHandle
//            ?.remove<T>(keyResult)
//
//        LaunchedEffect(it) {
//            delay(1000)
//            onResult.invoke(it)
//            Log.d("getGroups: ", "GetOnceResult")
//        }
//    }
//}

