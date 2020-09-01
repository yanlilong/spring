package com.yl.spring;


import com.yl.spring.kafka.Greeting;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.TopicPartition;
import org.springframework.kafka.core.KafkaTemplate;;
import org.springframework.kafka.listener.MessageListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.kafka.support.SendResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CountedCompleter;
import java.util.concurrent.TimeUnit;


@SpringBootApplication
@RestController
public class Application {
    /**
     * @GetMapping("/user") public Map<String, Object> user(@AuthenticationPrincipal OAuth2User principal) {
     * return Collections.singletonMap("name", principal.getAttribute("name"));
     * }
     * @GetMapping("/error") public String error(HttpServletRequest request) {
     * String message = (String) request.getSession().getAttribute("error.message");
     * request.getSession().removeAttribute("error.message");
     * return message;
     * }
     * @Override protected void configure(HttpSecurity http) throws Exception {
     * SimpleUrlAuthenticationFailureHandler handler = new SimpleUrlAuthenticationFailureHandler("/");
     * <p>
     * http.authorizeRequests(a -> a.antMatchers("/", "/error", "/webjars/***").permitAll().anyRequest().authenticated()
     * ).exceptionHandling(e -> e.authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
     * ).logout(l -> l.logoutSuccessUrl("/").permitAll()
     * ).csrf(c -> c.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()
     * )).oauth2Login(o -> o
     * .failureHandler((request, response, exception) -> {
     * request.getSession().setAttribute("error.message", exception.getMessage());
     * handler.onAuthenticationFailure(request, response, exception);
     * })
     * );
     * }
     */
    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = SpringApplication.run(Application.class, args);
        MessageProducer producer = context.getBean(MessageProducer.class);
        MessageListener listener = context.getBean(MessageListener.class);
        producer.sendMessage("Hello World");
        listener.latch.await(10, TimeUnit.SECONDS);

        for (int i = 0; i < 5; i++) {
            producer.sendMessageToPartion("Hello to partioned topic!", i);
        }
        listener.partitionLatch.await(10, TimeUnit.SECONDS);
        producer.sendMessageToFiltered("Hello world1");
        producer.sendMessageToFiltered("hello world2");
        listener.filterLatch.await(10, TimeUnit.SECONDS);
        context.close();
    }

    @Bean
    public MessageProducer messageProducer() {
        return new MessageProducer();
    }

    @Bean
    public MessageListener messageListener() {
        return new MessageListener();
    }

    public static class MessageListener {
        private CountDownLatch latch = new CountDownLatch(3);
        private CountDownLatch partitionLatch = new CountDownLatch(2);
        private CountDownLatch filterLatch = new CountDownLatch(2);
        private CountDownLatch greetingLatch = new CountDownLatch(1);

        @KafkaListener(topics = "${message.topic.name}", groupId = "foo", containerFactory = "fooKafkaListenerContainerFactory")
        public void listenGroupFoo(String message) {
            System.out.println("Received Messasge in group 'foo': " + message);
            latch.countDown();
        }


        @KafkaListener(topics = "${message.topic.name}", groupId = "bar", containerFactory = "barKafkaListenerContainerFactory")
        public void listenGroupBar(String message) {
            System.out.println("Received Message in group 'bar':" + message);
            latch.countDown();
        }

        @KafkaListener(topicPartitions = @TopicPartition(topic = "${partitioned.topic.name}", partitions = {"0", "3"}), containerFactory = "partitionsKafkaListenerContainerFactory")
        public void listenToPartition(@Payload String message, @Header(KafkaHeaders.RECEIVED_PARTITION_ID) int partition) {
            System.out.println("Received Message':" + message + "from partition:" + partition);
            this.partitionLatch.countDown();
        }

        @KafkaListener(topics = "${filtered.topic.name}", containerFactory = "filterKafkaListenerContainerFactory")
        public void listenWithFilter(String message) {
            System.out.println("Received Message in filtered listener':" + message);
            this.filterLatch.countDown();
        }

        @KafkaListener(topics = "${greeting.topic.name}", containerFactory = "greetingKafkaListenerContainerFactory")
        public void greetingListener(Greeting greeting) {
            System.out.println("Received greeting message:" + greeting);
            this.greetingLatch.countDown();
        }

    }

    public static class MessageProducer {

        @Autowired
        private KafkaTemplate<String, String> kafkaTemplate;
        @Autowired
        private KafkaTemplate<String, Greeting> greetingKafkaTemplate;
        @Value(value = "${message.topic.name}")
        private String topicName;

        @Value(value = "${partitioned.topic.name}")
        private String partionedTopicName;

        @Value(value = "${filtered.topic.name}")
        private String filteredTopicName;
        @Value(value = "${greeting.topic.name}")
        private String greetingTopicName;

        public void sendMessage(String message) {
            ListenableFuture<SendResult<String, String>> future = kafkaTemplate.send(topicName, message);
            future.addCallback(new ListenableFutureCallback<SendResult<String, String>>() {
                @Override
                public void onFailure(Throwable ex) {
                    System.out.println("Unable to send message=[" + message + "] due to:" + ex.getMessage());
                }

                @Override
                public void onSuccess(SendResult<String, String> result) {
                    System.out.println("Sent message=[" + message + "] with offset=[" + result.getRecordMetadata().offset() + "]");
                }
            });
        }

        public void sendMessageToPartion(String message, int partition) {
            kafkaTemplate.send(partionedTopicName, partition, null, message);
        }

        public void sendMessageToFiltered(String message) {
            kafkaTemplate.send(filteredTopicName, message);
        }

        public void sendGreetingMessage(Greeting greeting) {
            greetingKafkaTemplate.send(greetingTopicName, greeting);
        }
    }

}