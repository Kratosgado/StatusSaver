package com.kratosgado.statussaver.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.kratosgado.statussaver.R

@Composable
fun PermissionDialog(
  showDialog: Boolean,
  hasStoragePermission: Boolean,
  hasNotificationPermission: Boolean,
  hasManageStoragePermission: Boolean,
  onDismiss: () -> Unit,
  onRequestStoragePermission: () -> Unit,
  onRequestNotificationPermission: () -> Unit,
  onRequestManageStorage: () -> Unit
) {
  if (!showDialog) return

  AlertDialog(
    onDismissRequest = onDismiss,
    title = { Text(stringResource(R.string.permissions_required)) },
    text = {
      Text(
        when {
          !hasStoragePermission -> stringResource(R.string.storage_permission_rationale)
          !hasNotificationPermission -> stringResource(R.string.notification_permission_rationale)
          !hasManageStoragePermission -> stringResource(R.string.manage_storage_permission_rationale)
          else -> ""
        }
      )
    },
    confirmButton = {
      TextButton(
        onClick = {
          when {
            !hasStoragePermission -> onRequestStoragePermission()
            !hasNotificationPermission -> onRequestNotificationPermission()
            !hasManageStoragePermission -> onRequestManageStorage()
          }
        }
      ) {
        Text(stringResource(R.string.grant_permission))
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text(stringResource(R.string.cancel))
      }
    }
  )
}