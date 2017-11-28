import io.reactivex.Flowable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import wf.garnier.velibtest.extensions.waitAll
import java.util.concurrent.TimeUnit

class RxJavaExtensionsTest {

    @Test
    fun `waitAll should wait for all flowables to complete`() {
        val allPromises = listOf(Flowable.just(1), Flowable.just(2).delay(100, TimeUnit.MILLISECONDS))
                .waitAll()

        assertThat(allPromises).isEqualTo(listOf(1, 2))
    }
}