package com.jamie.pokedexhiltversion.util

import androidx.compose.ui.graphics.Color
import com.jamie.pokedexhiltversion.data.remote.responses.Stat
import com.jamie.pokedexhiltversion.data.remote.responses.Type
import com.jamie.pokedexhiltversion.data.remote.responses.TypeX
import com.jamie.pokedexhiltversion.ui.theme.*
import java.util.*

private fun parseTypeStringToColor(typeName: String): Color {
    return when (typeName.lowercase(Locale.ROOT)) {
        "normal" -> TypeNormal
        "fire" -> TypeFire
        "water" -> TypeWater
        "electric" -> TypeElectric
        "grass" -> TypeGrass
        "ice" -> TypeIce
        "fighting" -> TypeFighting
        "poison" -> TypePoison
        "ground" -> TypeGround
        "flying" -> TypeFlying
        "psychic" -> TypePsychic
        "bug" -> TypeBug
        "rock" -> TypeRock
        "ghost" -> TypeGhost
        "dragon" -> TypeDragon
        "dark" -> TypeDark
        "steel" -> TypeSteel
        "fairy" -> TypeFairy
        else -> Color.Black
    }
}

// For Pokemon types (which are nested)
fun parseTypeToColor(type: Type): Color {
    return parseTypeStringToColor(type.type.name)
}

// Overloaded function for Move types (which are not nested)
fun parseTypeToColor(type: TypeX): Color {
    return parseTypeStringToColor(type.name)
}

fun parseStatToColor(stat: Stat): Color {
    return when(stat.stat.name.lowercase(Locale.ROOT)) {
        "hp" -> HPColor
        "attack" -> AtkColor
        "defense" -> DefColor
        "special-attack" -> SpAtkColor
        "special-defense" -> SpDefColor
        "speed" -> SpdColor
        else -> Color.White
    }
}

fun parseStatToAbbr(stat: Stat): String {
    return when(stat.stat.name.lowercase(Locale.ROOT)) {
        "hp" -> "HP"
        "attack" -> "Atk"
        "defense" -> "Def"
        "special-attack" -> "SpAtk"
        "special-defense" -> "SpDef"
        "speed" -> "Spd"
        else -> ""
    }
}