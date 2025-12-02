package com.hiddendanang.app.ui.screen.profile.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.composables.icons.lucide.Lucide
import com.composables.icons.lucide.UserRoundPlus
import com.hiddendanang.app.R
import com.hiddendanang.app.ui.theme.Dimens
@Composable
fun NotLoggedInView(
    onLoginClick: () -> Unit = {},
    onSignUpClick: () -> Unit = {}
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.login))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.PaddingXLarge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Animation Section
        LottieAnimation(
            composition = composition,
            progress = { progress },
            modifier = Modifier
                .size(Dimens.ContainerLarge)
                .padding(bottom = Dimens.PaddingLarge)
        )

        // Title Section
        Text(
            text = stringResource(R.string.welcome),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = Dimens.PaddingSmall)
        )

        // Description Section
        Text(
            text = stringResource(R.string.welcome_description),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyMedium.lineHeight * 1.2,
            modifier = Modifier
                .padding(horizontal = Dimens.PaddingLarge)
                .padding(bottom = Dimens.PaddingXLarge)
        )

        // Login Button
        Button(
            onClick = onLoginClick,
            shape = RoundedCornerShape(Dimens.CornerRound),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.ButtonLarge),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.login_now),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        Spacer(modifier = Modifier.height(Dimens.PaddingMedium))

        // Divider with Text
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = Dimens.PaddingMedium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = Dimens.StrokeMedium
            )
            Text(
                text = stringResource(R.string.or),
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = Dimens.PaddingMedium)
            )
            HorizontalDivider(
                modifier = Modifier.weight(1f),
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                thickness = Dimens.StrokeMedium
            )
        }

        // Sign Up Button (Outlined)
        OutlinedButton(
            onClick = onSignUpClick,
            shape = RoundedCornerShape(Dimens.CornerRound),
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.ButtonLarge),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            border = ButtonDefaults.outlinedButtonBorder(enabled = true).copy(
                width = Dimens.StrokeMedium
            )
        ) {
            Icon(
                imageVector = Lucide.UserRoundPlus,
                contentDescription = null,
                modifier = Modifier
                    .size(Dimens.IconMedium)
                    .padding(end = Dimens.PaddingSmall)
            )
            Text(
                text = stringResource(R.string.register_new_account),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold
                )
            )
        }
        // Additional Info Text
        Text(
            text = stringResource(R.string.register_des),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .padding(top = Dimens.PaddingLarge)
                .padding(horizontal = Dimens.PaddingLarge)
        )
    }
}
