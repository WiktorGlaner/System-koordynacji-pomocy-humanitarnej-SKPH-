package org.ioad.spring.resource.scheduler;

import org.ioad.spring.resource.services.ResourceService;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ResourceStatusScheduler {
    private final ResourceService resourceService;

    public ResourceStatusScheduler(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void scheduleStatusUpdate() {
        resourceService.updateExpiredStatus();
    }

    @EventListener(org.springframework.boot.context.event.ApplicationReadyEvent.class)
    public void onApplicationReady() {
        resourceService.updateExpiredStatus();
    }
}