package io.cryptorush.userservice;


import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.CustomizableThreadFactory;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
@Configuration
public class SchedulerConfig {

    @Value("${scheduler.rest.core.pool.size}")
    private int schedulerRestCorePoolSize;
    @Value("${scheduler.rest.max.pool.size}")
    private int schedulerRestMaxPoolSize;
    @Value("${scheduler.rest.keep.alive.time.secs}")
    private int schedulerRestKeepAliveTime;
    @Value("${scheduler.rest.queue.max.size}")
    private int schedulerRestQueueMaxSize;

    @Bean
    @Qualifier("rest-scheduler")
    public Scheduler mainScheduler() {

        Preconditions.checkArgument(schedulerRestCorePoolSize > 0, "Missing scheduler.rest.core.pool.size config!");
        Preconditions.checkArgument(schedulerRestMaxPoolSize > 0, "Missing scheduler.rest.max.pool.size config!");
        Preconditions.checkArgument(schedulerRestKeepAliveTime > 0, "Missing scheduler.rest.keep.alive.time.secs config!");
        Preconditions.checkArgument(schedulerRestQueueMaxSize > 0, "Missing scheduler.rest.queue.max.size config!");

        log.info("Creating REST scheduler: core.pool.size={}, max.pool.size={}, keep.alive.time={}, queue.max.size={}",
                schedulerRestCorePoolSize,
                schedulerRestMaxPoolSize,
                schedulerRestKeepAliveTime,
                schedulerRestQueueMaxSize);
        BlockingQueue<Runnable> queue = new ArrayBlockingQueue<>(schedulerRestQueueMaxSize);
        ThreadPoolExecutor poolExecutor = new ThreadPoolExecutor(
                schedulerRestCorePoolSize,
                schedulerRestMaxPoolSize,
                schedulerRestKeepAliveTime,
                TimeUnit.SECONDS,
                queue, new CustomizableThreadFactory("rest-scheduler-t-"));
        return Schedulers.fromExecutor(poolExecutor);
    }
}
