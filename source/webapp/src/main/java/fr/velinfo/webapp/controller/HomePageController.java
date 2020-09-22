package fr.velinfo.webapp.controller;

import fr.velinfo.webapp.service.StationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomePageController {

    private final StationService stationService;

    public HomePageController(StationService stationService) {
        this.stationService = stationService;
    }

    @GetMapping("")
    public String getStationList(Model model) {

        var stations = stationService.getCurrentStationStates();
        model.addAttribute("stations", stations);

        return "home-page";
    }

}