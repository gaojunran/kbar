package gaojunran.kbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.InputMode
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalInputModeManager
import androidx.compose.ui.unit.dp
import gaojunran.kbar.MyStyles.Companion.getMonoFontFamily
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(isVisible: MutableState<Boolean>, focusRequester: FocusRequester) {
    val matchResult = remember { mutableListOf<GeneralItem>().toMutableStateList() }
    val cursor = remember { mutableStateOf(0 ) }

    MaterialTheme {
        val text = remember { mutableStateOf("") }
        val executeThisCommandItem  = remember (text) { GeneralItem(
            "Execute command", "",
            Action.PutToClipboard(text.value),
//            Action.ExecuteCommand(text.value)
        ) }

        LaunchedEffect(Unit) {
            registerKeyLambda("alt SPACE") {
                isVisible.value = !isVisible.value
                focusRequester.requestFocus()
            }
            focusRequester.requestFocus()
            matchResult.add(executeThisCommandItem)
        }

        Box(
            modifier = Modifier.clip(RoundedCornerShape(16.dp))
                .fillMaxWidth()
                .wrapContentHeight()
                .background(MyStyles.surColor)
                .onPreviewKeyEvent {
                    if (it.type == KeyEventType.KeyDown) {
                        when (it.key) {
                            Key.DirectionDown -> {
                                cursor.value++
                                return@onPreviewKeyEvent true
                            }

                            Key.DirectionUp -> {
                                cursor.value--
                                return@onPreviewKeyEvent true
                            }

                            Key.Enter -> {
                                matchResult[cursor.value].action.actionInvoke()
                                return@onPreviewKeyEvent true
                            }

                            else -> return@onPreviewKeyEvent false
                        }
                    } else {
                        return@onPreviewKeyEvent false
                    }
                }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.95f)
                    .align(Alignment.TopCenter),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                MainSearchBar(text, cursor, focusRequester, matchResult)
                Spacer(modifier = Modifier.height(32.dp))

                LazyColumn {
                    itemsIndexed(matchResult,
//                        key = {
//                            _, item ->
//                    }
                    ) {index, item ->
                        MainSearchResultItem(item, cursor, index)

                    }
                }
                if (matchResult.size != 0) {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }

    }
}


@Composable
fun MainSearchBar(text: MutableState<String>,
                  cursor: MutableState<Int>,
                  focusRequester: FocusRequester,
                  matchResult: List<GeneralItem>) {
    OutlinedTextField(
        value = text.value,
        onValueChange = { it ->
            text.value = it
            println("text: $text")
            cursor.value = 0
//            matchResult.filter { item -> !item.fallbackPrompt.isNullOrBlank() }
//                .forEach { item -> item.update(text.value) }
        },
        colors = TextFieldDefaults.outlinedTextFieldColors(cursorColor = Color.White),
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .height(80.dp)
            .fillMaxWidth()
            .background(MyStyles.textFieldColor)
            .border(2.dp, MyStyles.surColor)
            .focusRequester(focusRequester),
        textStyle = MaterialTheme.typography.h4.copy(color = Color.White, fontFamily = getMonoFontFamily()),
        singleLine = true,
    )
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun MainSearchResultItem(
    item: GeneralItem,
    cursor: MutableState<Int>,
    index: Int,
) {
//    var hoverActive by remember { mutableStateOf(false) }
    val bgColor by animateColorAsState(if (cursor.value == index) MaterialTheme.colors.primary else MyStyles.surColor)
    val txtColor by animateColorAsState(if (cursor.value == index) Color.White else Color.White)
    Card(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .onClick { item.action.actionInvoke() }
        .onPointerEvent(PointerEventType.Enter) { cursor.value = index }
        .onPointerEvent(PointerEventType.Exit) { },
        backgroundColor = bgColor
    ) {
        Text(
            item.title, style = MaterialTheme.typography.h4,
            modifier = Modifier.padding(16.dp),
            color = txtColor,
            fontFamily = getMonoFontFamily()
        )
    }
}




