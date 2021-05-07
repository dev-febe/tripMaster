package RewardsCentral.service;

import org.springframework.stereotype.Service;
import rewardCentral.RewardCentral;

import java.util.UUID;


@Service
public class RewardsService {
    RewardCentral rewardsCentral;

    RewardsService(RewardCentral rewardsCentral) {
        this.rewardsCentral = rewardsCentral;
    }

    /**
     * This method gets the calculated rewards points
     *
     * @param attractionId
     * @param userId
     * @return
     */
    public int getRewardPoints(UUID attractionId, UUID userId) {
        return rewardsCentral.getAttractionRewardPoints(attractionId, userId);
    }
}
