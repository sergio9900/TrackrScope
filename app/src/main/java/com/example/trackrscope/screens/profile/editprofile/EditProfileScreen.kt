package com.example.trackrscope.screens.profile.editprofile

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.example.trackrscope.utils.FormInputField
import com.example.trackrscope.utils.FormInputPassword

@Composable
fun EditProfileScreen(navController: NavController, viewModel: EditProfileViewModel = viewModel()) {

    val context = LocalContext.current

    val focusManager = LocalFocusManager.current

    LaunchedEffect(Unit) {
        viewModel.loadUserData { e ->
            Toast.makeText(context, e, Toast.LENGTH_SHORT).show()
        }
    }

    val firstFocusRequest = remember { FocusRequester() }
    val secondFocusRequest = remember { FocusRequester() }
    val thirdFocusRequest = remember { FocusRequester() }

    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }

    // Lanzador de selección de imagen.
    val launcher =
        rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedImageUri = result.data?.data
            }
        }


    //<editor-fold desc="Edit Profile">
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                focusManager.clearFocus()
            }
    ) {
        /* Contenedor principal */
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp),
        ) {
            /* Contenedor del formulario */
            Column(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                /* Contenedor de la foto de perfil */
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.Center
                )
                {
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = stringResource(id = R.string.placeholder),
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )

                    } else if (viewModel.profileImageUri.value.isNotEmpty()) {
                        AsyncImage(
                            model = viewModel.profileImageUri.value,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                        )

                    } else {
                        Image(
                            painter = painterResource(R.drawable.profile_img_white),
                            contentDescription = stringResource(id = R.string.placeholder),
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_PICK).apply {
                                type = "image/*"
                            }
                            launcher.launch(intent)
                        },
                        modifier = Modifier.align(Alignment.CenterVertically)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountBox,
                            contentDescription = null,
                        )
                    }
                }

                /* Contenedor de los campos de texto */
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    /* Campo username */
                    FormInputField(
                        label = stringResource(id = R.string.username),
                        value = viewModel.username.value,
                        onValueChange = { viewModel.onUsernameChanged(it) },
                        imeAction = ImeAction.Next,
                        focusRequester = firstFocusRequest,
                        onImeAction = { secondFocusRequest.requestFocus() }
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    if (viewModel.isEmailPasswordUser.value) {
                        /* Campo password */
                        FormInputPassword(
                            label = stringResource(id = R.string.password),
                            value = viewModel.newPassword.value,
                            onValueChange = { viewModel.onNewPasswordChanged(it) },
                            imeAction = ImeAction.Next,
                            focusRequester = secondFocusRequest,
                            onImeAction = { thirdFocusRequest.requestFocus() }
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        /* Campo confirm password */
                        FormInputPassword(
                            label = stringResource(id = R.string.confirm_password),
                            value = viewModel.confirmNewPassword.value,
                            onValueChange = { viewModel.onConfirmNewPasswordChanged(it) },
                            imeAction = ImeAction.Done,
                            focusRequester = thirdFocusRequest
                        )
                    }
                }

                Spacer(modifier = Modifier.height(30.dp))
                /* Boton de registro y posible boton de volver a login */
                Column(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    /* Botón de guardar */
                    Button(
                        onClick = {
                            viewModel.updateUserData(selectedImageUri, context, navController)
                        },
                        modifier = Modifier
                            .width(250.dp)
                            .padding(horizontal = 15.dp),
                        enabled = viewModel.isSaveEnabled.value,
                    ) {
                        Text(text = stringResource(id = R.string.save))
                    }

                    Button(
                        onClick = {
                            navController.navigate(Routes.PROFILE)
                        }, modifier = Modifier
                            .width(250.dp)
                            .padding(15.dp),
                        enabled = true,
                        colors = ButtonDefaults.buttonColors(Color.LightGray)
                    ) {
                        Text(
                            text = stringResource(id = R.string.cancel),
                        )
                    }
                }
            }
        }
    }
    // </editor-fold>
}