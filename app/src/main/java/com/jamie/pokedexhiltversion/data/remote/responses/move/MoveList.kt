package com.jamie.pokedexhiltversion.data.remote.responses.move

import com.jamie.pokedexhiltversion.data.remote.responses.Result

data class MoveList(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Result>
)