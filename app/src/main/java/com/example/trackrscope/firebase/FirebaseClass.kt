package com.example.trackrscope.firebase

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import androidx.core.app.NotificationCompat
import com.example.trackrscope.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * Servicio de Firebase para notificaciones.
 */
@SuppressLint("MissingFirebaseInstanceTokenRefresh")
class FirebaseClass : FirebaseMessagingService() {

    /**
     * Maneja las notificaciones entrantes.
     *
     * @param message Mensaje de notificación.
     */
    override fun onMessageReceived(message: RemoteMessage) {
        createNotificationChannel() // Crea el canal de notificación si es necesario.

        message.notification?.let { // Obtiene la notificación del mensaje.
            showNotification(it.title, it.body)
        }
    }

    /**
     * Muestra una notificación con el título y el cuerpo proporcionados.
     *
     * @param title Título de la notificación.
     * @param body Cuerpo de la notificación.
     */
    private fun showNotification(title: String?, body: String?) {
        // Obtengo el gestor de notificaciones.
        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val notificationId =
            System.currentTimeMillis().toInt() // Crea un Id único para la notificación.

        val notificationBuilder = NotificationCompat.Builder(this, "default_channel_id")
            .setSmallIcon(R.drawable.trackr_scope_logo_dark) // Logo
            .setContentTitle(title) // Título
            .setContentText(body) // Cuerpo
            .setPriority(NotificationCompat.PRIORITY_DEFAULT) // Prioridad
            .setAutoCancel(true) // Destruye la notificación al hacer click en ella

        // Muestra la notificación.
        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    /**
     * Crea un canal de notificación si es necesario.
     */
    private fun createNotificationChannel() {
        val name = "TrackrScope" // Nombre del canal.
        val descriptionText = "Notificaciones de TrackrScope" // Descripción del canal.
        val importance = NotificationManager.IMPORTANCE_DEFAULT // Prioridad del canal.
        // Creo el canal de notificación.
        val channel = NotificationChannel("default_channel_id", name, importance).apply {
            description = descriptionText
        }
        // Registro el canal en el sistema.
        val notificationManager: NotificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}