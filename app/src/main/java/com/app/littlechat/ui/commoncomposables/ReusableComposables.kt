package com.app.littlechat.ui.commoncomposables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.app.littlechat.R

@Composable
fun CustomToolbar(
    title: String,
    onBackPress: (() -> Boolean?)? = null,
    onHomePress: (() -> Boolean?)? = null
) {
    val backInteractionSource = remember { MutableInteractionSource() }
    val homeInteractionSource = remember { MutableInteractionSource() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = 50.dp)
            .background(color = MaterialTheme.colorScheme.primary)
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
                    .padding(10.dp)
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
        onHomePress?.let {
            Image(
                modifier = Modifier
                    .clickable(
                        indication = rememberRipple(),
                        interactionSource = homeInteractionSource
                    ) {
                        it.invoke()
                    }
                    .size(50.dp)
                    .padding(10.dp)
                    .align(Alignment.CenterEnd),
                painter = painterResource(id = R.drawable.ic_home),
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.inversePrimary),
                contentDescription = stringResource(
                    id = R.string.back_icon
                )
            )
        }
    }
}


@Composable
fun AppImage(modifier: Modifier = Modifier) {
    Image(
        painter = painterResource(R.drawable.app_icon),
        contentDescription = null,
        modifier = modifier
            .width(100.dp)
            .height(100.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommonAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector? = null,
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
            Text(text = dialogTitle)
        },
        text = {
            Text(text = dialogText)
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
                        onDismissRequest()
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
fun ProfileImage(modifier: Modifier, imageUrl: String, name: String) {
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
        contentScale = ContentScale.Crop
    )
}


@Preview(showBackground = true)
@Composable
private fun ToolbarPreview() {
    val context = LocalContext.current
    CustomToolbar(title = "Title") {
        Toast.makeText(context, "Home Clicked!", Toast.LENGTH_SHORT).show()
        false
    }
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