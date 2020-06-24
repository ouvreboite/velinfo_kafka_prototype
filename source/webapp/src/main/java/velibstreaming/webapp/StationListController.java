package velibstreaming.webapp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StationListController {

    @Autowired
    private StationService businessCache;

    @GetMapping("")
    public String getStationList(Model model) {
        model.addAttribute("stations", businessCache.getStations());
        return "station-list";
    }
}