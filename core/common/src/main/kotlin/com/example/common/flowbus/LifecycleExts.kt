package com.example.common.flowbus

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.Job

class LifecycleJob(private val job: Job) : DefaultLifecycleObserver {
    override fun onDestroy(owner: LifecycleOwner) {
        if (!job.isCancelled) {
            job.cancel()
        }
    }
}

fun Job.bindToLifecycle(lifecycle: Lifecycle) {
    lifecycle.addObserver(LifecycleJob(this))
}