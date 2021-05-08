package tourGuide;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import org.javamoney.moneta.Money;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.client.GpsUtilClient;
import tourGuide.helper.InternalTestHelper;
import tourGuide.model.*;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;
import tripPricer.Provider;

import javax.money.Monetary;

import static org.junit.Assert.*;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestTourGuideService {

    @Autowired
    GpsUtilClient gpsUtilClient;

    @Autowired
    RewardsService rewardsService;

    @Autowired
    TourGuideService tourGuideService;

    @Test
    public void getUserLocation() throws ExecutionException, InterruptedException {
        InternalTestHelper.setInternalUserNumber(0);
        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).get();
        tourGuideService.tracker.stopTracking();
        assertEquals(visitedLocation.userId, user.getUserId());
    }

    @Test
    public void addUser() {
        InternalTestHelper.setInternalUserNumber(0);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        User retrievedUser1 = tourGuideService.getUser(user.getUserName());
        User retrievedUser2 = tourGuideService.getUser(user2.getUserName());

        tourGuideService.tracker.stopTracking();

        assertEquals(user, retrievedUser1);
        assertEquals(user2, retrievedUser2);
    }

    @Test
    public void getAllUsers() {
        InternalTestHelper.setInternalUserNumber(0);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        User user2 = new User(UUID.randomUUID(), "jon2", "000", "jon2@tourGuide.com");

        tourGuideService.addUser(user);
        tourGuideService.addUser(user2);

        List<User> allUsers = tourGuideService.getAllUsers();

        tourGuideService.tracker.stopTracking();

        assertEquals(allUsers.size(), 102);
    }

    @Test
    public void trackUser() throws ExecutionException, InterruptedException {
        InternalTestHelper.setInternalUserNumber(0);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).get();

        tourGuideService.tracker.stopTracking();

        assertEquals(user.getUserId(), visitedLocation.userId);
    }

    @Test
    public void getNearbyAttractions() throws ExecutionException, InterruptedException {
        InternalTestHelper.setInternalUserNumber(0);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");
        VisitedLocation visitedLocation = tourGuideService.trackUserLocation(user).get();

        List<Map<String, Object>> attractions = tourGuideService.getNearByAttractions(visitedLocation, user);

        tourGuideService.tracker.stopTracking();

        assertEquals(5, attractions.size());
    }

    @Test
    public void getTripDeals() {
        InternalTestHelper.setInternalUserNumber(0);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        List<Provider> providers = tourGuideService.getTripDeals(user);

        tourGuideService.tracker.stopTracking();

        assertEquals(5, providers.size());
    }

    @Test
    public void preferencesTest() {
        InternalTestHelper.setInternalUserNumber(0);

        User user = new User(UUID.randomUUID(), "jon", "000", "jon@tourGuide.com");

        Optional<String> userName = Optional.of("jon");
        UserPreferences userPreferences = new UserPreferences();
        userPreferences.setAttractionProximity(2147483647);
        userPreferences.setLowerPricePoint(Money.of(100, Monetary.getCurrency("USD")));
        userPreferences.setNumberOfAdults(1);
        userPreferences.setNumberOfChildren(1);
        userPreferences.setTicketQuantity(1);
        tourGuideService.addUser(user);
        tourGuideService.tracker.stopTracking();
        // add preferences
        assertNotNull(tourGuideService.addUserPreferences("jon", userPreferences));
        // get preferences
        assertNotNull(tourGuideService.getUserPreferences(userName));
    }

    @Test
    public void getAllCurrentLocations() {
        InternalTestHelper.setInternalUserNumber(100);
        List<User> allUsers = tourGuideService.getAllUsers();
        tourGuideService.tracker.stopTracking();
        assertEquals(allUsers.size(), 102);
    }
}
