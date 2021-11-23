package com.example.hongcomposeweek2

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Stable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.*
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintSet
import androidx.constraintlayout.compose.Dimension
import coil.compose.rememberImagePainter
import com.example.hongcomposeweek2.ui.theme.HongComposeWeek2Theme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HongComposeWeek2Theme {
                // A surface container using the 'background' color from the theme
                Surface(color = MaterialTheme.colors.background) {
//                    PhotographerCard()
//                    LayoutsCodelab()
//                    ScrollingList()
//                    Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
//                    BodyContentForMyOwnColumn()
//                    BodyContent3()
//                    ConstraintLayoutContent()
//                    LargeConstraintLayout()
                    TwoTexts(text1 = "Hello", text2 = "World")
                }
            }
        }
    }
}

@Composable
fun TwoTexts(modifier: Modifier = Modifier, text1: String, text2: String) {
    // IntrinsicSize.Min 을 이용해 child 들의 height 중 maximum 값을 가져오게 된다.
    Row(modifier = modifier.height(IntrinsicSize.Min)) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 4.dp)
                .wrapContentWidth(Alignment.Start),
            text = text1
        )

        Divider(color = Color.Black, modifier = Modifier
            .fillMaxHeight()
            .width(1.dp))

        Text(
            modifier = Modifier
                .weight(1f)
                .padding(end = 4.dp)
                .wrapContentWidth(Alignment.End),

            text = text2
        )
    }
}


@Composable
fun DecoupledConstraintLayout() {
    BoxWithConstraints {
        // 기존에는 LargeConstraintLayout()에서 보듯이, constraints를 inline 해서 정의했다.
        // 그러나, constraint가 동적으로 바뀌어야 하는 경우가 있다면?
        //   - ex) 화면 방향 전환에 따라 constraint를 다르게 줘야 하는 경우
        //     - 아래처럼 ConstraintSet을 이용해서 decoupled 시키면 된다.
        val constraints = if (maxWidth < maxHeight) {
            decoupledConstraints(margin = 16.dp)
        } else {
            decoupledConstraints(margin = 32.dp)
        }

        ConstraintLayout(constraints) {
            Button(
                onClick = {},
                modifier = Modifier.layoutId("button")
            ) {
                Text("Button")
            }

            Text("Text", Modifier.layoutId("text"))
        }
    }
}

private fun decoupledConstraints(margin: Dp): ConstraintSet {
    return ConstraintSet {
        val button = createRefFor("button")
        val text = createRefFor("text")

        constrain(button) {
            top.linkTo(parent.top, margin= margin)
        }
        constrain(text) {
            top.linkTo(button.bottom, margin)
        }
    }
}

@Composable
fun LargeConstraintLayout() {
    ConstraintLayout {
        val text = createRef()

        val guideline = createGuidelineFromStart(0.5f)
        Text(
            "This is a very very very very very very very long text",
            Modifier.constrainAs(text) {
                linkTo(guideline, parent.end)
                // preferredWrapContent: the layout is wrap content, subject to the constraints in that dimension.
                //  - 제한된 범위 내에서 wrap_content // 화면 밖을 벗어나지 않는다
                // wrapContent: the layout is wrap content even if the constraints would not allow it.
                //  - 범위 밖으로 wrap_content // 화면을 벗어난다.
                width = Dimension.preferredWrapContent
            }
        )
    }
}

@Composable
fun ConstraintLayoutContent() {
    ConstraintLayout {
        val (button1, button2, text) = createRefs()

        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button1) {
                top.linkTo(parent.top, margin = 16.dp)
            }
        ) {
            Text("Button 1")
        }

        Text("Text", Modifier.constrainAs(text) {
            top.linkTo(button1.bottom, margin = 16.dp)
            centerAround(button1.end)
        })

        val barrier = createEndBarrier(button1, text)
        Button(
            onClick = { /* Do something */ },
            modifier = Modifier.constrainAs(button2) {
                top.linkTo(parent.top, margin = 16.dp)
                start.linkTo(barrier)
            }
        ) {
            Text("Button 2")
        }
    }
}

// How to create a modifier
@Stable
fun Modifier.padding(all: Dp) =
    this.then(
        PaddingModifier(start = all, top = all, end = all, bottom = all, rtlAware = true)
    )

class PaddingModifier(
    val start: Dp = 0.dp,
    val top: Dp = 0.dp,
    val end: Dp = 0.dp,
    val bottom: Dp = 0.dp,
    val rtlAware: Boolean = false
) : LayoutModifier {
    override fun MeasureScope.measure(
        measurable: Measurable,
        constraints: Constraints
    ): MeasureResult {
        val horizontal = start.roundToPx() + end.roundToPx()
        val vertical = top.roundToPx() + bottom.roundToPx()

        val placeable = measurable.measure(constraints.offset(-horizontal, -vertical))

        val width = constraints.constrainWidth(placeable.width + horizontal)
        val height = constraints.constrainHeight(placeable.height + vertical)
        return layout(width, height) {
            if (rtlAware) {
                placeable.placeRelative(start.roundToPx(), top.roundToPx())
            } else {
                placeable.place(start.roundToPx(), top.roundToPx())
            }
        }
    }
}

val topics = listOf(
    "Arts & Crafts", "Beauty", "Books", "Business", "Comics", "Culinary",
    "Design", "Fashion", "Film", "History", "Maths", "Music", "People", "Philosophy",
    "Religion", "Social sciences", "Technology", "TV", "Writing"
)


@Composable
fun BodyContent3(modifier: Modifier = Modifier) {
    // StaggeredGridLayout이 화면 밖으로 벗어나게 되므로, scrollable한 Row로 감싼다.
    // chaining 순서를 조심하자!
    //   - padding(16.dp).size(200.dp) -> Row는 232 * 232 / StaggeredGrid는 200 * 200
    //   - size(200.dp).padding(16.dp) -> Row는 200 * 200 / StaggeredGrid는 168 * 168
    Row(
        modifier = modifier
            .background(color = Color.LightGray)
            .padding(16.dp)
            .size(200.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        StaggeredGridLayout {
            for (topic in topics) {
                Chip(modifier = Modifier.padding(8.dp), text = topic)
            }
        }
    }
}

@Composable
fun Chip(modifier: Modifier = Modifier, text: String) {
    Card(
        modifier = modifier,
        border = BorderStroke(color = Color.Black, width = Dp.Hairline),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier.padding(start = 8.dp, top = 4.dp, end = 8.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(16.dp, 16.dp)
                    .background(color = MaterialTheme.colors.secondary)
            )
            Spacer(Modifier.width(4.dp))
            Text(text = text)
        }
    }
}

@Composable
fun StaggeredGridLayout(
    modifier: Modifier = Modifier,
    rows: Int = 3,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val rowWidths = IntArray(rows) { 0 }
        val rowHeights = IntArray(rows) { 0 }

        val placeables = measurables.mapIndexed { index, measurable ->
            val placeable = measurable.measure(constraints)
            // Track the width and max height of each row
            val row = index % rows
            // width는 accumulate, height는 max로 해주면 된다.
            rowWidths[row] += placeable.width
            rowHeights[row] = Math.max(rowHeights[row], placeable.height)
            placeable
        }

        val width = rowWidths.maxOrNull()
            ?.coerceIn(constraints.minWidth.rangeTo(constraints.maxWidth)) ?: constraints.minWidth
        val height = rowHeights.sumOf { it }
            .coerceIn(constraints.minHeight.rangeTo(constraints.maxHeight))

        // Y of each row, based on the height accumulation of previous rows
        val rowY = IntArray(rows) { 0 }
        for (i in 1 until rows) {
            rowY[i] = rowY[i - 1] + rowHeights[i - 1]
        }

        // Set the size of the parent layout
        layout(width, height) {
            // x cord we have placed up to, per row
            val rowX = IntArray(rows) { 0 }

            placeables.forEachIndexed { index, placeable ->
                val row = index % rows
                placeable.placeRelative(
                    x = rowX[row],
                    y = rowY[row]
                )
                rowX[row] += placeable.width
            }
        }
    }
}

@Composable
fun BodyContentForMyOwnColumn(modifier: Modifier = Modifier) {
    MyOwnColumn(modifier.padding(8.dp)) {
        Text("MyOwnColumn")
        Text("places items")
        Text("vertically.")
        Text("We've done it by hand!")
        Text("We've done it by hand!")
        Text("We've done it by hand!")
        Text("Temp", fontSize = 100.sp)
    }
}

// measurable: child to be measured and placed // 여기서는 children.
// constraints: minimum and maximum for the width and height of the child // 여기서는 각 child
@Composable
fun MyOwnColumn(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Layout(
        modifier = modifier,
        content = content
    ) { measurables, constraints ->
        val placeables = measurables.map {
            it.measure(constraints) // Measure each child
        }

        var yPosition = 0

        // 현재로써는 maxWidth, maxHeight는 screenWidth, screenHeight가 되는듯 싶다.
        // 이 범위 내에서 placeables를 그리겠다!
        layout(constraints.maxWidth, constraints.maxHeight) {
            placeables.forEach { placeable ->
                Log.d("MasonTesting", "constraints.maxWidth: ${constraints.maxWidth}")
                Log.d("MasonTesting", "constraints.maxHeight: ${constraints.maxHeight}")
                placeable.placeRelative(x = 0, y = yPosition)
                yPosition += placeable.height
            }
        }
    }
}


// measurable: child to be measured and placed
// constraints: minimum and maximum for the width and height of the child
fun Modifier.firstBaselineToTop(
    firstBaselineToTop: Dp
) = this.then(
    layout { measurable, constraints -> // TextWithPaddingToBaselinePreview() 기준 TextView가 measurable로 들어오는 것 같다.
        val placeable = measurable.measure(constraints) // for placeRelative
        check(placeable[FirstBaseline] != AlignmentLine.Unspecified)
        val firstBaseline = placeable[FirstBaseline]

        val placeableY = firstBaselineToTop.roundToPx() - firstBaseline
        val height = placeable.height + placeableY
        layout(placeable.width, height) { // 크기 및 위치 조정
            placeable.placeRelative(0, placeableY)
        }
    }
)

@Composable
fun ImageListItem(index: Int) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = rememberImagePainter(
                data = "https://developer.android.com/images/brand/Android_Robot.png"
            ),
            contentDescription = "Android Logo",
            modifier = Modifier.size(50.dp)
        )
        Spacer(Modifier.width(10.dp))
        Text("Item #$index", style = MaterialTheme.typography.subtitle1)
    }
}

@Composable
fun ScrollingList() {
    val listSize = 100
    val scrollState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Column {
        Row {
            Button(onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(0)
                }
            }) {
                Text("Scroll to the top")
            }

            Button(onClick = {
                coroutineScope.launch {
                    scrollState.animateScrollToItem(listSize - 1)
                }
            }) {
                Text("Scroll to the end")
            }
        }
        LazyColumn(state = scrollState) {
            items(100) {
                ImageListItem(it)
            }
        }
    }
}

@Composable
fun PhotographerCard(modifier: Modifier = Modifier) {
    Row(
        modifier
            .padding(8.dp)
            .clip(RoundedCornerShape(4.dp))
            .background(MaterialTheme.colors.surface)
            .clickable(onClick = {})
            .padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.size(50.dp),
            shape = CircleShape,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)
        ) {

        }
        Column(
            modifier = modifier
                .padding(start = 8.dp)
                .align(Alignment.CenterVertically)
        ) {
            Text("Alfered Sisley", fontWeight = FontWeight.Bold)
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                Text("3 minutes ago", style = MaterialTheme.typography.body2)
            }
        }
    }
}

@Composable
fun LayoutsCodelab() {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(text = "LayoutsCodelab")
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(Icons.Filled.Favorite, contentDescription = null)
                    }
                }
            )
        }
    ) { innerPadding ->
        BodyContent(
            Modifier
                .padding(innerPadding)
                .padding(8.dp)
        )
    }
}

@Composable
fun BodyContent(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = "Hi there")
        Text(text = "Thanks for going through the Layouts codelab")
    }
}

@Preview
@Composable
fun TextWithPaddingToBaselinePreview() {
    HongComposeWeek2Theme {
        Text("Hi there!", Modifier.firstBaselineToTop(32.dp))
    }
}

@Preview
@Composable
fun TextWithNormalPaddingPreview() {
    HongComposeWeek2Theme {
        Text("Hi there!", Modifier.padding(top = 32.dp))
    }
}

@Preview
@Composable
fun PhotographerCardPreview() {
    HongComposeWeek2Theme {
        PhotographerCard()
    }
}

@Preview
@Composable
fun LayoutsCodelabPreview() {
    HongComposeWeek2Theme {
        LayoutsCodelab()
    }
}