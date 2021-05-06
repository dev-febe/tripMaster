package tourGuide.service;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import gpsUtil.GpsUtil;
import gpsUtil.location.Attraction;
import gpsUtil.location.Location;
import gpsUtil.location.VisitedLocation;
import tourGuide.helper.InternalTestHelper;
import tourGuide.tracker.Tracker;
import tourGuide.user.User;
import tourGuide.user.UserReward;
import tripPricer.Provider;
import tripPricer.TripPricer;

@Service
public class TourGuideService {
    private final Logger logger = LoggerFactory.getLogger(TourGuideService.class);

    private final GpsUtil gpsUtil;
    private final RewardsService rewardsService;

    public boolean testMode;

    private final TripPricer tripPricer = new TripPricer();
    public final Tracker tracker;

    @Autowired
    public TourGuideService(
            GpsUtil gpsUtil,
            RewardsService rewardsService,
            @Value("true") boolean testMode
    ) {
        this.gpsUtil = gpsUtil;
        this.rewardsService = rewardsService;
        this.testMode = testMode;

        if (testMode) {
            logger.info("TestMode enabled");
            logger.debug("Initializing users");
            initializeInternalUsers();
            logger.debug("Finished initializing users");
        }

        tracker = new Tracker(this);
        addShutDownHook();
    }

    /**
     * Get user rewards
     * This method returns the user rewards
     *
     * @param user
     * @return
     */
    public List<UserReward> getUserRewards(User user) {
        return user.getUserRewards();
    }

    /**
     * Get Location per user
     *
     * @param user
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public VisitedLocation getUserLocation(User user) throws ExecutionException, InterruptedException {
        return (user.getVisitedLocations().size() > 0) ?
                user.getLastVisitedLocation() :
                trackUserLocation(user).get();
    }

    /**
     * Get user by username
     * This method returns a user by a given username
     *
     * @param userName
     * @return
     */
    public User getUser(String userName) {
        return internalUserMap.get(userName);
    }

    /**
     * Get all internal users
     * This method returns the list of all user
     *
     * @return
     */
    public List<User> getAllUsers() {
        return new ArrayList<>(internalUserMap.values());
    }

    /**
     * Add new user to internal users
     * This method add new user to internal users list
     *
     * @param user
     */
    public void addUser(User user) {
        if (!internalUserMap.containsKey(user.getUserName())) {
            internalUserMap.put(user.getUserName(), user);
        }
    }

    /**
     * @param user
     * @return
     */
    public List<Provider> getTripDeals(User user) {
        int cumulativeRewardPoints = user.getUserRewards().stream().mapToInt(UserReward::getRewardPoints).sum();

        List<Provider> providers = tripPricer.getPrice(
                tripPricerApiKey,
                user.getUserId(),
                user.getUserPreferences().getNumberOfAdults(),
                user.getUserPreferences().getNumberOfChildren(),
                user.getUserPreferences().getTripDuration(),
                cumulativeRewardPoints
        );

        user.setTripDeals(providers);

        return providers;
    }

    /**
     * Track user location
     * This method updates the location and the rewards of the user
     *
     * @param user
     * @return VisitedLocation
     */
    public CompletableFuture<VisitedLocation> trackUserLocation(User user) {
        return CompletableFuture.supplyAsync(() -> {
            VisitedLocation visitedLocation = gpsUtil.getUserLocation(user.getUserId());
            user.addToVisitedLocations(visitedLocation);
            rewardsService.calculateRewards(user);
            return visitedLocation;
        });
    }

    /**
     * Get attractions near the user
     * This method return 5 attractions near the current user location
     *
     * @param visitedLocation
     * @return
     */
    public List<Attraction> getNearByAttractions(VisitedLocation visitedLocation) {
        List<Attraction> nearbyAttractions = new ArrayList<>();
        for (Attraction attraction : gpsUtil.getAttractions()) {
            if (rewardsService.isWithinAttractionProximity(attraction, visitedLocation.location)) {
                nearbyAttractions.add(attraction);
            }
        }

        return nearbyAttractions;
    }

    private void addShutDownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(tracker::stopTracking));
    }

    /**********************************************************************************
     *
     * Methods Below: For Internal Testing
     *
     **********************************************************************************/
    private static final String tripPricerApiKey = "test-server-api-key";
    // Database connection will be used for external users, but for testing purposes internal users are provided and stored in memory
    private final Map<String, User> internalUserMap = new HashMap<>();

    /**
     * This method initializes fake users
     */
    private void initializeInternalUsers() {
        IntStream.range(0, InternalTestHelper.getInternalUserNumber()).forEach(i -> {
            String userName = "internalUser" + i;
            String phone = "000";
            String email = userName + "@tourGuide.com";
            User user = new User(UUID.randomUUID(), userName, phone, email);
            generateUserLocationHistory(user);

            internalUserMap.put(userName, user);
        });
        logger.debug("Created " + InternalTestHelper.getInternalUserNumber() + " internal test users.");
    }

    /**
     * This method generates random location for specified user
     *
     * @param user
     */
    private void generateUserLocationHistory(User user) {
        IntStream.range(0, 3).forEach(i -> {
            user.addToVisitedLocations(new VisitedLocation(
                    user.getUserId(),
                    new Location(generateRandomLatitude(), generateRandomLongitude()), getRandomTime())
            );
        });
    }

    /**
     * This method generates random longitude
     *
     * @return
     */
    private double generateRandomLongitude() {
        double leftLimit = -180;
        double rightLimit = 180;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    /**
     * This method generates random latitude
     *
     * @return
     */
    private double generateRandomLatitude() {
        double leftLimit = -85.05112878;
        double rightLimit = 85.05112878;
        return leftLimit + new Random().nextDouble() * (rightLimit - leftLimit);
    }

    /**
     * This method generates random time
     *
     * @return
     */
    private Date getRandomTime() {
        LocalDateTime localDateTime = LocalDateTime.now().minusDays(new Random().nextInt(30));
        return Date.from(localDateTime.toInstant(ZoneOffset.UTC));
    }

}
