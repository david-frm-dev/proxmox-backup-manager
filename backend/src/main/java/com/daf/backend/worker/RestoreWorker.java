package com.daf.backend.worker;

import com.daf.backend.model.RestoreJob;
import com.daf.backend.repository.RestoreJobRepository;
import com.daf.backend.service.RestoreExecutor;
import com.daf.backend.service.RestoreQueueService;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class RestoreWorker implements ApplicationRunner {
    private final RestoreQueueService queueService;
    private final RestoreJobRepository jobRepository;
    private final RestoreExecutor executor;

    private volatile boolean running = true;

    @Override
    public void run(ApplicationArguments args) {
        Thread worker = new Thread(this::loop, "restore-worker");
        worker.setDaemon(true);
        worker.start();
        log.info("Restore worker started");
    }

    private void loop() {
        while (running) {
            try {
                String jobId = queueService.dequeue();
                if (jobId == null) continue;

                RestoreJob job = jobRepository.findById(UUID.fromString(jobId)).orElseThrow();
                executor.execute(job);
            } catch (Exception e) {
                log.error("Restore worker failed to process queue entry: {}", e.getMessage());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException err) {
                    log.error(err.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("Restore worker stopped");
    }

    @PreDestroy
    public void shutdown() {
        running = false;
    }
}
