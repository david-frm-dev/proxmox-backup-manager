package com.daf.backend.scheduler;

import com.daf.backend.model.BackupJob;
import com.daf.backend.repository.BackupJobRepository;
import com.daf.backend.service.BackupQueueService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.scheduling.support.SimpleTriggerContext;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

@Component
@AllArgsConstructor
@Slf4j
public class JobScheduler {
    private final ThreadPoolTaskScheduler taskScheduler;
    private final BackupJobRepository jobRepository;
    private final BackupQueueService queueService;

    private final Map<UUID, ScheduledFuture<?>> scheduledJobs = new ConcurrentHashMap<>();

    public void cancel(UUID jobId) {
        ScheduledFuture<?> sf = scheduledJobs.remove(jobId);

        if (sf != null) {
            sf.cancel(false);
        }
    }

    public void schedule(BackupJob job) {
        cancel(job.getId());

        if (!job.isEnabled()) {
            log.warn("JOB: {} NOT STARTED BECAUSE ITS DISABLED", job.getId());
            return;
        }

        CronTrigger trigger = new CronTrigger(job.getScheduleCron());
        ScheduledFuture<?> sf = taskScheduler.schedule(() -> {
            queueService.enqueue(job.getId());

            job.setNextRunAt(calcNextTimestamp(trigger));
            jobRepository.save(job);
        }, trigger);

        scheduledJobs.put(job.getId(), sf);

        job.setNextRunAt(calcNextTimestamp(trigger));
        jobRepository.save(job);
        log.info("Job: {} scheduled for {}", job.getName(), job.getScheduleCron());
    }

    @PostConstruct
    public void initSchedules() {
        System.out.println();

        List<BackupJob> jobs = jobRepository.findAll();
        jobs.forEach(this::schedule);

        log.info("Backup jobs found ({}/{}) (scheduled/all)\n", scheduledJobs.size(), jobs.size());
    }

    private Timestamp calcNextTimestamp(CronTrigger trigger) {
        TriggerContext ctx = new SimpleTriggerContext();
        Instant instant = trigger.nextExecution(ctx);
        if (instant == null) return null;

        return Timestamp.from(instant);
    }
}
