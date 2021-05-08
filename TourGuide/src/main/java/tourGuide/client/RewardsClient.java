package tourGuide.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "rewards", url = "${client.rewards.url}")
public interface RewardsClient {
    /**
     * Endpoint: Get /getRewards
     * Desc: Get user rewards
     *
     * @param attractionId
     * @param userId
     * @return
     */
    @GetMapping("/getRewards")
    int getRewards(@RequestParam UUID attractionId, @RequestParam UUID userId);
}
