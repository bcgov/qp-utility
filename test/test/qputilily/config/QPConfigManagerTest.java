package test.qputilily.config;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.xml.sax.SAXException;

import qputility.config.QPConfigManager;
import qputility.exceptions.QPConfig_Exception;

public class QPConfigManagerTest {
	
	/**
	 * Test case for setting and retrieving various
	 * value fields in QPConfigManager
	 */
	@Test
	public void testConfigMgr() {
		// load dummy config file
		// should be located in same package as this class
		String configPath = this.getClass().getResource("test_config.xml").getPath().replaceFirst("/", "");
		QPConfigManager mgr;
		try {
			mgr = QPConfigManager.getInstance(configPath);
			Object xml = mgr.getValue("XMLtest");
			Object bean = mgr.getValue("JAXBtest");
			Assert.assertTrue(mgr.getStringValue("StringTest").equalsIgnoreCase("The value for my key"));
			Assert.assertTrue(xml instanceof org.w3c.dom.Document);
			Assert.assertTrue(bean instanceof test.support.beans.TestBean);
		} catch (QPConfig_Exception e) {
			e.printStackTrace();
			Assert.fail();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (SAXException e) {
			e.printStackTrace();
			Assert.fail();
		} catch (IOException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}

}
