//package ru.otus.service;
//
//import org.quartz.JobExecutionContext;
//import org.quartz.JobExecutionException;
//import org.quartz.SchedulerContext;
//import org.quartz.listeners.JobListenerSupport;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import ru.otus.db.service.DBServiceUser;
//import ru.otus.backend.model.User;
//import ru.otus.telegramApi.Bot;
//
//import java.util.List;
//
//public class ConcertJobListener extends JobListenerSupport {
//    private static Logger logger = LoggerFactory.getLogger(ConcertJobListener.class);
//    private String name;
//
//    public ConcertJobListener(String name) {
//        this.name = name;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    @Override
//    //jobWasExecuted() method of listener gets called after the execute method of the job has
//    // finished its work.
//    public void jobWasExecuted(JobExecutionContext jobExecutionContext,
//                               JobExecutionException jobException) {
//        try {
//            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
//            DBServiceUser dbService = (DBServiceUser) schedulerContext.get("dbService");
//            Bot bot = (Bot) schedulerContext.get("bot");
//            List<User> userList = dbService.getUsersForNotifying();
//            if (userList.size() > 0) {
//                logger.info("list-for-monitoring in listener {}", userList);
//                userList.forEach(bot::sendMessagesToSubscribers);
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//            logger.error("Error in doing MonitorListenerJob {}", e.getMessage());
//        }
//    }
//
//}
