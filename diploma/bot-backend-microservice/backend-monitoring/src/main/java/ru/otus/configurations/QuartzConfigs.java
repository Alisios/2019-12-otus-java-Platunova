package ru.otus.configurations;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import ru.otus.scheduler.ConcertMonitoringJob;

import java.util.Properties;

@Configuration
@ComponentScan("ru.otus")
public class QuartzConfigs {

    private final RabbitMQProperties rabbitMQProperties;
    private final QuartzProperties quartzPropertiesMy;

    @Autowired
    public QuartzConfigs(RabbitMQProperties rabbitMQProperties, QuartzProperties quartzPropertiesMy) {
        this.rabbitMQProperties = rabbitMQProperties;
        this.quartzPropertiesMy = quartzPropertiesMy;
    }

    @Bean
    public Scheduler scheduler(CronTrigger trigger, JobDetail jobDetail, SchedulerFactoryBean factory) throws SchedulerException {
        Properties quartzProperties = new Properties();
        quartzProperties.put("org.quartz.scheduler.instanceName", quartzPropertiesMy.getInstanceName());
        quartzProperties.put("org.quartz.threadPool.threadCount", quartzPropertiesMy.getThreadCount());
        factory.setQuartzProperties(quartzProperties);
        factory.setAutoStartup(true);
        factory.setWaitForJobsToCompleteOnShutdown(true);
        Scheduler scheduler = factory.getScheduler();
        scheduler.deleteJob(jobDetail.getKey());
        scheduler.scheduleJob(jobDetail, trigger);
        scheduler.getContext().put("exchangeName", rabbitMQProperties.getDbExchange());
        scheduler.getContext().put("queueName", rabbitMQProperties.getDbQueue());
        scheduler.start();
        return scheduler;
    }

    @Bean
    public CronTrigger trigger() {
        return TriggerBuilder.newTrigger()
                .withIdentity(quartzPropertiesMy.getTriggerName(), quartzPropertiesMy.getGroup())
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 4,16 * * ?"))
                .withDescription("fire twice a day every day")
                .forJob(quartzPropertiesMy.getJobName(), quartzPropertiesMy.getGroup())
                .build();
    }

    @Bean
    public JobDetail jobDetail() {
        return JobBuilder.newJob().ofType(ConcertMonitoringJob.class)
                .storeDurably()
                .withIdentity(quartzPropertiesMy.getJobName(), quartzPropertiesMy.getGroup())
                .withDescription("monitoring of concert tickets ")
                .build();
    }

}
