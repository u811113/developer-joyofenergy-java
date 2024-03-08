package uk.tw.energy.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import uk.tw.energy.domain.ElectricityReading;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service

public class MeterReadingService {

    private final Map<String, List<ElectricityReading>> meterAssociatedReadings;
    private final static Logger logger = LoggerFactory.getLogger(MeterReadingService.class);


    public MeterReadingService(Map<String, List<ElectricityReading>> meterAssociatedReadings) {
        this.meterAssociatedReadings = meterAssociatedReadings;
    }

    public Optional<List<ElectricityReading>> getReadings(String smartMeterId) {
        return Optional.ofNullable(meterAssociatedReadings.get(smartMeterId));
    }

    public ResponseEntity<String> storeReadings(String smartMeterId, List<ElectricityReading> electricityReadings) {
        logger.info("Storing readings for meter: {}", smartMeterId);
        if (!meterAssociatedReadings.containsKey(smartMeterId)) {
            logger.info("No previous readings for this meter, creating a new list.");
            meterAssociatedReadings.put(smartMeterId, new ArrayList<>());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("No reading information");
        }
        meterAssociatedReadings.get(smartMeterId).addAll(electricityReadings);
        logger.info("Readings stored successfully for meter: {}", smartMeterId);
        return ResponseEntity.ok("Reading stored successfully");
    }
}
