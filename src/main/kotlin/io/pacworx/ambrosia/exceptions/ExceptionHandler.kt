package io.pacworx.ambrosia.exceptions

import io.pacworx.ambrosia.player.AuditLogService
import mu.KotlinLogging
import org.apache.commons.lang3.exception.ExceptionUtils
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@ControllerAdvice
class ExceptionHandler(
    private val auditLogService: AuditLogService
) : ResponseEntityExceptionHandler() {

    private val log = KotlinLogging.logger {}

    @ExceptionHandler(RuntimeException::class)
    fun handleRuntimeException(exception: RuntimeException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(action = "${exception.message}: ${ExceptionUtils.getRootCauseMessage(exception)}")
        log.error(exception.message, exception)
        return ResponseEntity(ExceptionResponse("Internal Server Error", exception.message ?: "no message", HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(GeneralException::class)
    fun handleGeneralException(exception: GeneralException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(exception.player.id, exception.message)
        log.warn("Player ${exception.player.name} #${exception.player.id} caused a general exception: ${exception.message}")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotFoundException(exception: EntityNotFoundException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(exception.player.id, exception.message)
        log.warn("Player ${exception.player.name} #${exception.player.id} requested an unknown ${exception.entityName} with id ${exception.entityId}")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(OngoingBattleException::class)
    fun handleOngoingBattleException(exception: OngoingBattleException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(exception.player.id, exception.message)
        log.warn("Player ${exception.player.name} #${exception.player.id} tried to start a new battle while there is still one ongoing.")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(MapTileActionException::class)
    fun handleMapTileActionException(exception: MapTileActionException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(exception.player.id, exception.message)
        log.warn("Player ${exception.player.name} #${exception.player.id} tried to ${exception.action} map #${exception.mapId} ${exception.posX}x${exception.posY}")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(UnauthenticatedException::class)
    fun handleUnauthenticatedException(exception: UnauthenticatedException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(action = exception.message)
        log.warn(exception.message)
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(UnauthorizedException::class)
    fun handleUnauthorizedException(exception: UnauthorizedException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(exception.player.id, exception.message)
        log.warn("Player ${exception.player.name} #${exception.player.id} tried to execute an action he is not allowed for: ${exception.message}")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(ConfigurationException::class)
    fun handleConfigurationException(exception: ConfigurationException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(action = exception.message)
        log.warn("Configuration exception occurred: ${exception.message}")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(HeroBusyException::class)
    fun handleHeroBusyException(exception: HeroBusyException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(exception.player.id, exception.message)
        log.warn("Player ${exception.player.name} #${exception.player.id} tried to use a busy hero: ${exception.message}")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(VehicleBusyException::class)
    fun handleVehicleBusyException(exception: VehicleBusyException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(exception.player.id, exception.message)
        log.warn("Player ${exception.player.name} #${exception.player.id} tried to use a busy vehicle: ${exception.message}")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }

    @ExceptionHandler(InsufficientResourcesException::class)
    fun handleInsufficientResourcesException(exception: InsufficientResourcesException): ResponseEntity<ExceptionResponse> {
        auditLogService.logError(exception.playerId, exception.message)
        log.warn("Player #${exception.playerId} tried to spend ${exception.amount} ${exception.resource} but had not enough")
        return ResponseEntity(ExceptionResponse(exception), exception.httpStatus)
    }
}
