package ru.mpei.iec61850svlistener.api.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mpei.iec61850svlistener.api.manager.DataManager;
import ru.mpei.iec61850svlistener.api.service.DataService;

import java.util.Map;

@RestController
@RequestMapping("/sv-parser")
public class DataController {
    DataManager dataManager;
    DataService dataService;

    @Autowired
    public DataController(DataManager dataManager, DataService dataService) {
        this.dataManager = dataManager;
        this.dataService = dataService;
    }

    @GetMapping("/home")
    public String homePage() {
        return "HOME PAGE";
    }

    @GetMapping("/start-listening")
    public boolean startListening() {
        return dataManager.startListening();
    }

    @GetMapping("/stop-listening")
    public boolean stopListening() {
        return dataManager.stopListening();
    }


    @GetMapping("/measurements")
    public Map<String, double[]> findFirstMeasurements() {
        return dataService.findLastMeasurements();
    }
}
