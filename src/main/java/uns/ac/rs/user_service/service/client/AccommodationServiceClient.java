package uns.ac.rs.user_service.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import uns.ac.rs.user_service.dto.response.MessageResponse;

@Service
public class AccommodationServiceClient {
    private final WebClient webClient;

    @Autowired
    public AccommodationServiceClient(WebClient.Builder webClientBuilder,
                                      @Value("${accommodation.service.url}") String userServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

    public void deleteAllAccommodationsByHost(String jwtToken) {
        try {
            webClient.delete()
                    .uri("/accommodations/delete/all-host")
                    .header(HttpHeaders.AUTHORIZATION, "Bearer " + jwtToken)
                    .retrieve()
                    .toBodilessEntity()
                    .block();
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to AccommodationService: ", e);
        }
    }
}
