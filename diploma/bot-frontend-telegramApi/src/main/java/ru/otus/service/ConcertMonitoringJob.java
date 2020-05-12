//package ru.otus.service;
//import org.quartz.Job;
//import org.quartz.JobExecutionContext;
//import org.quartz.SchedulerContext;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import ru.otus.db.service.DBServiceUser;
//import ru.otus.backend.eventApi.MonitoredEvent;
//import ru.otus.backend.model.User;
//
//import java.util.Date;
//import java.util.List;
//
//public class ConcertMonitoringJob implements Job {
//    private static Logger logger = LoggerFactory.getLogger(ConcertMonitoringJob.class);
//
//    public ConcertMonitoringJob(){ }
//
//    @Override
//    public void execute(JobExecutionContext jobExecutionContext){
//        try {
//            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
//            DBServiceUser dbServiceUser = (DBServiceUser) schedulerContext.get("dbService");
//            MonitoredEvent monitoredEvent = (MonitoredEvent) schedulerContext.get("monitoredEvent");
//            List<User> userList = dbServiceUser.getAllUsers();
//            System.out.println("from JOB");
//            if (userList.size() != 0)
//                for (User user : userList) {
//                    if (monitoredEvent.checkingTickets(user)) {
//                        dbServiceUser.saveUser(user);
//                    }
//                    if (user.getDateOfMonitorFinish().before(new Date())) {
//                        user.setDateExpired(true);
//                        dbServiceUser.saveUser(user);
//                    }
//                }
//        }
//        catch (Exception e) {
//            e.printStackTrace();
//            logger.error("Error in doing MonitorJob {}", e.getMessage());
//        }
//    }
//
//}
