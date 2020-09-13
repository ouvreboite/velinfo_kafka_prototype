package fr.velinfo.webapp;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import fr.velinfo.avro.record.stream.AvroStationUpdate;

import java.util.List;

@Controller
public class StationListController {

    @Autowired
    private StationService stationService;

    @Autowired
    private AvroJsonMapper avroJsonMapper;

    @GetMapping("")
    public String getStationList(Model model) throws JsonProcessingException {

        List<AvroStationUpdate> stations = stationService.getStations();
        model.addAttribute("stations", stations);

        String stationsJs = avroJsonMapper.serializeStations(stations);
        model.addAttribute("stationsJs", stationsJs);

        return "station-list";
    }

}