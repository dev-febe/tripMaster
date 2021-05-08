package tourGuide;

import static org.junit.Assert.*;

import java.util.*;

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

@SpringBootTest
@RunWith(SpringRunner.class)
public class TestRewardsService {
    @Autowired
    GpsUtilClient gpsUtilClient;

    @Autowired
    RewardsService rewardsService;

    @Autowired()
    TourGuideService tourGuideService;

    @Test
    public void userGetRewards() {
        User user = tourGuideService.getAllUsers().get(1);
        tourGuideService.trackUserLocation(user);
        List<UserReward> userRewards = user.getUserRewards();
        tourGuideService.tracker.stopTracking();
        assertEquals(1, userRewards.size());
    }

    @Test
    public void isWithinAttractionProximity() {
        Attraction attraction = gpsUtilClient.getAttractions().get(0);
        assertTrue(rewardsService.isWithinAttractionProximity(attraction, attraction));
    }

    @Test
    public void nearAllAttractions() {
        rewardsService.setProximityBuffer(Integer.MAX_VALUE);
        InternalTestHelper.setInternalUserNumber(1);
        User user = tourGuideService.getAllUsers().get(0);
        List<Map<String, Object>> nearByAttractions = tourGuideService.getNearByAttractions(user.getLastVisitedLocation(), user);
        assertEquals(5, nearByAttractions.size());
    }
}
