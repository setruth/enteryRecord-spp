package com.setruth.entityflowrecord.ui.pages.home.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.setruth.entityflowrecord.data.model.ThemeMode
import com.setruth.entityflowrecord.ui.frame.MainFrame
import com.setruth.entityflowrecord.ui.theme.EntityFlowRecordTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectDialog() {
    BasicAlertDialog(onDismissRequest = {}) {
        Card{
            Column {
                Text("连接设备")
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 360, heightDp = 720)
@Composable
fun ConnectDialogPreview() {
    EntityFlowRecordTheme(ThemeMode.LIGHT) {
        ConnectDialog()
    }
}