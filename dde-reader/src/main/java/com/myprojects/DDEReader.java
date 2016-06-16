package com.myprojects;

import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;

import com.pretty_tools.dde.DDEException;
import com.pretty_tools.dde.DDEMLException;
import com.pretty_tools.dde.client.DDEClientConversation;
import com.pretty_tools.dde.client.DDEClientEventListener;

public class DDEReader {

	private static Logger log = Logger.getLogger(DDEReader.class);
	
	private static final String SERVICE = "IWDDE";
    private static final String TOPIC = "STOCK_PRICE";
    //private static final String TOPIC = "IWDDE|STOCK_PRICE";
    private static final String ITEM = "'331?contractPrice'";
	
	public static void main(String[] args) {
		log.info("Avvio test connessione DDE. Parametri...");
		log.info("SERVICE: " + SERVICE);
		log.info("TOPIC: " + TOPIC);
		log.info("ITEM: " + ITEM);
		
		try
        {
            // event to wait disconnection
            final CountDownLatch eventDisconnect = new CountDownLatch(1);

            // DDE client
            final DDEClientConversation conversation = new DDEClientConversation();
            // We can use UNICODE format if server prefers it
            //conversation.setTextFormat(ClipboardFormat.CF_UNICODETEXT);

            conversation.setEventListener(new DDEClientEventListener()
            {
                public void onDisconnect()
                {
                	log.info("onDisconnect()");
                    eventDisconnect.countDown();
                }

                public void onItemChanged(String topic, String item, String data)
                {
                	log.info("onItemChanged(" + topic + "," + item + "," + data + ")");
                    try
                    {
                        if ("stop".equalsIgnoreCase(data))
                            conversation.stopAdvice(item);
                    }
                    catch (DDEException e)
                    {
                    	log.info("Exception: " + e);
                    }
                }
            });

            log.info("Connecting...");
            conversation.connect(SERVICE, TOPIC);
            conversation.startAdvice(ITEM);

            log.info("Waiting event...");
            eventDisconnect.await();
            log.info("Disconnecting...");
            conversation.disconnect();
            log.info("Exit from thread");
        }
        catch (DDEMLException e)
        {
        	log.info("DDEMLException: 0x" + Integer.toHexString(e.getErrorCode()) + " " + e.getMessage());
        }
        catch (DDEException e)
        {
        	log.info("DDEClientException: " + e.getMessage());
        }
        catch (Exception e)
        {
        	log.info("Exception: " + e);
        }
	}

}
