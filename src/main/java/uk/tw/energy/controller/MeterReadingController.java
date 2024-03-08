package uk.tw.energy.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.tw.energy.domain.ElectricityReading;
import uk.tw.energy.domain.MeterReadings;
import uk.tw.energy.service.MeterReadingService;

import java.util.List;
import java.util.Optional;

@RequestMapping("/readings")

public class MeterReadingController {

    private final MeterReadingService meterReadingService;
    private final static Logger logger = LoggerFactory.getLogger(MeterReadingController.class);


    public MeterReadingController(MeterReadingService meterReadingService) {
        this.meterReadingService = meterReadingService;
    }

    @PostMapping("/store")
    public ResponseEntity<String> storeReadings(@RequestBody MeterReadings meterReadings) {
        String logMessage = "Received meter readings: " + meterReadings;
        logger.info(logMessage);
        if (!isMeterReadingsValid(meterReadings)) {
            logMessage = "Invalid meter readings: " + meterReadings;
            logger.warn(logMessage);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(logMessage);
        }
        meterReadingService.storeReadings(meterReadings.smartMeterId(), meterReadings.electricityReadings());
        logMessage = "Data logged successfully for meter readings: " + meterReadings;
        return ResponseEntity.ok().body(logMessage);
    }

    private boolean isMeterReadingsValid(MeterReadings meterReadings) {
        String smartMeterId = meterReadings.smartMeterId();
        List<ElectricityReading> electricityReadings = meterReadings.electricityReadings();
        boolean isValid = smartMeterId != null && !smartMeterId.isEmpty()
                && electricityReadings != null && !electricityReadings.isEmpty();
        if (!isValid) {
            logger.warn("Invalid meter readings: {}", meterReadings);
        }
        return isValid;
    }


    @GetMapping("/read/{smartMeterId}")
    public ResponseEntity<?> readReadings(@PathVariable String smartMeterId) {
        logger.info("Received request to get readings for meter: {}", smartMeterId);
        Optional<List<ElectricityReading>> readings = meterReadingService.getReadings(smartMeterId);
        if (!readings.isPresent()) {
            logger.warn("No readings found for meter {}", smartMeterId);
            return ResponseEntity.notFound().build();
        }
        logger.info("Found readings for meter {}: {}", smartMeterId, readings.get());
        return ResponseEntity.ok(readings.get());
    }

}
