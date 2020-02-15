package ir.carpino.tracker.controller;

import ir.carpino.tracker.service.MysqlPersister;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdministrationController {

    private MysqlPersister persister;

    @Autowired
    public AdministrationController(MysqlPersister persister) {
        this.persister = persister;
    }

    @PutMapping("/v1/master-state")
    public void setMasterState(@RequestBody Boolean state) {
        persister.persistTrackerData = state;
    }

    @GetMapping("/v1/master-state")
    public boolean getMasterState() {
        return persister.persistTrackerData;
    }
}
