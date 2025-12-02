package com.backthree.cohobby.global.config.payments;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "payment")
public class PaymentsConfig {
    private Toss toss = new Toss();
    private String successUrl;
    private String failUrl;

    @Getter
    @Setter
    public static class Toss {
        private String secretKey;
        private String securityKey;
        private String confirmUrl;
        private String billingAuthUrl; // 빌링키 발급 URL
        private String billingPayUrl; // 자동결제 URL
    }
}
