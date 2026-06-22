package br.com.gemsbiotec.auth;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class LoginAttemptService {

    private static final int MAX_TENTATIVAS = 5;
    private static final Duration JANELA = Duration.ofMinutes(15);
    private static final int MAX_CHAVES = 10_000;

    private final Map<String, Tentativas> tentativasPorEmail = new ConcurrentHashMap<>();

    public long segundosAteLiberar(String email) {
        Tentativas tentativas = tentativasPorEmail.get(email);
        if (tentativas == null || tentativas.quantidade < MAX_TENTATIVAS) {
            return 0;
        }
        long segundos = Duration.between(Instant.now(), tentativas.bloqueadoAte).toSeconds();
        if (segundos <= 0) {
            tentativasPorEmail.remove(email, tentativas);
            return 0;
        }
        return segundos;
    }

    public void registrarFalha(String email) {
        Instant agora = Instant.now();
        tentativasPorEmail.compute(email, (chave, atual) -> {
            if (atual == null || agora.isAfter(atual.bloqueadoAte)) {
                return new Tentativas(1, agora.plus(JANELA));
            }
            return new Tentativas(atual.quantidade + 1, atual.bloqueadoAte);
        });
        limparExpiradosSeNecessario(agora);
    }

    public void registrarSucesso(String email) {
        tentativasPorEmail.remove(email);
    }

    private void limparExpiradosSeNecessario(Instant agora) {
        if (tentativasPorEmail.size() <= MAX_CHAVES) {
            return;
        }
        tentativasPorEmail.entrySet().removeIf(entry -> agora.isAfter(entry.getValue().bloqueadoAte));
        var iterator = tentativasPorEmail.keySet().iterator();
        while (tentativasPorEmail.size() > MAX_CHAVES && iterator.hasNext()) {
            tentativasPorEmail.remove(iterator.next());
        }
    }

    private record Tentativas(int quantidade, Instant bloqueadoAte) {
    }
}
