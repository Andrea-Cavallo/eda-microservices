package it.eda.shipments.notifier;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"it.example.notifier.*"})
public class NotifierConsumerApplication {

    public static void main(String[] args) {
        SpringApplication.run(NotifierConsumerApplication.class, args);
    }
    

 
}
