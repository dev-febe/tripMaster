package tourGuide;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.junit.Test;

import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import tourGuide.model.*;
import tourGuide.client.GpsUtilClient;
import tourGuide.client.RewardsClient;
import tourGuide.helper.InternalTestHelper;
import tourGuide.service.RewardsService;
import tourGuide.service.TourGuideService;


@SpringBootTest
@RunWith(SpringRunner.class)
public class TestPerformance {
    @Autowired
    GpsUtilClient gpsUtilClient;

    @Autowired
    RewardsClient rewardsClient;

    @Autowired
    RewardsService rewardsService;

    @Autowired
    TourGuideService tourGuideService;

    /*
     * A note on performance improvements:
     *
     *     The number of users generated for the high volume tests can be easily adjusted via this method:
     *
     *     		InternalTestHelper.setInternalUserNumber(100000);
     *
     *
     *     These tests can be modified to suit new solutions, just as long as the performance metrics
     *     at the end of the tests remains consistent.
     *
     *     These are performance metrics that we are trying to hit:
     *
     *     highVolumeTrackLocation: 100,000 users within 15 minutes:
     *     		assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     *
     *     highVolumeGetRewards: 100,000 users within 20 minutes:
     *          assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
     */
    @Test
    public void highVolumeTrackLocation() {
        // Users should be incremented up to 100,000, and test finishes within 15 minutes
        InternalTestHelper.setInternalUserNumber(10);
        List<User> allUsers = tourGuideService.getAllUsers();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        allUsers.forEach(u -> futures.add(CompletableFuture.runAsync(() -> tourGuideService.trackUserLocation(u))));
        futures.forEach(CompletableFuture::join);

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeTrackLocation: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(15) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }

    @Test
    public void highVolumeGetRewards() {
        // Users should be incremented up to 100,000, and test finishes within 20 minutes
        InternalTestHelper.setInternalUserNumber(10);

        StopWatch stopWatch = new StopWatch();
        stopWatch.start();

        Attraction attraction = gpsUtilClient.getAttractions().get(0);
        List<User> allUsers = tourGuideService.getAllUsers();
        List<CompletableFuture<Void>> futures = new ArrayList<>();

        allUsers.forEach(u -> {
            u.addToVisitedLocations(new VisitedLocation(u.getUserId(), attraction, new Date()));
            futures.add(CompletableFuture.runAsync(() -> rewardsService.calculateRewards(u)));
        });
        futures.forEach(CompletableFuture::join);

        stopWatch.stop();
        tourGuideService.tracker.stopTracking();

        System.out.println("highVolumeGetRewards: Time Elapsed: " + TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()) + " seconds.");
        assertTrue(TimeUnit.MINUTES.toSeconds(20) >= TimeUnit.MILLISECONDS.toSeconds(stopWatch.getTime()));
    }
}
