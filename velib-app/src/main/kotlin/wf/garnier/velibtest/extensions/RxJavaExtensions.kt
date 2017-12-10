package wf.garnier.velibtest.extensions

import io.reactivex.Flowable

fun <T> Collection<Flowable<T>>.waitAll(): Collection<T> =
        Flowable.zip(this, { it })
                .blockingLast()
                .map { it as T }