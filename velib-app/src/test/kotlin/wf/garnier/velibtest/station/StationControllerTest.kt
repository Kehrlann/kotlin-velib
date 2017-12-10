package wf.garnier.velibtest.station

import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.model
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.view
import wf.garnier.velibtest.whenever


@RunWith(SpringRunner::class)
@WebMvcTest
class StationControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var repo: StationRepository

    @Test
    fun `it should get all stations from the repo, sorted`() {
        val stations = listOf(Station(2, "that"), Station(1, "test"))
        whenever(repo.findAll()).thenReturn(stations)

        mockMvc.perform(get("/stations"))
                .andExpect(model().attribute("stations", stations.reversed()))
                .andExpect(view().name("stations"))
    }

}