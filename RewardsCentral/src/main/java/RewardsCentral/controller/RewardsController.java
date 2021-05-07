package RewardsCentral.controller;

import RewardsCentral.service.RewardsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
public class RewardsController {
    RewardsService rewardsService;

    @Autowired
    RewardsController(RewardsService rewardsService) {
        this.rewardsService = rewardsService;
    }

    @RequestMapping("/")
    public String index() {
        return "Greetings from RewardsCentral!";
    }


    /**
     * Get user rewards by attraction
     *
     * @param userId
     * @param attractionId
     */
    @GetMapping("/getRewards")
    public int getRewards(@RequestParam UUID userId, @RequestParam UUID attractionId) {
        return this.rewardsService.getRewardPoints(userId, attractionId);
    }
}
