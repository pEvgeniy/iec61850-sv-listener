package ru.mpei.iec61850svlistener.service;


import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.PacketListener;
import ru.mpei.iec61850svlistener.model.Measurement;
import ru.mpei.iec61850svlistener.model.Packet;
import ru.mpei.iec61850svlistener.service.filter.FourierFilter;
import ru.mpei.iec61850svlistener.service.parser.PacketParser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

@Slf4j
public class Listener implements PacketListener {

    @Getter
    private final Map<String, Queue<Measurement>> instBufMap;
    private final Map<String, Measurement> fMeasurements = new HashMap<>();
    private final Map<String, FourierFilter> filters = new HashMap<>();
    private int counter = 0;

    public Listener(Map<String, Queue<Measurement>> instBufMap) {
        this.instBufMap = instBufMap;
    }

    @SneakyThrows
    @Override
    public void gotPacket(org.pcap4j.packet.Packet packet) {
        byte[] rawData = packet.getRawData();
        PacketParser msgParser = new PacketParser(rawData);
        Optional<Packet> element = msgParser.parse();
        if (element.isPresent()) {
            memorizeMeasurement(element.get());
//            createGraph(element.get());
//            filterA(element.get());
//            checkCurrent(1000);
        }
    }

    private void memorizeMeasurement(Packet packet) {
        Measurement measurement = new Measurement(packet.getA().getPhsA());
        if (!instBufMap.containsKey(packet.getSvID())) {
            instBufMap.put(packet.getSvID(), new LinkedList<>());
        }
        instBufMap.get(packet.getSvID()).add(measurement);
    }

//    private void createGraph(Packet packet) {
//        if (measurements.get(packet.getSvID()) == null) {
//            Measurement measurement = new Measurement(packet.getA().getPhsA());
//            measurements.put(packet.getSvID(), measurement);
//            if (!instBufMap.containsKey(packet.getSvID())) {
//                instBufMap.put(packet.getSvID(), new LinkedList<>());
//            }
//            instBufMap.get(packet.getSvID()).add(measurement);
//            graph.addSignals(new Signal(packet.getSvID(), measurement));
////            log.debug("New signal {} added", packet.getSvID());
//            return;
//        }
//        measurements.get(packet.getSvID()).setValue(packet.getA().getPhsA());
//        graph.process();
//    }

//    private void filterA(Packet packet) {
//        if (filters.get(packet.getSvID()) == null) {
//            FourierFilter filter = new FourierFilter();
//            filters.put(packet.getSvID(), filter);
//            Measurement measurement = new Measurement(filter.process(packet));
//            fMeasurements.put(packet.getSvID(), measurement);
//            graphF.addSignals(new Signal(packet.getSvID(), measurement));
//            log.info("New signal {} added", packet.getSvID());
//            return;
//        }
//        fMeasurements.get(packet.getSvID()).setValue(filters.get(packet.getSvID()).process(packet));
////        filters.get(element.getSvID()).process(element);
//        graphF.process();
//    }

    private void checkCurrent(double bound) {
        for (Map.Entry<String, Measurement> measurement : fMeasurements.entrySet()) {
            if (counter > 10000) {
                counter = 0;
            }
            if (measurement.getValue().getValue() > bound && counter == 0) {
                log.warn("Short circuit on {}", measurement.getKey());
            }
            counter++;
        }
    }

    private void getLogs(Packet packet) {
        log.info("MAC address of destination: " + packet.getDestination());
        log.info("MAC address of source: " + packet.getSource());

        log.info("svID = {}", packet.getSvID());

        log.info("phsMeas Ia = {}", packet.getA().getPhsA() / 1000);
        log.info("phsMeas Ib = {}", packet.getA().getPhsB() / 1000);
        log.info("phsMeas Ic = {}", packet.getA().getPhsC() / 1000);
        log.info("phsMeas In = {}", packet.getA().getNeut() / 1000);

        log.info("phsMeas Ua = {}", packet.getV().getPhsA() / 1000);
        log.info("phsMeas Ub = {}", packet.getV().getPhsB() / 1000);
        log.info("phsMeas Uc = {}", packet.getV().getPhsC() / 1000);
        log.info("phsMeas Un = {}", packet.getV().getNeut() / 1000);
    }
}
