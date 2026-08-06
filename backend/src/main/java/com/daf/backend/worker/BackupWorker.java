package com.daf.backend.worker;

import com.daf.backend.model.BackupJob;
import com.daf.backend.model.BackupType;
import com.daf.backend.repository.BackupJobRepository;
import com.daf.backend.service.BackupExecutor;
import com.daf.backend.service.BackupQueueService;
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
public class BackupWorker implements ApplicationRunner {
    private final BackupQueueService queueService;
    private final BackupJobRepository jobRepository;
    private final BackupExecutor executor;

    private volatile boolean running = true;

    @Override
    public void run(ApplicationArguments args) {
        Thread worker = new Thread(this::loop, "backup-worker");
        worker.setDaemon(true);
        worker.start();
        log.info("Backup worker started");
    }

    private void loop() {
        while (running) {
            try {
                String jobId = queueService.dequeue();
                if (jobId == null) continue;

                BackupJob job = jobRepository.findById(UUID.fromString(jobId)).orElseThrow();
                executor.execute(job, BackupType.GUEST);
            } catch (Exception e) {
                log.error("Worker failed to process queue entry: {}", e.getMessage());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException err) {
                    log.error(err.getMessage());
                    Thread.currentThread().interrupt();
                }
            }
        }
        log.info("Backup worker stopped");
    }

    @PreDestroy
    public void shutdown() {
        running = false;
    }
}
