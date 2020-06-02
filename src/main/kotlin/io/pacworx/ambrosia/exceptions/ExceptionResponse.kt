package io.pacworx.ambrosia.exceptions

data class ExceptionResponse(
    val title: String,
    val message: String,
    val httpStatus: Int
) {
    constructor(exception: AmbrosiaException): this(exception.title, exception.message, exception.httpStatus.value())
}
