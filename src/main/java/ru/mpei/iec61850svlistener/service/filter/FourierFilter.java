package ru.mpei.iec61850svlistener.service.filter;

import ru.mpei.iec61850svlistener.model.Packet;

public class FourierFilter {
    public static int bSize = 80;
    private final double[] bufferX = new double[bSize];
    private final double[] bufferY = new double[bSize];
    private int bCount = 0;
    private double x = 0d;
    private double y = 0d;
    private final double k =(double) 2/bSize;

    public double process(Packet packet) {
        double value = packet.getA().getPhsA();

        x += k * value * Math.sin((2 * Math.PI * 50) * (0.02/bSize) * bCount) - bufferX[bCount];
        y += k * value * Math.cos((2 * Math.PI * 50) * (0.02/bSize) * bCount) - bufferY[bCount];

        bufferX[bCount] = (k * value * Math.sin((2 * Math.PI * 50) * (0.02/bSize) * bCount));
        bufferY[bCount] = (k * value * Math.cos((2 * Math.PI * 50) * (0.02/bSize) * bCount));

        bCount++;
        if(bCount >= bSize) {
            bCount = 0;
        }

        return Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

}
