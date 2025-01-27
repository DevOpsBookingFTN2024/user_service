package uns.ac.rs.user_service.service.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ReservationServiceClient {
    private final WebClient webClient;

    @Autowired
    public ReservationServiceClient(WebClient.Builder webClientBuilder,
                                    @Value("${reservation.service.url}") String reservationServiceUrl) {
        this.webClient = webClientBuilder.baseUrl(reservationServiceUrl).build();
    }

    public boolean isGuestHasAcceptedReservation(String guest) {
        try {
            return Boolean.TRUE.equals(webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reservations/guest/has-accepted-reservation/{guest}")
                            .build(guest))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to ReservationService: ", e);
        }
    }

    public boolean isHostHasAcceptedReservation(String host) {
        try {
            return Boolean.TRUE.equals(webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/reservations/host/has-accepted-reservation/{host}")
                            .build(host))
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block());
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to ReservationService: ", e);
        }
    }
}
