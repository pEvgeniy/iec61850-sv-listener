package ru.mpei.iec61850svlistener.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Currents {
    private double phsA;
    private double phsB;
    private double phsC;
    private double neut;
}
