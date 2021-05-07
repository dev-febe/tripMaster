package tourGuide.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "gpsUtil", url = "localhost:8083")
public interface GpsUtilClient {
    @GetMapping("/getAttractions")
    List<Attraction> getAttractions();

    @GetMapping("/getUserLocation")
    VisitedLocation getUserLocation(@RequestParam UUID userId);
}
