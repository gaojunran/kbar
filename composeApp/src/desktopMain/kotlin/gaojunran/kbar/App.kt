package gaojunran.kbar

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.onClick
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.*
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogWindow
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.rememberDialogState
import gaojunran.kbar.MyStyles.Companion.getMonoFontFamily
import gaojunran.kbar.MyStyles.Companion.textDescColor
import gaojunran.kbar.MyStyles.Companion.textTitleColor
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview


@Composable
@Preview
fun App(isVisible: MutableState<Boolean>, focusRequester: FocusRequester) {
    var isDialogOpen by remember { mutableStateOf(false) }
    val matchResult = remember { mutableListOf<GeneralItem>().toMutableStateList() }
    val cursor = remember { mutableStateOf(0) }
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()

    if (isDialogOpen) {
        DialogWindow(
            undecorated = true,
            transparent = true,
            onCloseRequest = { isDialogOpen = false },
            state = rememberDialogState(position = WindowPosition(Alignment.Center), width = 400.dp, height = 400.dp),
            onPreviewKeyEvent = {
                when {
                    it.key == Key.Enter && it.type == KeyEventType.KeyDown -> {
                        isDialogOpen = false
                        return@DialogWindow true
                    }

                    else -> return@DialogWindow false
                }
            }
        ) {
            Box(modifier = Modifier.background(color = MyStyles.surColor, shape = RoundedCornerShape(16.dp)).fillMaxSize()){
                Text("Debug", color = Color.White)
            }
        }
    }


    MaterialTheme {
        val fieldText = remember { mutableStateOf("") }
        val debugThisItem = GeneralItem(
            "[Debug]",
            Action.Lambda {
                isDialogOpen = !isDialogOpen
            },
        )

        // Running only once at the beginning
        LaunchedEffect(Unit) {
            // request focus to text field
            focusRequester.requestFocus()

            // init the connection to sqlite
            initSqlite()

            // register the show/hide hotkey and other hotkeys
            registerKeyLambda(MyKeys.ALT_SPACE) {
                isVisible.value = !isVisible.value
                // Thank you: https://stackoverflow.com/questions/74391260/jetpack-compose-requestfocus-works-only-once
                scope.launch {
                    focusManager.clearFocus(true)
                    delay(100)
                    focusRequester.requestFocus()
                }
                fieldText.value = ""
            }
            registerKeys(loadConfigList("config/hotKeyConfig.json"))

            // load configs to sqlite
            clearTable()
            insertBatch(loadConfigList<NormalConfig>("config/normalConfig.json").map { it.toGeneralItem() })
        }

        LaunchedEffect(fieldText.value) {
            // putting searching action into LaunchedEffect to avoid stuffing mainThread
            matchResult.clear()
            if (fieldText.value.isNotBlank()) {
                matchResult.addAll(
                    searchDynamic(fieldText.value)
                )
                matchResult.add(debugThisItem)
            }
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
                                if (cursor.value < matchResult.size - 1) {
                                    cursor.value++
                                }
                                return@onPreviewKeyEvent true
                            }

                            Key.DirectionUp -> {
                                if (cursor.value > 0) {
                                    cursor.value--
                                }
                                return@onPreviewKeyEvent true
                            }

                            Key.Enter -> {
                                if (matchResult.size != 0) matchResult[cursor.value].action.invoke()
                                return@onPreviewKeyEvent true
                            }

                            Key.NumPadEnter -> {
                                if (matchResult.size != 0) matchResult[cursor.value].action.invoke()
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
                MainSearchBar(fieldText, cursor, focusRequester)
                Spacer(modifier = Modifier.height(32.dp))
                LazyColumn {
                    itemsIndexed(matchResult, key = { _, item -> item.hashCode() }) { index, item ->
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
fun MainSearchBar(
    text: MutableState<String>,
    cursor: MutableState<Int>,
    focusRequester: FocusRequester,
) {
    OutlinedTextField(
        value = text.value,
        onValueChange = {
            text.value = it
            cursor.value = 0
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
    val bgColor by animateColorAsState(if (cursor.value == index) MaterialTheme.colors.primary else MyStyles.surColor)
    Card(modifier = Modifier
        .clip(RoundedCornerShape(16.dp))
        .fillMaxWidth()
        .onClick { item.action.invoke() }
        .onPointerEvent(PointerEventType.Enter) { cursor.value = index }
        .onPointerEvent(PointerEventType.Exit) { },
        backgroundColor = bgColor
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween) {
            Text(
                item.title, style = MaterialTheme.typography.h4,
                modifier = Modifier.padding(16.dp),
                color = textTitleColor,
                fontFamily = getMonoFontFamily()
            )
            Text(
                item.desc ?: "", style = MaterialTheme.typography.h5,
                modifier = Modifier.padding(16.dp),
                color = textDescColor,
                fontFamily = getMonoFontFamily()
            )
        }

    }
}