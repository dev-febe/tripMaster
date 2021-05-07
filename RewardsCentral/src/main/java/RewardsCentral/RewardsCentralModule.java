package RewardsCentral;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import rewardCentral.RewardCentral;

@Configuration
public class RewardsCentralModule {

    @Bean
    public RewardCentral getRewardsCentral() {
        return new RewardCentral();
    }
}
