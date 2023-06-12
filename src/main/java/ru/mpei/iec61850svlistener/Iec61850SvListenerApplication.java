package ru.mpei.iec61850svlistener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import ru.mpei.iec61850svlistener.service.PacketDetector;

@SpringBootApplication
public class Iec61850SvListenerApplication {

    public static void main(String[] args) {
        SpringApplication.run(Iec61850SvListenerApplication.class, args);
    }

}
