package ru.otus.scheduler;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class ConcertMonitoringJob implements Job {
    private static Logger logger = LoggerFactory.getLogger(ConcertMonitoringJob.class);

    public ConcertMonitoringJob(){ };

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
            String exchange = schedulerContext.getString("exchangeName");
            String queue = schedulerContext.getString("queueName");
            ConnectionFactory factory = new ConnectionFactory();
            factory.setHost("localhost");
            logger.info("In job put to rabbitMq exchange: {} queue: {}", exchange, queue);
            try (Connection connectionMonitoring = factory.newConnection();
                 Channel channelProducer = connectionMonitoring.createChannel()) {
                channelProducer.exchangeDeclare(exchange, "direct");
                channelProducer.basicPublish("", queue,
                        MessageProperties.PERSISTENT_TEXT_PLAIN,
                        Serializers.serialize(new MessageModel(MessageType.GET_MONITORING_RESULT, null)));
            } catch (IOException | TimeoutException e) {
                logger.error(e.getMessage(), e);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            logger.error("Error in doing MonitorJob {}", e.getMessage());
        }
    }
}
