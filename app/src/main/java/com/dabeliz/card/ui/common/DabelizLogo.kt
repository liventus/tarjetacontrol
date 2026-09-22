package com.dabeliz.card.ui.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dabeliz.card.R
import com.dabeliz.card.ui.theme.DabelizGold

/**
 * Marca completa de Dabeliz: insignia + wordmark. Se usa en Login y en
 * cualquier pantalla donde la marca deba destacar (splash, encabezados).
 */
@Composable
fun DabelizLogo(
    modifier: Modifier = Modifier,
    markSize: Dp = 88.dp,
    showWordmark: Boolean = true,
    /** false = solo el monograma dorado, sin el fondo navy (para fondos oscuros). */
    useBadge: Boolean = true,
    wordmarkColor: Color = Color.Unspecified
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DabelizMark(size = markSize, useBadge = useBadge)

        if (showWordmark) {
            Text(
                text = "dabeliz",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                color = wordmarkColor,
                modifier = Modifier.padding(top = 10.dp)
            )
            Box(
                modifier = Modifier
                    .padding(top = 4.dp)
                    .width(40.dp)
                    .height(3.dp)
                    .clip(RoundedCornerShape(50))
                    .background(DabelizGold)
            )
        }
    }
}

/** Solo la insignia (sin texto), pensada para barras superiores pequeñas. */
@Composable
fun DabelizMark(
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    useBadge: Boolean = true
) {
    Image(
        painter = painterResource(
            id = if (useBadge) R.drawable.ic_dabeliz_badge else R.drawable.ic_launcher_foreground
        ),
        contentDescription = "Dabeliz",
        modifier = modifier.size(size)
    )
}
