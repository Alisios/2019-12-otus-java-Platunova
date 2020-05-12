//package ru.otus.service;
//import org.quartz.*;
//import org.quartz.impl.StdSchedulerFactory;
//import org.quartz.impl.matchers.KeyMatcher;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import ru.otus.db.service.DBServiceUser;
//import ru.otus.backend.eventApi.MonitoredEvent;
//import ru.otus.telegramApi.Bot;
//import java.util.Properties;
//
//public class NotificationServiceImpl implements NotificationService {
//    private static Logger logger = LoggerFactory.getLogger(NotificationServiceImpl.class);
//
//    final private Bot bot;
//    final private String name;
//    final private MonitoredEvent concert ;
//    final private DBServiceUser dbService;
//    private SchedulerFactory schedulerFactory;
//    private Scheduler scheduler;
//    private ConcertJobListener concertJobListener = new ConcertJobListener("concertJobListener");
//    private ConcertMonitoringJob concertMonitoringJob = new ConcertMonitoringJob();
//
//    public NotificationServiceImpl(MonitoredEvent concert, DBServiceUser dbService, Bot bot, String name) {
//        this.name = name;
//        this.bot = bot;
//        this.concert = concert;
//        this.dbService = dbService;
//    }
//
//    @Override
//    public void startMonitoring()  {
//        try {
//            Properties quartzProperties = new Properties();
//            quartzProperties.put("org.quartz.scheduler.instanceName", "Scheduler_test");
//            //при увеличении количества потоков проверить синхронзацию к обращениию к БД
//            quartzProperties.put("org.quartz.threadPool.threadCount", String.valueOf(1));
//            schedulerFactory = new StdSchedulerFactory(quartzProperties);
//            scheduler = schedulerFactory.getScheduler();
//            scheduler.getContext().put("dbService", dbService);
//            scheduler.getContext().put("monitoredEvent", concert);
//            scheduler.getContext().put("bot", bot);
//
//            JobDetail job = JobBuilder.newJob(concertMonitoringJob.getClass()).
//                    withIdentity("ticketMonitorJob", "group1")
//                    .build();
//
//            CronTrigger trigger = TriggerBuilder.newTrigger()
//                    .withIdentity("ticketMonitorTrigger", "group1")
//                    .withSchedule(CronScheduleBuilder.cronSchedule("0 47,48,49 22 * * ?"))//("0 0,1,2 20 * * ?"))//("0 0 5,17 * * ?"))
//                    .forJob("ticketMonitorJob", "group1")
//                    .build();
//
//            scheduler.getListenerManager().addJobListener(concertJobListener, KeyMatcher.keyEquals(new JobKey("ticketMonitorJob", "group1")));
//            scheduler.scheduleJob(job, trigger);
//            logger.info("Scheduler strated..");
//            scheduler.start();
//        }catch (SchedulerException e){
//            logger.error("Failed with QuartzScheduler {}", e.getMessage());
//        }
//
//    }
//
//    @Override
//    public void stopMonitoring() {
//        try {
//            scheduler.shutdown();
//        logger.info("Scheduler is turning off...");
//        }
//        catch (SchedulerException e){
//            logger.error("Failed with Quartzscheduler turning off  {}", e.getMessage());
//        }
//    }
//
//    public Bot getBot() {
//        return bot;
//    }
//
//    @Override
//    public String getName() {
//        return name;
//    }
//
//}
