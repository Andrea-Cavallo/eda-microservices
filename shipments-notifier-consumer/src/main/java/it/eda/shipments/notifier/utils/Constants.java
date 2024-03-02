package it.eda.shipments.notifier.utils;

public class Constants {
	
	private Constants() {}
	

	
	//DEBEZIUM OPERATION HEADERS
	public static final String DELETE = "d";
	public static final String UPDATE = "u";
	public static final String CREATE = "c";
	public static final String READ = "r";
	
	//CLIENT-ID NAMES FOR TOPICS
	public static final String SHIPMENT_CLIENT_ID = "shipmentClient";
	
	
	//CONSTANTS FOR LOGS
	public static final String DELETE_LOG = "**OPERATION IS DELETE**";
	public static final String UPDATE_LOG = "**OPERATION IS UPDATE**";
	public static final String READ_LOG = "**OPERATION IS READ**";
	public static final String CREATE_LOG = "**OPERATION IS CREATE**";
	public static final String START_DEBEZIUM_LOG = "*********** START DEBEZIUM *****************";
	
	//INNER-ROUTES
	public static final String DIRECT_SER_AND_PRODUCE = "direct:serAndProduce";


}
