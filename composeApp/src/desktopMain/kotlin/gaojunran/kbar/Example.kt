//import androidx.compose.foundation.layout.Column
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.MaterialTheme
//
//import androidx.compose.material.OutlinedTextField
//import androidx.compose.material.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.saveable.rememberSaveable
//import androidx.compose.runtime.setValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//
//@Composable
//fun HelloScreen() {
//    var name1 by rememberSaveable { mutableStateOf("") }
//
//    HelloContent(name = name1, onNameChange = { name1 = it })
//}
//
//@Composable
//fun HelloContent(name: String, onNameChange: (String) -> Unit) {
//    Column(modifier = Modifier.padding(16.dp)) {
//        Text(
//            text = "Hello, $name",
//            modifier = Modifier.padding(bottom = 8.dp),
//            style = MaterialTheme.typography.body1
//        )
//        OutlinedTextField(value = name, onValueChange = {name = it}, label = { Text("Name") })
//    }
//}