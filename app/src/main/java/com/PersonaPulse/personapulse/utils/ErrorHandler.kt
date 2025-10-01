package com.PersonaPulse.personapulse.utils

import com.google.gson.JsonSyntaxException
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException


object ErrorHandler {
    

    fun getErrorMessage(exception: Throwable): String {
        return when (exception) {
            is HttpException -> {
                when (exception.code()) {
                    401 -> "Authentication failed. Please check your API key."
                    403 -> "Access forbidden. Please check your permissions."
                    404 -> "Resource not found. Please try again."
                    429 -> "Too many requests. Please wait a moment and try again."
                    500 -> "Server error. Please try again later."
                    else -> "Network error: ${exception.code()}"
                }
            }
            is UnknownHostException -> "No internet connection. Please check your network."
            is SocketTimeoutException -> "Request timeout. Please try again."
            is JsonSyntaxException -> "Data format error. Please try again."
            is IllegalArgumentException -> "Invalid input. Please check your data."
            is SecurityException -> "Permission denied. Please check app permissions."
            else -> "An unexpected error occurred: ${exception.message ?: "Unknown error"}"
        }
    }
    
    /**
     * Checks if an exception is a network-related error
     * @param exception The exception to check
     * @return true if it's a network error, false otherwise
     */

    fun isNetworkError(exception: Throwable): Boolean {
        return when (exception) {
            is UnknownHostException,
            is SocketTimeoutException,
            is HttpException -> true
            else -> false
        }
    }
    
    /**
     * Checks if an exception is a data parsing error
     * @param exception The exception to check
     * @return true if it's a parsing error, false otherwise
     */

    fun isDataError(exception: Throwable): Boolean {
        return when (exception) {
            is JsonSyntaxException,
            is IllegalArgumentException -> true
            else -> false
        }
    }
    
    /**
     * Logs the exception for debugging purposes
     * @param exception The exception to log
     * @param tag Optional tag for the log
     */

    fun logException(exception: Throwable, tag: String = "ErrorHandler") {
        println("$tag: ${exception.javaClass.simpleName} - ${exception.message}")
        exception.printStackTrace()
    }
}













