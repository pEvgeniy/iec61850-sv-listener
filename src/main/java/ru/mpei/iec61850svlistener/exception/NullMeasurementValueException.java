package ru.mpei.iec61850svlistener.exception;

public class NullMeasurementValueException extends RuntimeException {
    public NullMeasurementValueException(String message) {
        super(message);
    }
}
