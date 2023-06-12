package ru.mpei.iec61850svlistener.service;


import lombok.extern.slf4j.Slf4j;
import ru.mpei.iec61850svlistener.model.Measurement;
import ru.mpei.iec61850svlistener.service.helper.PcapHelper;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

@Slf4j
public class PacketDetector {
    private final ScheduledExecutorService ses = Executors.newScheduledThreadPool(2);
    private final PcapHelper pcapHelper;
    private boolean discovering;
    private ScheduledFuture<?> discoveringTask;

    public PacketDetector(String interfaceName, int t) {
        pcapHelper = new PcapHelper(interfaceName, t);
    }

    public void startDiscovering(Map<String, Queue<Measurement>> instBufMap) {
        Listener listener = new Listener(instBufMap);
        if (!discovering) {
            discoveringTask = pcapHelper.startPacketsCapturing(listener, ses);
            discovering = true;
        } else {
            log.trace("Can not discover: already discovering");
        }
    }

    public void stopDiscovering() {
        if (discovering) {
            discoveringTask.cancel(true);
            discovering = false;
        }
    }
}
