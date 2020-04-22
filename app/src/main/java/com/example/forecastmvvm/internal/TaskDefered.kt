package com.example.forecastmvvm.internal

import com.google.android.gms.tasks.Task
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred

fun <T> Task<T>.asDeferred(): Deferred<T>{
    val deferred = CompletableDeferred<T>()

    addOnSuccessListener { result -> deferred.complete(result) }

    addOnFailureListener { exception -> deferred.completeExceptionally(exception) }

    return deferred
}