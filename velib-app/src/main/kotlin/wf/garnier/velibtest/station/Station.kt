package wf.garnier.velibtest.station

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Station(@Id val id: Long = 0L, val name: String = "")
