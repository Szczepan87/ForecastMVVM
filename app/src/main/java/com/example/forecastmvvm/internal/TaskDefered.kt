package com.example.forecastmvvm.internal

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

/**
 * Extension function changing Task returned by FusedLocationProviderClient from google.gms library.
 * Returns CompletableDeferred with catching possible Error caused by i.e. no permissions granted.
 */
fun <T> Task<T>.asDeferred(): Deferred<T>{
    val deferred = CompletableDeferred<T>()

    addOnSuccessListener { result -> deferred.complete(result) }

    addOnFailureListener { exception -> deferred.completeExceptionally(exception) }

    return deferred
}