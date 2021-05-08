package tourGuide.controller;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import tourGuide.model.*;
import tourGuide.service.TourGuideService;

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
     * @param userName username of user
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
        User user = tourGuideService.getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
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
        User user = tourGuideService.getUser(userName);
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(user);
        return tourGuideService.getNearByAttractions(visitedLocation, user);
    }

    @RequestMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        User user = tourGuideService.getUser(userName);
        return tourGuideService.getUserRewards(user);
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
     *
     * @param userName
     * @return
     */
    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        User user = tourGuideService.getUser(userName);
        return tourGuideService.getTripDeals(user);
    }
}
