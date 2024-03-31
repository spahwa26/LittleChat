package com.app.littlechat.ui.commoncomposables

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.app.littlechat.R

@Composable
fun CustomToolbar(
    navController: NavController = rememberNavController(),
    title: String,
    onBackPress: () -> Boolean? = {
        navController.navigateUp()
    },
    onHomePress: () -> Boolean
) {
    val backInteractionSource = remember { MutableInteractionSource() }
    val homeInteractionSource = remember { MutableInteractionSource() }
    MaterialTheme {
        Box(Modifier.padding(bottom = 10.dp)) {
            Card(
                shape = RoundedCornerShape(0.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Image(
                        modifier = Modifier
                            .clickable(
                                indication = rememberRipple(),
                                interactionSource = backInteractionSource
                            ) {
                                onBackPress.invoke()
                            }
                            .size(50.dp)
                            .padding(10.dp),
                        painter = painterResource(id = R.drawable.back),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        contentDescription = stringResource(
                            id = R.string.back_icon
                        )
                    )
                    Text(text = title, fontSize = 20.sp, color = MaterialTheme.colorScheme.primary)

                    Image(
                        modifier = Modifier
                            .clickable(
                                indication = rememberRipple(),
                                interactionSource = homeInteractionSource
                            ) {
                                onHomePress.invoke()
                            }
                            .size(50.dp)
                            .padding(10.dp),
                        painter = painterResource(id = R.drawable.ic_home),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary),
                        contentDescription = stringResource(
                            id = R.string.back_icon
                        )
                    )
                }
            }
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
                Text(stringResource(id = R.string.ok))
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


@Preview(showBackground = true)
@Composable
private fun ToolbarPreview() {
    val context = LocalContext.current
    CustomToolbar(title = "Title") {
        Toast.makeText(context, "Home Clicked!", Toast.LENGTH_SHORT).show()
        false
    }
}