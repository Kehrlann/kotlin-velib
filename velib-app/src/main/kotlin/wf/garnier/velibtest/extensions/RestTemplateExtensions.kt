package wf.garnier.velibtest.extensions

import io.reactivex.Flowable
import org.springframework.http.ResponseEntity
import org.springframework.util.concurrent.ListenableFuture
import org.springframework.util.concurrent.ListenableFutureCallback
import org.springframework.web.client.AsyncRestTemplate
import java.util.concurrent.CompletableFuture

fun <T> AsyncRestTemplate.getFlowable(uri: String, returnType: Class<T>): Flowable<ResponseEntity<T>> =
        Flowable.fromFuture(this.getForEntity(uri, returnType))


fun <T> ListenableFuture<ResponseEntity<T>>.toCompletable(): CompletableFuture<T> {
    val completableFuture = CompletableFuture<T>()

    this.addCallback(object: ListenableFutureCallback<ResponseEntity<T>> {
        override fun onFailure(ex: Throwable) {
            completableFuture.completeExceptionally(ex)
        }

        override fun onSuccess(result: ResponseEntity<T>) {
            completableFuture.complete(result.body)
        }
    })

    return completableFuture
}