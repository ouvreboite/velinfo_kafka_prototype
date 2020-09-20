package fr.velinfo.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StationListController {

    @Autowired
    private StationService stationService;

    @GetMapping("")
    public String getStationList(Model model) {

        var stations = stationService.getStations();
        model.addAttribute("stations", stations);

        return "station-list";
    }

}