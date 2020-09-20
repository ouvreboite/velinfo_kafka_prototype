package fr.velinfo.webapp;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StationListController {

    private StationService stationService;

    public StationListController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("")
    public String getStationList(Model model) {

        var stations = stationService.getStations();
        model.addAttribute("stations", stations);

        return "station-list";
    }

}