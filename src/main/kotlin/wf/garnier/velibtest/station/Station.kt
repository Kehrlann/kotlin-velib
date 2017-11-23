package wf.garnier.velibtest.station

import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class Station(@Id val id: Long, val nom: String, val isOpen: Boolean = true, val isConnected: Boolean = true)