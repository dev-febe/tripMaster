package tourGuide.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import gpsUtil.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import gpsUtil.location.VisitedLocation;

import tourGuide.service.TourGuideService;
import tourGuide.user.User;
import tourGuide.user.UserPreferences;

import tourGuide.user.UserReward;
import tripPricer.Provider;

@RestController
public class TourGuideController {
    TourGuideService tourGuideService;

    @Autowired
    TourGuideController(TourGuideService tourGuideService) {
        this.tourGuideService = tourGuideService;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from TourGuide!";
    }

    /**
     * Endpoint: /userPreferences?userName={userName}
     * Desc: Add user preferences to user
     *
     * @param userName
     * @param userPreferences
     * @return
     */
    @PostMapping("/addUserPreferences")
    public UserPreferences addUserPreferences(@RequestParam String userName, @RequestBody UserPreferences userPreferences) {
        return tourGuideService.addUserPreferences(userName, userPreferences);
    }

    /**
     * Endpoint:  /userPreferences?userName={userName}
     * Desc: Get specific user preferences from a user
     *
     * @param userName
     * @return
     */
    @GetMapping("/getUserPreferences")
    public Object getUserPreferences(@RequestParam Optional<String> userName) {
        return tourGuideService.getUserPreferences(userName);
    }

    /**
     * Endpoint: /getLocation?userName={userName}
     * Desc: Get Location of specified user
     *
     * @param userName
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping("/getLocation")
    public Location getLocation(@RequestParam String userName) throws ExecutionException, InterruptedException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return visitedLocation.location;
    }


    /**
     * Endpoint: /getNearbyAttractions?userName={userName}
     * Desc: Get the closest five tourist attractions to the user
     *
     * @param userName
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    @RequestMapping("/getNearbyAttractions")
    public List<Map<String, Object>> getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
        User user = getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getNearByAttractions(visitedLocation, user);
    }

    @RequestMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return tourGuideService.getUserRewards(getUser(userName));
    }

    /**
     * Endpoint: /getAllCurrentLocations
     * Desc: Get a list of every user's most recent location as JSON
     *
     * @return
     */
    @RequestMapping("/getAllCurrentLocations")
    public Map<UUID, Location> getAllCurrentLocations() {
        return tourGuideService.getAllCurrentLocations();
    }

    /**
     * Endpoint: /getTripDeals
     * Desc: Get trip deals
     * @param userName
     * @return
     */
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        return tourGuideService.getTripDeals(getUser(userName));
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }
}
