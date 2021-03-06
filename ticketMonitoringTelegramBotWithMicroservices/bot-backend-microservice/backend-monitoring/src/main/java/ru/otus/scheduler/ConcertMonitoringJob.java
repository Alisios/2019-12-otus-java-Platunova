package ru.otus.scheduler;

import com.rabbitmq.client.MessageProperties;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.SchedulerContext;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.otus.helpers.MessageModel;
import ru.otus.helpers.MessageType;
import ru.otus.helpers.Serializers;

/**
 * срабатывание тригера -> запрос в БД для получения всех пользователей
 */
@Component
@Slf4j
public class ConcertMonitoringJob implements Job {

    @Autowired
    private AmqpTemplate template;

    public ConcertMonitoringJob() {
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        try {
            SchedulerContext schedulerContext = jobExecutionContext.getScheduler().getContext();
            String exchange = schedulerContext.getString("exchangeName");
            String queue = schedulerContext.getString("queueName");
            template.convertAndSend(exchange, queue,
                    MessageBuilder
                            .withBody(Serializers.serialize(new MessageModel(MessageType.GET_MONITORING_RESULT, null)))
                            .setContentType(String.valueOf(MessageProperties.PERSISTENT_TEXT_PLAIN))
                            .build());
        } catch (Exception e) {
            e.printStackTrace();
            log.error("Error in doing MonitorJob {}", e.getMessage());
        }
    }
}
