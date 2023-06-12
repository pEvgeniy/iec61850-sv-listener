package ru.mpei.iec61850svlistener.api.manager;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.mpei.iec61850svlistener.model.Measurement;
import ru.mpei.iec61850svlistener.service.Listener;
import ru.mpei.iec61850svlistener.service.PacketDetector;

import java.util.HashMap;
import java.util.Map;
import java.util.Queue;

@Component
public class DataManager {
    PacketDetector agentDetector;
    Map<String, Queue<Measurement>> instBufMap = new HashMap<>();

    public Map<String, Queue<Measurement>> getInstBufMap() {
        return instBufMap;
    }

    public boolean startListening() {
        agentDetector = new PacketDetector("\\Device\\NPF_{117B20F1-7BE1-4265-9015-188008C10FF5}", 1000);
        agentDetector.startDiscovering(instBufMap);
        return true;
    }

    public boolean stopListening() {
        agentDetector.stopDiscovering();
        return true;
    }
}
