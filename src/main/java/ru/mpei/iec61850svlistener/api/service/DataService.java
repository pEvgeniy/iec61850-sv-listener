package ru.mpei.iec61850svlistener.api.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mpei.iec61850svlistener.api.manager.DataManager;
import ru.mpei.iec61850svlistener.exception.NullMeasurementValueException;
import ru.mpei.iec61850svlistener.model.Measurement;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Slf4j
@Service
public class DataService {
    DataManager dataManager;

    @Autowired
    public DataService(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public Map<String, double[]> findLastMeasurements() {
        Map<String, double[]> result = new HashMap<>();
        for (Map.Entry<String, Queue<Measurement>> signal : dataManager.getInstBufMap().entrySet()) {
            double[] firstMeasurements = new double[80];
            for (int i = 0; i < firstMeasurements.length; i++) {
                Measurement value = signal.getValue().poll();
                try {
                    firstMeasurements[i] = value.getValue();
                } catch (NullPointerException e) {
                    throw new NullMeasurementValueException("Measured value is null");
                }
            }
            result.put(signal.getKey(), firstMeasurements);
        }
        return result;
    }
}
