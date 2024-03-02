package it.eda.shipments.notifier.routes;


import org.apache.camel.LoggingLevel;
import org.apache.camel.Predicate;
import org.apache.camel.builder.RouteBuilder;
import org.apache.kafka.clients.producer.BufferExhaustedException;
import org.apache.kafka.common.errors.InterruptException;
import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.errors.RecordTooLargeException;
import org.apache.kafka.common.errors.SerializationException;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.stereotype.Component;

import it.eda.shipments.notifier.conf.builder.KafkaConsumerBuilder;
import it.eda.shipments.notifier.exception.InternalException;
import it.eda.shipments.notifier.processor.DeserializeProcessor;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NotifierRoute extends RouteBuilder {
	
	private final KafkaConsumerBuilder kafkaConsumerBuilder;  
    private final DeserializeProcessor deserializeProcessor;


	@Override
	public void configure() {

		onException();
		consumerRoute();

	}
	
	private void consumerRoute()   {
		
		from(kafkaConsumerBuilder.buildKafkaEndpoint())
				.log(LoggingLevel.INFO,"Topic received is : ${body}")
				.process(deserializeProcessor)
				.to("direct:sendMail")
				.log(LoggingLevel.INFO,"Email succesfully sended...");
		
		
		from("direct:sendMail")
	    .process(exchange -> {
	        String stato = exchange.getIn().getHeader("stato", String.class);
	        String destinatario = exchange.getIn().getHeader("destinatario", String.class);
	        String userEmail = exchange.getIn().getHeader("userEmail", String.class);

	        exchange.getIn().setBody("Gentile " + destinatario + ",\n\nIl tuo ordine è " + stato + ".");
	        exchange.getIn().setHeader("Subject", "Aggiornamento stato ordine");
	        exchange.getIn().setHeader("To", userEmail);
	    })
	    .to("smtp://mailhog:1025?from=notifier@apachecamel.com");


	}
	

	private void onException() {
		
		onException(InternalException.class)
			.handled(true)
			.log(LoggingLevel.ERROR,"#### Internal Exception  Thrown  ####");
		
		onException(SerializationException.class)
			.handled(true)
			.log(LoggingLevel.ERROR,"#### Serialization Exception  Thrown  ####");

		onException(BufferExhaustedException.class)
			.handled(true)
			.log(LoggingLevel.ERROR,"#### BufferExhausted Exception  Thrown  ####");

		onException(TimeoutException.class)
			.handled(true)
			.log(LoggingLevel.ERROR,"#### Timeout Exception  Thrown  ####");

		onException(ProducerFencedException.class)
			.handled(true)
			.log(LoggingLevel.ERROR,"#### ProducerFenced Exception  Thrown  ####");

		onException(InterruptException.class)
			.handled(true)
			.log(LoggingLevel.ERROR,"#### Interrupt Exception  Thrown  ####");

		onException(RecordTooLargeException.class)
			.handled(true)
			.log(LoggingLevel.ERROR,"#### RecordTooLarge Exception  Thrown  ####");			
		


		onException(Exception.class)
			.handled(true)
			.onWhen(skipSpecifiedException())
			.log(LoggingLevel.ERROR, "#### Generic Exception Thrown  ####");

	}



	private Predicate skipSpecifiedException() {
		return exchange -> {return
		        (      !(exchange.getException() instanceof TimeoutException) 
		        );};
	}
	
	
}
