package wf.garnier.velibtest.extensions

import io.reactivex.Flowable
import org.springframework.http.ResponseEntity
import org.springframework.web.client.AsyncRestTemplate

fun <T> AsyncRestTemplate.getFlowable(uri: String, returnType: Class<T>): Flowable<ResponseEntity<T>> =
        Flowable.fromFuture(this.getForEntity(uri, returnType))