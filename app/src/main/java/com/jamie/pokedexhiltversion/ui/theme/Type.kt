package com.jamie.pokedexhiltversion.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp


// Initialize Google Fonts provider
val provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = com.jamie.pokedexhiltversion.R.array.com_google_android_gms_fonts_certs
)

// Define Roboto font family using Google Fonts
val robotoFamily = FontFamily(
    Font(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
        weight = FontWeight.Light,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
        weight = FontWeight.Normal,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
        weight = FontWeight.Normal,
        style = FontStyle.Italic
    ),
    Font(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
        weight = FontWeight.Medium,
        style = FontStyle.Normal
    ),
    Font(
        googleFont = GoogleFont("Roboto"),
        fontProvider = provider,
        weight = FontWeight.Bold,
        style = FontStyle.Normal
    )
)

// Define Typography with Material3 standards
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp
    ),
    bodySmall = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp
    ),
    titleLarge = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp
    ),
    titleMedium = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 18.sp
    ),
    titleSmall = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp
    ),
    labelLarge = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize = 14.sp
    ),
    labelMedium = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp
    ),
    labelSmall = TextStyle(
        fontFamily = robotoFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 10.sp
    )
)
