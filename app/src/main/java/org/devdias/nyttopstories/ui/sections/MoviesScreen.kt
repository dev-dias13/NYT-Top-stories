package org.devdias.nyttopstories.ui.sections

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.SubcomposeAsyncImage
import kotlinx.coroutines.launch
import org.devdias.nyttopstories.model.movies.MoviesResult
import org.devdias.nyttopstories.ui.theme.DeepBlue
import org.devdias.nyttopstories.ui.theme.TextWhite
import org.devdias.nyttopstories.util.Status
import org.devdias.nyttopstories.viewmodel.MoviesViewModel

@SuppressLint("CoroutineCreationDuringComposition")
@Composable
fun MoviesScreen(viewModel: MoviesViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var snackbarResult: SnackbarResult? = null
    val scaffoldState = rememberScaffoldState()
    val getAllData = viewModel.getData.observeAsState()
    Surface(
        color = MaterialTheme.colors.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            scaffoldState = scaffoldState
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.LightGray)
            ) {
                scope.launch {
                    val result = viewModel.getArtsData()
                    if (result is Status.Success) {
                        snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                            message = "Success"
                        )
                    } else if (result is Status.Error) {
                        snackbarResult = scaffoldState.snackbarHostState.showSnackbar(
                            message = "Error: ${result.message}"
                        )
                    }
                }

                if (!viewModel.isLoading.value) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(40.dp),
                            color = DeepBlue,
                            strokeWidth = 6.dp)
                    }
                }

                if (viewModel.isLoading.value) {
                    if (viewModel.getData.value?.moviesResults!!.isNotEmpty()) {
                        getAllData.value?.let { GreetingMoviesSection() }
                        LazyColumn(
                            modifier = Modifier.padding(10.dp)
                        ) {
                            items(getAllData.value?.moviesResults!!.size) { index ->
                                MovieItem(getAllData.value!!.moviesResults[index])
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun GreetingMoviesSection() {
    Box(modifier = Modifier
        .background(DeepBlue)
        .fillMaxWidth()) {
        Text(
            text = "Movies",
            color = TextWhite,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(15.dp)
                .align(Alignment.TopCenter)
        )
    }
}
@Composable
fun MovieItem(moviesResult: MoviesResult) {
    Card(
        modifier = Modifier
            .padding(10.dp)
            .fillMaxWidth()
            .height(400.dp), shape = RoundedCornerShape(8.dp), elevation = 4.dp
    ) {
        Surface() {
            Scaffold(
                Modifier
                    .padding(7.dp, 4.dp)
                    .fillMaxSize()
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
                ) {
                    Text(
                        text = moviesResult!!.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    SubcomposeAsyncImage(
                        model = moviesResult.multimedia[0].url,
                        loading = {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = DeepBlue,
                                strokeWidth = 4.dp)
                        },
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.clip(CircleShape)
                            .width(150.dp).height(150.dp).align(Alignment.CenterHorizontally),
                        contentDescription = moviesResult.multimedia[0].caption
                    )

                    Text(
                        text = moviesResult!!.subsection,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = moviesResult.byline,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.padding(5.dp))
                    Text(
                        text = moviesResult.abstract,
                        fontWeight = FontWeight.Bold,
                        overflow = TextOverflow.Clip
                    )
                    Spacer(modifier = Modifier.padding(5.dp))


                }
            }
        }
    }
}
@Preview
@Composable
fun MoviesScreenPreview() {
    MoviesScreen()
}