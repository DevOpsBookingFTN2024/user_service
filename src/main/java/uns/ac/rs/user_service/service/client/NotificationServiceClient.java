package uns.ac.rs.user_service.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class NotificationServiceClient {
    private final WebClient webClient;

    @Autowired
    public NotificationServiceClient(WebClient.Builder webClientBuilder,
                                     @Value("${notification.service.url}") String notificationServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(notificationServiceUrl).build();
    }

    public void createHostNotificationSettings(String host) {
        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/notifications/host/create/{host}")
                            .build(host))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to NotificationService: ", e);
        }
    }

    public void createGuestNotificationSettings(String guest) {
        try {
            webClient.post()
                    .uri(uriBuilder -> uriBuilder
                            .path("/notifications/guest/create/{guest}")
                            .build(guest))
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to NotificationService: ", e);
        }
    }
}
