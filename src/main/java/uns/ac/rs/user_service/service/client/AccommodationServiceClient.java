package uns.ac.rs.user_service.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class AccommodationServiceClient {
    private final WebClient webClient;

    @Autowired
    public AccommodationServiceClient(WebClient.Builder webClientBuilder,
                                      @Value("${accommodation.service.url}") String userServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(userServiceUrl).build();
    }

}
