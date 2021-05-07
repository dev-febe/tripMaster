package GpsUtil.controller;

import GpsUtil.service.GpsUtilService;
import gpsUtil.location.Attraction;
import gpsUtil.location.VisitedLocation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class GpsUtilController {

    GpsUtilService gpsUtilService;

    GpsUtilController(GpsUtilService gpsUtilService) {
        this.gpsUtilService = gpsUtilService;
    }

    @GetMapping("/getAttractions")
    public List<Attraction> getAttractions() {
        return gpsUtilService.getAttractions();
    }

    @GetMapping("/getUserLocation")
    public VisitedLocation getUserLocation(@RequestParam UUID userId) {
        return gpsUtilService.getUserLocation(userId);
    }
}
