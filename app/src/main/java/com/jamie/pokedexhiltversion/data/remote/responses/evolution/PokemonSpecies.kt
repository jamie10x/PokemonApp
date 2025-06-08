package com.jamie.pokedexhiltversion.data.remote.responses.evolution

data class PokemonSpecies(
    val evolution_chain: EvolutionChainUrl
)

data class EvolutionChainUrl(
    val url: String
)