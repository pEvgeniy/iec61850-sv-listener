package ru.mpei.iec61850svlistener.service.helper;

import com.sun.jna.NativeLibrary;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.pcap4j.core.*;

import java.util.List;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
public class PcapHelper {

    static {
        if (System.getProperty("os.name").toLowerCase().contains("win")) {
            NativeLibrary.addSearchPath("wpcap", "C:\\Windows\\System32\\Npcap");
        }
    }
    private final PcapHandle pcapHandle;

    /**
     * @param ifaceName - interface name, which is used to capture packets
     * @param T - time period to get packets from network card buffer in ms (use 50 - 100 ms)
     */
    public PcapHelper(String ifaceName, int T) {
        pcapHandle = openIFaceToCapturePackets(ifaceName, T);
    }

    /**
     * @param pl - Builder.Packet listener, called when required UDP packet received. it must contain logic to handle new data item
     * @param ses - executer to use to handle packets. NOTE : it occupies a thread
     * @return - task of packets capturing
     * @throws PcapNativeException  if pcap can not open found interface handle
     */
    public ScheduledFuture<?> startPacketsCapturing(PacketListener pl, ScheduledExecutorService ses) {
        return startCapturingTask(pl, ses, pcapHandle);

    }

    private ScheduledFuture<?> startCapturingTask(PacketListener pl, ScheduledExecutorService ses, PcapHandle pcapHandle) {
        String filterExpression = "ether proto 0x88BA";

        return ses.schedule(() -> {
            try {
                pcapHandle.setFilter(filterExpression, BpfProgram.BpfCompileMode.NONOPTIMIZE);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("error during filter applying {}", e.toString());
            }
            try {
                pcapHandle.loop(8000, pl);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("error during grabbing packets{}", e.toString());
            }
        }, 0, TimeUnit.MILLISECONDS);
    }

    @SneakyThrows
    private PcapHandle openIFaceToCapturePackets(String ifaceName, int T) {
        List<PcapNetworkInterface> foundIfaces;
        try {
            foundIfaces = Pcaps.findAllDevs();
        } catch (PcapNativeException e) {
            log.error("lib pcap works incorrect. unable to find active ethernet interfaces");
            throw new RuntimeException("can not call function find all Devs from pcap lib");
        }

        if (foundIfaces == null || foundIfaces.size() == 0) {
            log.error("libPcap could not discover ethernet interfaces");
            throw new RuntimeException("libPcap could not discover ethernet interfaces");
        }

        PcapNetworkInterface chosenIface = null;
        for (PcapNetworkInterface ifs : foundIfaces) {
            if (ifs.getName().equals(ifaceName)) {
                chosenIface = ifs;
                break;
            }
        }
        if (chosenIface == null) {
            log.error("Failed to find iface with name{}  ethernet available interface are : {}", ifaceName, foundIfaces.stream().map(el -> el.getName() + el.getAddresses()).collect(Collectors.toList()));
            throw new RuntimeException("Can not find iface with given name "+ ifaceName);
        }

        return chosenIface.openLive(65536, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, T / 2);
    }
}