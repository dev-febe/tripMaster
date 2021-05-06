package tourGuide.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import rewardCentral.RewardCentral;
import tourGuide.user.User;
import tourGuide.user.UserReward;

@Service
public class RewardsService {
    private static final double STATUTE_MILES_PER_NAUTICAL_MILE = 1.15077945;
    private static final int ATTRACTION_PROXIMITY_RANGE = 200;
    private static final int DEFAULT_PROXIMITY_BUFFER = 10;

	// proximity in miles
	private int proximityBuffer = DEFAULT_PROXIMITY_BUFFER;
	private final GpsUtil gpsUtil;
	private final RewardCentral rewardsCentral;

	@Autowired
	public RewardsService(GpsUtil gpsUtil, RewardCentral rewardCentral) {
		this.gpsUtil = gpsUtil;
		this.rewardsCentral = rewardCentral;
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
	 * @param user
	 */
	public void calculateRewards(User user) {
		List<VisitedLocation> userLocations = user.getVisitedLocations();
		List<Attraction> attractions = gpsUtil.getAttractions();
		
		for(VisitedLocation visitedLocation : userLocations) {
			for(Attraction attraction : attractions) {
				if(user.getUserRewards().stream().noneMatch(r -> r.attraction.attractionName.equals(attraction.attractionName))) {
					if(nearAttraction(visitedLocation, attraction)) {
						user.addUserReward(new UserReward(visitedLocation, attraction, getRewardPoints(attraction, user)));
					}
				}
			}
		}
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
	private int getRewardPoints(Attraction attraction, User user) {
		return rewardsCentral.getAttractionRewardPoints(attraction.attractionId, user.getUserId());
	}

	/**
	 * Get distance between 2 locations
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
