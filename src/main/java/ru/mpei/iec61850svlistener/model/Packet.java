package ru.mpei.iec61850svlistener.model;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Packet {
    private String destination;
    private String source;
    private String protocol;
    private String svID;
    private int smpCnt;
    private Currents A = new Currents();
    private Currents bufferedA = new Currents();
    private Voltages V = new Voltages();
}
