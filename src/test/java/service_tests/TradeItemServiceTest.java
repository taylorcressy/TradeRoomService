package service_tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import service.ServerMessage;
import service.TradeItemService;
import config.AppConfig;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={AppConfig.class})
public class TradeItemServiceTest {

	private static final Logger log = LoggerFactory.getLogger("service-test-logger");
	
	@Autowired private TradeItemService itemService;
	
	@Test
	public void testAddTradeItem() {
		List<String> tags = new ArrayList<String>();
		
		tags.add("Pictures");
		tags.add("Phones");
		tags.add("Boxes");
		
		ServerMessage message = itemService.addTradeItem("Test Item", "This is a description", tags, null, 5, "POOR", "01-11-1991", "someId");
		assertNotNull(message);
		log.info(message.toString());
	}
}
