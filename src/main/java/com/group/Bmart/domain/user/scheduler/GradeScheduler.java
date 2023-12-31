package com.group.Bmart.domain.user.scheduler;

import com.group.Bmart.domain.user.service.GradeService;
import lombok.RequiredArgsConstructor;
import net.javacrumbs.shedlock.spring.annotation.EnableSchedulerLock;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@EnableSchedulerLock(defaultLockAtMostFor = "PT60S")
public class GradeScheduler {

    private final GradeService gradeService;

}
