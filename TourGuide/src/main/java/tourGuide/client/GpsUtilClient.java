package tourGuide.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tourGuide.model.Attraction;
import tourGuide.model.VisitedLocation;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "gpsUtil", url = "${client.gpsUtil.url}")
public interface GpsUtilClient {
    /**
     * Endpoint: GET /getAttractions
     * Desc: Get all attractions
     *
     * @return
     */
    @GetMapping("/getAttractions")
    List<Attraction> getAttractions();

    /**
     * Endpoint: /getUserLocation
     * Desc: Get locations by specific user
     *
     * @param userId Uuid of the user
     * @return
     */
    @GetMapping("/getUserLocation")
    VisitedLocation getUserLocation(@RequestParam UUID userId);
}
