package com.airbnb.lottie.issues.compose

import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.lifecycleScope
import com.airbnb.lottie.LottieCompositionFactory
import com.airbnb.lottie.LottieListener
import com.airbnb.lottie.LottieTask
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class ComposeIssueReproActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Content()
        }
    }

    @Composable
    fun Content() {
        var count = 0

        Column {
            Button(
                onClick = {
                    Log.d("test","Click me a clicked")
                    val currentCount = count++
                    val task = LottieCompositionFactory
                        .fromUrl(this@ComposeIssueReproActivity, LOTTIE_FILE_URL)
                    task.addListener { Log.d("test", "onResult $currentCount") }
                    task.addFailureListener { Log.d("test", "onFail $currentCount") }
                }
            ) {
                Text(text = "Click me a")
            }

            Button(
                onClick = {
                    Log.d("test","Click me b clicked")
                    lifecycleScope.launch {
                        LottieCompositionFactory
                            .fromUrl(this@ComposeIssueReproActivity, LOTTIE_FILE_URL)
                            .await()
                        LottieCompositionFactory
                            .fromUrl(this@ComposeIssueReproActivity, LOTTIE_FILE_URL)
                            .await()
                    }
                }
            ) {
                Text(text = "Click me b")
            }
        }
    }

    private suspend fun <T> LottieTask<T>.await(): T = suspendCancellableCoroutine { continuation ->
        val successListener = LottieListener<T> { continuation.resume(it) }
        val failureListener = LottieListener<Throwable> { continuation.resumeWithException(it) }

        addListener(successListener)
        addFailureListener(failureListener)

        continuation.invokeOnCancellation {
            removeListener(successListener)
            removeFailureListener(failureListener)
        }
    }

    companion object {
        private const val LOTTIE_FILE_URL =
            "https://airbnb.design/wp-content/themes/airbnbdesign/microsites/lottie/static/js/data.68f901f6b26434d7cee58361d3d5766e.json"
    }
}
