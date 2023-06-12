package ru.mpei.iec61850svlistener.gui;


import ru.mpei.iec61850svlistener.model.Measurement;

public class Signal {
    private final String name;
    private final Measurement dataX, dataY;

    public Signal(String name, Measurement data) {
        this.name = name;
        this.dataX = null;
        this.dataY = data;
    }
    public Signal(String name, Measurement dataX, Measurement dataY) {
        this.name = name;
        this.dataX = dataX;
        this.dataY = dataY;
    }

    public String getName() { return name; }
    public Measurement getDataX() { return dataX; }
    public Measurement getDataY() { return dataY; }
}
