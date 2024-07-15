package com.example.task_manager

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.VectorConverter
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateValue
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandIn
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkOut
import androidx.compose.animation.togetherWith
import androidx.compose.animation.with
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DismissDirection
import androidx.compose.material3.DismissValue
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismiss
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import com.example.task_manager.ui.theme.Task_managerTheme
import com.example.task_manager.ui.theme.TodoItem
import com.example.task_manager.ui.theme.TodoViewModel
import kotlinx.coroutines.delay
import androidx.compose.material3.*
import androidx.compose.animation.*
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    private val todoViewModel: TodoViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Task_managerTheme {
                App(todoViewModel)

            }
        }
    }
}
@Composable
fun App(viewModel: TodoViewModel) {
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        delay(5000) // Задержка на 5 секунд
        isLoading = false
    }

    if (isLoading) {
        Loading()
    } else {
        MainPage()
        Notes(viewModel)
    }
}

@Composable
fun Loading () {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing)
        )
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()
    ) {
        Canvas(modifier = Modifier.size(50.dp)) {
            rotate(rotation) {
                drawArc(
                    color = Color.Black,
                    startAngle = 0f,
                    sweepAngle = 270f,
                    useCenter = false,
                    style = Stroke(width = 5.dp.toPx(), cap = StrokeCap.Round)
                )
            }
        }
    }
}

@Composable
fun MainPage(){
    
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(5.dp))
            .padding(5.dp)
            .fillMaxSize()
            .background(Color.White)
            .wrapContentSize(Alignment.TopStart)
    ){
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 50.dp),

            colors = CardDefaults.cardColors(
                containerColor = Color.LightGray,
            ),
        ) {
            Text(
                text = "Привет пользователь!",
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.CenterHorizontally),
                color = Color.Black,
                style = TextStyle(fontSize = 17.sp),
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Notes(viewModel: TodoViewModel){
    val items by viewModel.items.collectAsState()
    val textState = remember { mutableStateOf(TextFieldValue("")) }
    val sortedItems = items.sortedBy { it.isChecked }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 100.dp)

    ) {
        TextField(
            value = textState.value,
            onValueChange = {textState.value = it},
            placeholder = { Text("Добавьте первую заметку!") },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.LightGray
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        )
        Button(
            onClick = {
                if (textState.value.text.isNotBlank()) {
                    viewModel.addItem(textState.value.text)
                    textState.value = TextFieldValue("")
                }
            },
            modifier = Modifier.align(Alignment.End),
            colors = ButtonColors(
                containerColor = Color.LightGray,
                contentColor = Color.Black,
                disabledContentColor = Color.Black,
                disabledContainerColor = Color.LightGray)
        ) {
            Text("Добавить")
        }
        Spacer(modifier = Modifier.height(10.dp))
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            items(sortedItems, key = { it.id }) { item ->
                AnimatedContent(
                    targetState = item,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(600)) togetherWith fadeOut(animationSpec = tween(600)) using SizeTransform { initialSize, targetSize ->
                            tween(durationMillis = 600)
                        }
                    }
                ) { targetItem ->
                    SwipeToDelete(
                        item = targetItem,
                        onCheckedChange = { viewModel.toggleItemChecked(targetItem.id) },
                        onDelete = { viewModel.removeItem(targetItem.id) }
                    )
                }
            }
        }
    }
}


@Composable
fun TodoItem(item: TodoItem, onCheckedChange: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically

    ) {
        Checkbox(
            checked = item.isChecked,
            onCheckedChange = { onCheckedChange(it) },
        )

        Text(text = item.text,
            textAlign = TextAlign.Left
            )

        Spacer(modifier = Modifier.weight(1f))

//        IconButton(onClick = { onDelete() },
//            modifier = Modifier.size(24.dp)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Clear,
//                contentDescription = "Del",
//                tint = Color.Red
//            )

    }
}

@Composable
fun SwipeToDelete(
    item: TodoItem,
    onCheckedChange: (Boolean) -> Unit,
    onDelete: () -> Unit
) {
    var offsetX by remember { mutableStateOf(0f) }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(end = 10.dp)
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragEnd = {
                        if (offsetX.absoluteValue > 300f) {
                            scope.launch {
                                onDelete()
                            }
                        } else {
                            offsetX = 0f
                        }
                    }
                ) { change, dragAmount ->
                    change.consumeAllChanges()
                    offsetX += dragAmount
                }
            }
    ) {
        Row(
            modifier = Modifier
                .offset { IntOffset(offsetX.roundToInt(), 0) }
                .fillMaxWidth()
                .background(Color.White)
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = item.isChecked,
                onCheckedChange = { onCheckedChange(it) },
            )

            Text(
                text = item.text,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

