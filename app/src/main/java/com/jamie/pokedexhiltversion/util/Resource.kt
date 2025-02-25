package com.jamie.pokedexhiltversion.util

sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    //class Loading<T>(data: T? = null) : Resource<T>(data)
    //I am not using this Loading state because I have defined another state in ViewModel. I do not need this in this project.
}