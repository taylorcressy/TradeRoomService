package database_tests;

import static org.junit.Assert.*;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.imageio.ImageIO;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import config.TestConfig;
import database_entities.TradeItem;
import database_entities.repositories.TradeItemRepository;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { TestConfig.class })
public class TestTradeItemDatabaseHandling {

	@Autowired
	private TradeItemRepository itemRepo;

	@Test
	public void testSearchingDbByTags() {
		Collection<String> tags = new ArrayList<String>();
		
		tags.add("Phones");
		
		List<TradeItem> items = itemRepo.findItemsWithTags(tags);
		
		System.out.println(items.toString());
	}
	
	
	@Test
	public void testSavingImageToDB() {
		try {
			byte[] imageBytes = extractBytes("test-image.png");
			String retId = itemRepo.saveTradeItemImage(imageBytes, "test-image", "photo");
			System.out.println(retId);
		}
		catch(IOException ie) {
			fail("IO EXCEPTION");
		}
	}
	
	@Test
	public void testGettingImageFromDB() throws IOException {		
		byte [] image = itemRepo.getTradeItemImage("535d38da300416a1dad8b6e1");
		
		assertNotNull(image);
		
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(new File("another-test.png")));
		bos.write(image);
		bos.flush();
		bos.close();
	}

	public byte[] extractBytes(String ImageName) throws IOException {
		// open image
		ClassPathResource resource = new ClassPathResource(ImageName);
		File imgPath = resource.getFile();
		BufferedImage bufferedImage = ImageIO.read(imgPath);

		// get DataBufferBytes from Raster
		WritableRaster raster = bufferedImage.getRaster();
		DataBufferByte data = (DataBufferByte) raster.getDataBuffer();

		return (data.getData());
	}
}
