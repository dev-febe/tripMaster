package GpsUtil;

import gpsUtil.GpsUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GpsUtilModule {

    @Bean
    public GpsUtil getGpsUtil() {
        return new GpsUtil();
    }
}
