package com.example.trackrscope.screens.profile.editprofile.components

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.trackrscope.R
import com.example.trackrscope.navigation.Routes
import com.example.trackrscope.screens.profile.editprofile.EditProfileViewModel
import com.example.trackrscope.utils.FormInputField
import com.example.trackrscope.utils.FormInputPassword

object EditProfileComponents {

    /**
     * Componente para el contenido de la pantalla de edición de perfil.
     */
    @Composable
    fun EditProfileContent(
        viewModel: EditProfileViewModel,
        selectedImageUri: Uri?,
        onImageSelect: (Intent) -> Unit,
        onUriChange: (Uri?) -> Unit,
        focusRequests: Triple<FocusRequester, FocusRequester, FocusRequester>,
        navController: NavController
    ) {
        val context = LocalContext.current
        Column(
            modifier = Modifier.Companion.fillMaxWidth(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            ProfileImagePicker(
                selectedImageUri = selectedImageUri,
                imageUrl = viewModel.profileImageUri.value,
                onImageSelect = onImageSelect
            )
            Spacer(modifier = Modifier.Companion.height(10.dp))

            ProfileFields(
                viewModel = viewModel,
                focusRequests = focusRequests
            )

            Spacer(modifier = Modifier.Companion.height(30.dp))

            Buttons(
                isSaveEnabled = viewModel.isSaveEnabled.value,
                onSave = { viewModel.updateUserData(selectedImageUri, context, navController) },
                onCancel = { navController.navigate(Routes.PROFILE) }
            )
        }
    }


    /**
     * Componente para los campos de perfil.
     */
    @Composable
    fun ProfileFields(
        viewModel: EditProfileViewModel,
        focusRequests: Triple<FocusRequester, FocusRequester, FocusRequester>
    ) {
        Column(
            modifier = Modifier.Companion.fillMaxSize(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            FormInputField(
                label = stringResource(id = R.string.username),
                value = viewModel.username.value,
                onValueChange = { viewModel.onUsernameChanged(it) },
                imeAction = ImeAction.Companion.Next,
                focusRequester = focusRequests.first,
                onImeAction = { focusRequests.second.requestFocus() }
            )

            Spacer(modifier = Modifier.Companion.height(10.dp))

            if (viewModel.isEmailPasswordUser.value) {
                FormInputPassword(
                    label = stringResource(id = R.string.password),
                    value = viewModel.newPassword.value,
                    onValueChange = { viewModel.onNewPasswordChanged(it) },
                    imeAction = ImeAction.Companion.Next,
                    focusRequester = focusRequests.second,
                    onImeAction = { focusRequests.third.requestFocus() }
                )

                Spacer(modifier = Modifier.Companion.height(10.dp))

                FormInputPassword(
                    label = stringResource(id = R.string.confirm_password),
                    value = viewModel.confirmNewPassword.value,
                    onValueChange = { viewModel.onConfirmNewPasswordChanged(it) },
                    imeAction = ImeAction.Companion.Done,
                    focusRequester = focusRequests.third
                )
            }
        }
    }

    /**
     * Componente para los botones de guardar y cancelar.
     */
    @Composable
    private fun Buttons(
        isSaveEnabled: Boolean,
        onSave: () -> Unit,
        onCancel: () -> Unit
    ) {
        Column(
            modifier = Modifier.Companion.fillMaxSize(),
            horizontalAlignment = Alignment.Companion.CenterHorizontally
        ) {
            Button(
                onClick = onSave,
                modifier = Modifier.Companion
                    .width(250.dp)
                    .padding(horizontal = 15.dp),
                enabled = isSaveEnabled
            ) {
                Text(text = stringResource(R.string.save))
            }

            Button(
                onClick = onCancel,
                modifier = Modifier.Companion
                    .width(250.dp)
                    .padding(horizontal = 15.dp),
                colors = ButtonDefaults.buttonColors(Color.Companion.LightGray)
            ) {
                Text(text = stringResource(R.string.cancel))
            }
        }
    }

    /**
     * Componente para la imagen de perfil.
     */
    @Composable
    fun ProfileImagePicker(
        selectedImageUri: Uri?,
        imageUrl: String,
        onImageSelect: (Intent) -> Unit
    ) {
        Row(
            modifier = Modifier.Companion
                .fillMaxSize()
                .padding(20.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            val imageModifier = Modifier.Companion
                .size(100.dp)
                .clip(CircleShape)

            when {
                selectedImageUri != null -> {
                    AsyncImage(
                        model = selectedImageUri,
                        contentDescription = stringResource(id = R.string.placeholder),
                        contentScale = ContentScale.Companion.Crop,
                        modifier = imageModifier
                    )
                }

                imageUrl.isNotEmpty() -> {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = null,
                        contentScale = ContentScale.Companion.Crop,
                        modifier = imageModifier
                    )
                }

                else -> {
                    Image(
                        painter = painterResource(R.drawable.profile_img_white),
                        contentDescription = stringResource(id = R.string.placeholder),
                        modifier = imageModifier,
                        contentScale = ContentScale.Companion.Crop
                    )
                }
            }

            Spacer(modifier = Modifier.Companion.width(16.dp))

            Button(
                onClick = {
                    val intent = Intent(Intent.ACTION_PICK).apply {
                        type = "image/*"
                    }
                    onImageSelect(intent)
                },
                modifier = Modifier.Companion.align(Alignment.Companion.CenterVertically)
            ) {
                Icon(
                    imageVector = Icons.Default.AccountBox,
                    contentDescription = null
                )
            }
        }
    }
}