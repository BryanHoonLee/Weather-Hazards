package com.example.weatherapp.util

import android.util.Log
import com.esri.arcgisruntime.concurrent.ListenableFuture
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

/**
 * Suspends the current coroutine until the given future is completed. Throws any exception thrown by Future.get or
 * returns its result. This function also forwards any cancellation of the current coroutine to the future"
 */
suspend fun <T> ListenableFuture<T>.await(): T = suspendCancellableCoroutine { cont: CancellableContinuation<T> ->
    fun onDone() {
        if (cont.isCompleted) return
        val result = try {
            Log.i("COROUTINE", "TRY")

            get()
        } catch (t: Throwable) {
            Log.i("COROUTINE", "THROW")
            return
        }
        cont.resume(result as T)
    }

    addDoneListener(::onDone)
    cont.invokeOnCancellation {
        Log.i("COROUTINE", "CANCEL")

        removeDoneListener(::onDone)
        cancel(false)
    }
}