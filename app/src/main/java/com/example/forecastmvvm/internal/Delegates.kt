package com.example.forecastmvvm.internal

import kotlinx.coroutines.*

/**
 * Helper function that initializes value lazily. It allows to use await() function that waits for
 * asynchronous block to return its value.
 */
fun <T> lazyDeferred(block: suspend CoroutineScope.() -> T): Lazy<Deferred<T>>{
    return lazy {
        GlobalScope.async(start = CoroutineStart.LAZY) {
            block.invoke(this)
        }
    }
}