package tourGuide.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import tourGuide.client.GpsUtilClient;
import tourGuide.client.RewardsClient;
import tourGuide.model.*;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private static final int ATTRACTION_PROXIMITY_RANGE = 200;
    private static final int DEFAULT_PROXIMITY_BUFFER = 10;

    // proximity in miles
    private int proximityBuffer = DEFAULT_PROXIMITY_BUFFER;
    private final GpsUtilClient gpsUtilClient;
    private final RewardsClient rewardsClient;

    @Autowired
    public RewardsService(GpsUtilClient gpsUtilClient, RewardsClient rewardsClient) {
        this.gpsUtilClient = gpsUtilClient;
        this.rewardsClient = rewardsClient;
    }

    /**
     * Setter for ${proximityBuffer}
     *
     * @param proximityBuffer
     */
    public void setProximityBuffer(int proximityBuffer) {
        this.proximityBuffer = proximityBuffer;
    }

    /**
     * This method calculates user rewards
     *
     * @param user
     */
    public void calculateRewards(User user) {
        CompletableFuture.runAsync(() -> {
            List<VisitedLocation> userLocations = user.getVisitedLocations();
            List<Attraction> attractions = gpsUtilClient.getAttractions();
            for (VisitedLocation visitedLocation : userLocations) {
                for (Attraction attraction : attractions) {
                    if (user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
                        if (nearAttraction(visitedLocation, attraction)) {
                            user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
                        }
                    }
                }
            }
        });

    }

    /**
     * This method checks if attraction is nearby
     *
     * @param attraction
     * @param location
     * @return
     */
    public boolean isWithinAttractionProximity(Attraction attraction, Location location) {
        return !(getDistance(attraction, location) > ATTRACTION_PROXIMITY_RANGE);
    }

    /**
     * This method checks if attraction is near the user location
     *
     * @param visitedLocation
     * @param attraction
     * @return
     */
    private boolean nearAttraction(VisitedLocation visitedLocation, Attraction attraction) {
        return !(getDistance(attraction, visitedLocation.location) > proximityBuffer);
    }

    /**
     * This method gets the calculated rewards points
     *
     * @param attraction
     * @param user
     * @return
     */
    public int getRewardPoints(Attraction attraction, User user) {
        return rewardsClient.getRewards(attraction.attractionId, user.getUserId());
    }

    /**
     * Get distance between 2 locations
     *
     * @param loc1
     * @param loc2
     * @return
     */
    public double getDistance(Location loc1, Location loc2) {
        double lat1 = Math.toRadians(loc1.latitude);
        double lon1 = Math.toRadians(loc1.longitude);
        double lat2 = Math.toRadians(loc2.latitude);
        double lon2 = Math.toRadians(loc2.longitude);

        double angle = Math.acos(Math.sin(lat1) * Math.sin(lat2)
                + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2));

        double nauticalMiles = 60 * Math.toDegrees(angle);
        return STATUTE_MILES_PER_NAUTICAL_MILE * nauticalMiles;
    }
}
