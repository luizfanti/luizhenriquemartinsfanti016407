package com.example.musicapi.security;
import java.util.stream.Collectors;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class RateLimitService {

    private static class WindowCounter {
        private volatile long windowStartEpochSec;
        private final AtomicInteger count = new AtomicInteger(0);

        private WindowCounter(long windowStartEpochSec) {
            this.windowStartEpochSec = windowStartEpochSec;
        }
    }

    private final ConcurrentHashMap<String, WindowCounter> counters = new ConcurrentHashMap<>();

    public boolean allow(String key, int maxRequestsPerMinute) {
        long nowSec = Instant.now().getEpochSecond();
        long windowStart = nowSec - (nowSec % 60);

        WindowCounter counter = counters.computeIfAbsent(key, k -> new WindowCounter(windowStart));

        // Se virou a janela, reseta (com cuidado para concorrência)
        if (counter.windowStartEpochSec != windowStart) {
            synchronized (counter) {
                if (counter.windowStartEpochSec != windowStart) {
                    counter.windowStartEpochSec = windowStart;
                    counter.count.set(0);
                }
            }
        }

        int current = counter.count.incrementAndGet();

        // limpeza simples (evita crescer infinito) - remove chaves antigas
        // Remove só quando estourar a janela antiga por muito tempo (best-effort)
        // (mantém simplicidade do desafio)
        if (counters.size() > 10_000) {
            counters.entrySet().removeIf(e -> (nowSec - e.getValue().windowStartEpochSec) > 300);
        }

        return current <= maxRequestsPerMinute;
    }
}

