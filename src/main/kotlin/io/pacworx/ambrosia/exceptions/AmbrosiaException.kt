package io.pacworx.ambrosia.exceptions

import org.springframework.http.HttpStatus

interface AmbrosiaException {
    val title: String
    val message: String
    val httpStatus: HttpStatus
}
