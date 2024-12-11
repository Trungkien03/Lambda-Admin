package loki.edu.yogaclassadmin.features.view.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.PopupProperties
import loki.edu.yogaclassadmin.model.ClassType
import kotlin.collections.forEach

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ClassTypeDropdownMenu(
    selectedType: String?,
    onTypeSelected: (String) -> Unit,
    classTypes: List<ClassType>
) {
    var expanded by remember { mutableStateOf(false) }

    // Lấy tên của `ClassType` dựa trên `selectedType`
    val selectedClassTypeName = classTypes.find { it.id == selectedType }?.name ?: "Select Class Type"

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { expanded = true } // Mở menu khi nhấn vào trường
    ) {
        OutlinedTextField(
            value = selectedClassTypeName, // Gán tên class type vào trường
            onValueChange = { },
            label = { Text("Class Type") },
            trailingIcon = {
                Icon(Icons.Default.ArrowDropDown, contentDescription = "Dropdown")
            },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = TextFieldDefaults.outlinedTextFieldColors(
                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                disabledBorderColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                disabledTrailingIconColor = MaterialTheme.colorScheme.onSurface
            ),
            enabled = false // Chỉ cho phép nhấn vào trường, không chỉnh sửa trực tiếp
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            classTypes.forEach { classType ->
                DropdownMenuItem(
                    text = { Text(classType.name) },
                    onClick = {
                        onTypeSelected(classType.id) // Gửi ID class type được chọn lên callback
                        expanded = false // Đóng menu
                    }
                )
            }
        }
    }
}
