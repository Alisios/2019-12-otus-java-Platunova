package ru.otus.scheduler;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Properties;

public class QuartzScheduler  implements SchedulerService{
    private static Logger logger = LoggerFactory.getLogger(QuartzScheduler.class);
    private Scheduler scheduler;

    public QuartzScheduler(String exchangeName, String queueName ){
        try{
            Properties quartzProperties = new Properties();
            quartzProperties.put("org.quartz.scheduler.instanceName", "MonitoringScheduler");
            quartzProperties.put("org.quartz.threadPool.threadCount", String.valueOf(1));
            SchedulerFactory schedulerFactory = new StdSchedulerFactory(quartzProperties);
            ConcertMonitoringJob concertMonitoringJob = new ConcertMonitoringJob();
            scheduler = schedulerFactory.getScheduler();
            scheduler.getContext().put("exchangeName", exchangeName);
            scheduler.getContext().put("queueName", queueName);

            JobDetail job = JobBuilder.newJob(concertMonitoringJob.getClass()).
                    withIdentity("ConcertMonitoringJob", "group1")
                    .build();

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity("ticketMonitorTrigger", "group1")
                    .withSchedule(CronScheduleBuilder.cronSchedule("0 0 4,16 * * ?"))//("0 0,1,2 20 * * ?"))//("0 0 5,17 * * ?"))
                    .forJob("ConcertMonitoringJob", "group1")
                    .build();
            scheduler.scheduleJob(job, trigger);
            logger.info("Scheduler configuration is done");
        }
        catch (SchedulerException e){
            logger.error("Failed with QuartzScheduler {}", e.getMessage());
        }
    }

    @Override
    public void startMonitoring(){
        try {
            logger.info("Scheduler started");
            scheduler.start();
        }
        catch(SchedulerException ex){
            logger.error("Failed with starting of QuartzScheduler {}", ex.getMessage());
        }
    }

    @Override
    public void stopMonitoring() {
        try {
            scheduler.shutdown();
            logger.info("Scheduler is turned off.");
        }
        catch (SchedulerException e){
            logger.error("Failed with QuartzScheduler turning off  {}", e.getMessage());
        }
    }
}
