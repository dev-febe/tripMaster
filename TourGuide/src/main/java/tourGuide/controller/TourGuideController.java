package tourGuide.controller;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.jsoniter.output.JsonStream;

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

    //  TODO: Change this method to no longer return a List of Attractions.
    //  Instead: Get the closest five tourist attractions to the user - no matter how far away they are.
    //  Return a new JSON object that contains:
    //  Name of Tourist attraction,
    //  Tourist attractions lat/long,
    //  The user's location lat/long,
    //  The distance in miles between the user's location and each of the attractions.
    //  The reward points for visiting each Attraction.
    //  Note: Attraction reward points can be gathered from RewardsCentral
    @RequestMapping("/getNearbyAttractions")
    public List<Attraction> getNearbyAttractions(@RequestParam String userName) throws ExecutionException, InterruptedException {
        VisitedLocation visitedLocation = tourGuideService.getUserLocation(getUser(userName));
        return tourGuideService.getNearByAttractions(visitedLocation);
    }

    @RequestMapping("/getRewards")
    public List<UserReward> getRewards(@RequestParam String userName) {
        return tourGuideService.getUserRewards(getUser(userName));
    }

    @RequestMapping("/getAllCurrentLocations")
    public String getAllCurrentLocations() {
        // TODO: Get a list of every user's most recent location as JSON
        //- Note: does not use gpsUtil to query for their current location,
        //        but rather gathers the user's current location from their stored location history.
        //
        // Return object should be the just a JSON mapping of userId to Locations similar to:
        //     {
        //        "019b04a9-067a-4c76-8817-ee75088c3822": {"longitude":-48.188821,"latitude":74.84371}
        //        ...
        //     }

        return JsonStream.serialize("");
    }

    @RequestMapping("/getTripDeals")
    public List<Provider> getTripDeals(@RequestParam String userName) {
        return  tourGuideService.getTripDeals(getUser(userName));
    }

    private User getUser(String userName) {
        return tourGuideService.getUser(userName);
    }
}
