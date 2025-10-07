package com.PersonaPulse.personapulse.ui.components.common

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PersonaPulseTitle() {
    Text(
        text = "PersonaPulse",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.SansSerif,
        color = Color.White,
        textAlign = TextAlign.Start,
        style = TextStyle(
            shadow = Shadow(
                color = Color.White.copy(alpha = 0.3f),
                offset = Offset(0f, 2f),
                blurRadius = 8f
            )
        ),
        modifier = Modifier
            .shadow(
                elevation = 4.dp,
                shape = RoundedCornerShape(4.dp),
                ambientColor = Color.White.copy(alpha = 0.2f),
                spotColor = Color.White.copy(alpha = 0.1f)
            )
            .padding(horizontal = 4.dp)
    )
}
