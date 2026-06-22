package br.com.gemsbiotec.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class LoginAttemptServiceTest {

    @Test
    void deveBloquearDepoisDeCincoFalhasELiberarAposSucesso() {
        LoginAttemptService service = new LoginAttemptService();
        String email = "usuario@exemplo.com";

        for (int i = 0; i < 5; i++) {
            assertEquals(0, service.segundosAteLiberar(email));
            service.registrarFalha(email);
        }

        assertTrue(service.segundosAteLiberar(email) > 0);
        service.registrarSucesso(email);
        assertEquals(0, service.segundosAteLiberar(email));
    }
}
