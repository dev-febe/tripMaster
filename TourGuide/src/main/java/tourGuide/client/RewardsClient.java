package tourGuide.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.UUID;

@FeignClient(name = "rewards", url = "localhost:8082")
public interface RewardsClient {

    @GetMapping("/getRewards")
    int getRewards(@RequestParam UUID attractionId, @RequestParam UUID userId);
}
