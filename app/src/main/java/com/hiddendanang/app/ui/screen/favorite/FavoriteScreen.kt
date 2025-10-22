package com.hiddendanang.app.ui.screen.favorite

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.components.PlaceCard
import com.hiddendanang.app.ui.model.PlaceViewModel
import com.hiddendanang.app.ui.screen.profile.NotLoggedInView
import com.hiddendanang.app.ui.screen.profile.User
import com.hiddendanang.app.ui.theme.Dimens
import java.net.URL

@Composable
fun FavoriteScreen(
    navController: NavHostController,
    user: User?
) {
    Box(
        modifier = Modifier.background(MaterialTheme.colorScheme.background)
            .fillMaxSize()
    ) {
        if(user == null){
            NotLoggedInView(onLoginClick = { /* do something */})
        }
        else{
            FavoriteView()
        }
    }
}

@Composable
fun FavoriteView(){
    val viewmodel: PlaceViewModel = viewModel()
    val places = remember{mutableStateOf(viewmodel.topPlace)}
    if(places.value.isEmpty()){
        val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.heart_off))
        val progress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Dimens.PaddingXLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(Dimens.ContainerMedium)
                    .padding(bottom = Dimens.PaddingLarge)
            )

            Text(
                stringResource(R.string.no_favorite),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(Dimens.PaddingSmall))
            Text(
                stringResource(R.string.no_favorite_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = Dimens.PaddingMedium, vertical = Dimens.PaddingSmall)
            )

        }
        return
    }
    Column {
        Text(
            text = stringResource(R.string.my_favorite),
            modifier = Modifier.fillMaxSize(),
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
        )

        for(place in places.value) {
            PlaceCard(place)
        }
    }
}

@Preview
@Composable
fun Show(){
    val user = User(
        "Nguyễn Văn A",
        "nguyenvana@email.com",
        URL("https://via.placeholder.com/150")
    )
    val navController: NavHostController = rememberNavController()
    FavoriteScreen(navController, user)
}
