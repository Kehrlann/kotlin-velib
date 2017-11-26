package wf.garnier.velibtest.station

import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import org.springframework.ui.ExtendedModelMap
import org.springframework.ui.Model


@RunWith(SpringRunner::class)
@WebMvcTest
class StationControllerTest {

    @Autowired
    lateinit var mockMvc: MockMvc

    @MockBean
    lateinit var repo: StationRepository

    @Test
    fun `it should get all stations from the repo`() {
        val stations = listOf(Station(1, "test"), Station(2, "that"))
        `when`(repo.findAll()).thenReturn(stations)

        mockMvc.perform(get("/stations"))
                .andExpect(model().attribute("stations", stations))
                .andExpect(view().name("stations"))
    }

}