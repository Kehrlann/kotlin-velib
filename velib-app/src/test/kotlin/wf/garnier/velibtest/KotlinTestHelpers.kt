package wf.garnier.velibtest

import org.mockito.Mockito

fun <T> whenever(methodCall: T) = Mockito.`when`(methodCall)