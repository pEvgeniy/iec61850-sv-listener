package ru.mpei.iec61850svlistener.service.parser;

import lombok.extern.slf4j.Slf4j;
import ru.mpei.iec61850svlistener.model.Packet;

import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class PacketParser {

    private final Map<String, Packet> elements = new HashMap<>();
    private final byte[] packetData;
    private int left = 0;

    public PacketParser(byte[] packetData) {
        this.packetData = packetData;
    }

    public Optional<Packet> parse(){
        Packet packet = new Packet();
//        MAC addresses
        packet.setDestination(getMACDestination().toString());
        packet.setSource(getMACSource().toString());

//        Protocol
        if (checkProtocol()) {
            packet.setProtocol("88ba");
        } else {
            packet.setProtocol("unknown");
        }

//        svID
        packet.setSvID(getSvID());

        packet.setSmpCnt(getSmpCnt());
//        System.out.println(packet.getSmpCnt());

//        Something
        left = left + 11;

//        Current and voltage
        double[] measurements = new double[8];
        for (int i = 0; i < measurements.length; i++) {
            measurements[i] = getValue();
        }
        packet.getA().setPhsA(measurements[0]);
        packet.getA().setPhsB(measurements[1]);
        packet.getA().setPhsC(measurements[2]);
        packet.getA().setNeut(measurements[3]);

        packet.getV().setPhsA(measurements[4]);
        packet.getV().setPhsB(measurements[5]);
        packet.getV().setPhsC(measurements[6]);
        packet.getV().setNeut(measurements[7]);

        if (packet.getSmpCnt() == elements.getOrDefault(packet.getSvID(), new Packet()).getSmpCnt()) {
            elements.put(packet.getSvID(), packet);
            System.out.println("ПОВТОР");
            return Optional.empty();
        }

        elements.put(packet.getSvID(), packet);

        if ("88ba".equals(packet.getProtocol())) {
            return Optional.of(packet);
        }
        return Optional.empty();
    }

    private StringBuilder getMACDestination() {
        StringBuilder destination = new StringBuilder();
        // First 6 bytes is MAC address of destination
        for (int i = 0; i < 6; i++) {
            destination.append(String.format("%02X%s", packetData[i], (i < 6 - 1) ? ":" : ""));
        }
        return destination;
    }

    private StringBuilder getMACSource() {
        StringBuilder source = new StringBuilder();
        // From 6 byte to 12 byte is MAC address of source
        for (int i = 6; i < 12; i++) {
            source.append(String.format("%02X%s", packetData[i], (i < 12 - 1) ? ":" : ""));
        }
        return source;
    }

    private boolean checkProtocol() {
        byte[] header = new byte[14];
        System.arraycopy(packetData, left, header, 0, 14);
        String protocol = bytesToHex(new byte[]{header[12], header[13]});
        if (!"88ba".equals(protocol)) {
            log.error("Wrong protocol, should be 0x88BA");
            return false;
//            throw new RuntimeException("Wrong protocol, should be 0x88BA");
        }
        left += 14;
        return true;
    }

    private String getSvID() {
        left = 33;
        byte svIDLength = packetData[32];
        byte[] svID = new byte[svIDLength];
        System.arraycopy(packetData, left, svID, 0, svIDLength);
        left += svIDLength;
        return new String(svID);
    }

    private int getSmpCnt() {
        left += 2;
        byte[] smpCnt = new byte[2];
        System.arraycopy(packetData, left, smpCnt, 0, smpCnt.length);
        left += 2;
        return rawDataToDec(smpCnt);
    }

    private int getValue() {
        byte[] phsMeas = new byte[4];
        System.arraycopy(packetData, left, phsMeas, 0, phsMeas.length);
        left += 8;
        return rawDataToDec(phsMeas);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(b & 0xff);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    public static int rawDataToDec(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.allocate(Integer.BYTES);
        buffer.put(bytes);
        buffer.rewind();
        return buffer.getInt();
    }
}
