package io.pacworx.ambrosia.maps

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import javax.transaction.Transactional

@Service
class MapResetIntervalScheduler(
    private val mapRepository: MapRepository
) {

    @Scheduled(initialDelay = 5000, fixedDelay = 1800000)
    @Transactional
    fun resetMaps() {
        mapRepository.findAllByIntervalToIsBefore().forEach { map ->
            if (map.resetIntervalHours != null) {
                map.intervalFrom = map.intervalTo
                map.intervalTo = map.intervalFrom!!.plusHours(map.resetIntervalHours.toLong())
            }
        }
    }
}