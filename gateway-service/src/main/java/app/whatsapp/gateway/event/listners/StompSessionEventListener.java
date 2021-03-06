package app.whatsapp.gateway.event.listners;

import app.whatsapp.gateway.models.StompPrincipal;
import app.whatsapp.gateway.service.SessionRegistryService;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

import java.util.Objects;

@Component
public class StompSessionEventListener {

    private SessionRegistryService sessionRegistryService;

    public StompSessionEventListener(SessionRegistryService sessionRegistryService) {
        this.sessionRegistryService = sessionRegistryService;
    }

    @EventListener
    private void handleSessionDisconnected(SessionDisconnectEvent event) {
        if (Objects.nonNull(event.getUser())) {
            StompPrincipal stompPrincipal = (StompPrincipal) event.getUser();
            sessionRegistryService.evictSession(stompPrincipal);
        }
    }
}
