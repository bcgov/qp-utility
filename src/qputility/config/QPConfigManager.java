package qputility.config;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import qputility.encodeDecode.QPEncodeDecode;
import qputility.enums.QPDisplayDebugInfo;
import qputility.exceptions.QPConfig_Exception;
import qputility.io.QPWriter;
import qputility.jaxb.JAXBUtil;
import qputility.xml.QPXml;

public class QPConfigManager
{
//	A singleton class that parses a config file in the form of:
	
//	<root>
//		<entry>
//			<key>someKey</key>
//			<value>someValue</value>
//		</entry>
//		<entry>
//			<key>someKey2</key>
//			<value>someValue2</value> 
//		</entry>
//		<list>
//			<key>someKey3</key>
//			<value>someValue3</value>
//			<value>someValue4</value>
//			<value>someValue5</value>
//		</list>
//		<list>
//			<key>someKey4</key>
//			<value>someValue6</value>
//			<value>someValue7</value>
//			<value>someValue8</value>
//		</list>
//		<map id="fielders">
//			<entry>
//				<key>Term</key>
//				<value>//definition/term</value>
//			</entry>
//			<entry>
//				<key>Descriptor</key>
//				<value>//descr</value>
//			</entry>
//		</map>
//	</root>
	
	private static Map<String, QPConfigManager> cache = null;
	
	private HashMap<String, Object> configEntryValues;
	private HashMap<String, List<String>> configListValues;
	private Map<String, Map<String, String>> configMapValues;
	private String configXMLFileLocation = System.getProperty("user.dir") + "\\" + "config.xml";
	private QPDisplayDebugInfo displayDebugInfo = QPDisplayDebugInfo.doNotDisplayDebugInfo;
	
	protected QPConfigManager(String _configXMLFileLocation, boolean createNewConfigFile) throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
	{
		configXMLFileLocation = _configXMLFileLocation;
		if(createNewConfigFile)
		{
			createNewConfigFile();
		}
		populateConfig();
	}

	public static synchronized QPConfigManager getInstance(String _configXMLFileLocation) throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
	{
		return getInstance(_configXMLFileLocation, false); 
	}
	
	public static synchronized QPConfigManager getInstance(String _configXMLFileLocation, boolean createNewConfigFile) throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
	{
		if(cache == null)
		{
			cache = new HashMap<String, QPConfigManager>();
		}
				
		QPConfigManager instance = cache.get(_configXMLFileLocation);
		if (instance == null)         
		{
			cache.put(_configXMLFileLocation, instance = new QPConfigManager(_configXMLFileLocation, createNewConfigFile)); 
		}
		return instance;  
	}
	
	private void createNewConfigFile() throws IOException
	{
		File configFile = new File(configXMLFileLocation);
		if(!configFile.exists())
		{
			createDefaultConfigStructure();
		}
	}
	
	private void createDefaultConfigStructure() throws IOException
	{
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<root>");
		sb.append("</root>");
		QPWriter writer = new QPWriter();
		writer.writeStringToFile(sb.toString(), configXMLFileLocation, false, true);
	}
	
	private void populateConfig() throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
	{
		configEntryValues = new HashMap<String, Object>();
		configListValues = new HashMap<String, List<String>>();
		this.configMapValues = new HashMap<String, Map<String, String>>();
		
		if(displayDebugInfo.equals(QPDisplayDebugInfo.displayDebugInfo))
		{
			//System.out.println("Config File Path: " + configFile.getAbsolutePath());
			System.out.println();
			System.out.println("Adding the following {key} : {value} pairs:");
			System.out.println("___________________________________________");
			System.out.println();
		}
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db = null;
		Document configDocument = null;
		
		db= dbf.newDocumentBuilder();
		//configDocument = db.parse(configFile);
		configDocument = db.parse(configXMLFileLocation);
				
		configDocument.getDocumentElement().normalize();
		
		NodeList configEntryNodeList = configDocument.getElementsByTagName("entry");
		
		for(int i = 0; i< configEntryNodeList.getLength(); i++)
		{
			Node entryNode = configEntryNodeList.item(i);
			if(entryNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element entryElement = (Element)entryNode;
				NodeList keys = entryElement.getElementsByTagName("key");
				String key =((Element)keys.item(0)).getTextContent();
				if(configEntryValues.containsKey(key))
				{
					throw new QPConfig_Exception("Unable to populate the Config Entry Values.  Duplicate Key found in the config file. Key name: \"" + key + "\"");
				}
				// get the value
				Element valueElement = (Element) entryElement.getElementsByTagName("value").item(0);
				Object value = null;
				// see if there is a type and if so, handle the type-casting
				if(valueElement.getAttribute("type").length() > 0)
				{
					try {
						value = parseValue(valueElement, valueElement.getAttribute("type"));
					} catch (JAXBException e) {
						throw new QPConfig_Exception(e.getMessage(), e);
					} catch (ClassNotFoundException e) {
						throw new QPConfig_Exception(e.getMessage(), e);
					}
				}
				else
				{
					// assume this is a string value
					value = QPEncodeDecode.htmlDecodeValue(valueElement.getTextContent());
				}
				

				if(displayDebugInfo.equals(QPDisplayDebugInfo.displayDebugInfo))
				{
					System.out.println(key + "  :  " + value.toString());
				}
				configEntryValues.put(key, value);
			}
		}
		
		NodeList configListNodeList = configDocument.getElementsByTagName("list");
		
		for(int i = 0; i< configListNodeList.getLength(); i++)
		{
			Node arrayNode = configListNodeList.item(i);
			if(arrayNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element arrayElement = (Element)arrayNode;
				NodeList keys = arrayElement.getElementsByTagName("key");
				String key =((Element)keys.item(0)).getTextContent();
				if(configListValues.containsKey(key))
				{
					throw new QPConfig_Exception("Unable to populate the Config List Values.  Duplicate Array Key found in the config file. Key name: \"" + key + "\"");
				}
				NodeList values = arrayElement.getElementsByTagName("value");
				List<String> currentList = new ArrayList<String>();
				
				for(int a = 0; a < values.getLength(); a++)
				{
					String value = QPEncodeDecode.htmlDecodeValue(((Element)values.item(a)).getTextContent());
					currentList.add(value);
					if(displayDebugInfo.equals(QPDisplayDebugInfo.displayDebugInfo))
					{
						System.out.println(key + "  :  " + value);
					}
				}
				configListValues.put(key, currentList);
			}
		}
		
		NodeList configMapNodeList = configDocument.getElementsByTagName("map");
		for(int i = 0; i < configMapNodeList.getLength(); i++) {
			Node arrayNode = configMapNodeList.item(i);
			if(arrayNode.getNodeType() == Node.ELEMENT_NODE)
			{
				Element arrayElement = (Element)arrayNode;
				String mapKey = arrayElement.getAttribute("id");
				Map<String, String> entryMap = new HashMap<String, String>();
				NodeList nl = arrayElement.getElementsByTagName("entry");
				for(int j = 0; j < nl.getLength(); j++) {
					Node nEntry = nl.item(j);
					if(nEntry.getNodeType() == Node.ELEMENT_NODE) {
						Element entry = (Element)nEntry;
						NodeList keys = entry.getElementsByTagName("key");
						String key =((Element)keys.item(0)).getTextContent();
						if(configListValues.containsKey(key))
						{
							throw new QPConfig_Exception("Unable to populate the Config List Values.  Duplicate Array Key found in the config file. Key name: \"" + key + "\"");
						}
						NodeList values = entry.getElementsByTagName("value");
						String value =((Element)values.item(0)).getTextContent();
						if(configListValues.containsKey(key))
						{
							throw new QPConfig_Exception("Unable to populate the Config List Values.  Duplicate Array Key found in the config file. Key name: \"" + key + "\"");
						}
						entryMap.put(key, value);
					}
				}
				this.configMapValues.put(mapKey, entryMap);
			}
		}
	}
	
	/**
	 * For value elements that have a type attribute. This function attempts to parse or
	 * marshal the value of the element into its respective object type. 
	 * @param valueElement the 'value' element
	 * @param type the fully qualified class name of the intended type. Ex: "org.w3c.dom.Document"
	 * @return Object of requested type
	 * @throws ParserConfigurationException
	 * @throws ClassNotFoundException 
	 * @throws JAXBException 
	 */
	private Object parseValue(Node valueElement, String type) throws ParserConfigurationException, JAXBException, ClassNotFoundException {
		
		Object obj = null;
		Element root = null;
		NodeList nodes = valueElement.getChildNodes();

		// get the first 'root' element of this Node
		// xml must be well formed within the Value element (having only one root node)
		// or only the first element will be considered the root node.
		for(int i = 0; i<nodes.getLength(); i++)
		{
			Node node = nodes.item(i);
			if(node.getNodeType() == Node.ELEMENT_NODE)
			{
				root = (Element) node;
				break;
			}	
		}
		
		// if this is intended to be a Document object, parse accordingly
		if(type.equalsIgnoreCase("org.w3c.dom.Document"))
		{
			obj = QPXml.documentTypeFromNode(root);
		}
		else
		{
			// we assume this to be a JAX-B class and attempt to marshall it...
			obj = JAXBUtil.unmarshal(QPXml.documentTypeFromNode(root), type);

		}
		return obj;
	}

	public void showConfigValues()
	{
		Set<Entry<String, Object>> set = configEntryValues.entrySet();
	    Iterator<Map.Entry<String, Object>> i = set.iterator();

	    while(i.hasNext())
	    {	
	    	Map.Entry<String, Object> me = (Map.Entry<String, Object>)i.next();
	    	System.out.println(me.getKey() + " : " + QPEncodeDecode.htmlDecodeValue(me.getValue().toString()));
	    }
	    
	    Set<Entry<String, List<String>>> arraySet = configListValues.entrySet();
	    Iterator<Entry<String, List<String>>> iArray = arraySet.iterator();

	    while(iArray.hasNext())
	    {	
	    	Map.Entry<String, List<String>> arrayMe = (Map.Entry<String, List<String>>)iArray.next();
	    	System.out.println(arrayMe.getKey() + " -Contains:");
	    	List<String> currentList = arrayMe.getValue();
	    	for(int cl = 0; cl < currentList.size(); cl++)
	    	{
	    		System.out.println(QPEncodeDecode.htmlDecodeValue(currentList.get(cl)));
	    	}
	    }
	}
	
	/**
	 * Gets the string value of the value.
	 * In the event that the value is not a String type
	 * default behaviour returns the object id.
	 * @param key
	 * @return the String value of of the entry or null if nothing is found.
	 */		
	public String getStringValue(String key)
	{
		return (configEntryValues.get(key) == null) ? null : configEntryValues.get(key).toString();
	}
	
	/**
	 * Returns the object value of the value. It
	 * is up to the caller to know how to cast the
	 * Object.
	 * @param key
	 * @return
	 */
	public Object getValue(String key)
	{
		return configEntryValues.get(key);
	}
	
	public List<String> getListValue(String key)
	{
		return configListValues.get(key);
	}
	
	public Map<String, String> getMapValue(String key) {
		return this.configMapValues.get(key);
	}
	
	public void changeStringConfigValue(String key, String value) throws QPConfig_Exception, IOException
	{
		changeStringConfigValue(key, value, false);
	}
	
	public void changeStringConfigValue(String key, String value, boolean create) throws QPConfig_Exception, IOException
	{
		if(!create)
		{
			if(!configEntryValues.containsKey(key)) 
			{
				throw new QPConfig_Exception("Unable to update String Config Value.  Key does not exist in config file.  Key name: \"" + key +"\"");
			}
		}
		configEntryValues.put(key, value);
		writeConfig(configXMLFileLocation);
	}
	
	public void changeListConfigValue(String key, List<String> value) throws QPConfig_Exception, IOException
	{
		changeListConfigValue(key, value, false);
	}
	
	public void changeListConfigValue(String key, List<String> value, boolean create) throws QPConfig_Exception, IOException
	{
		if(!create)
		{	
			if(!configListValues.containsKey(key)) 
			{
				throw new QPConfig_Exception("Unable to update List Config Value.  Key does not exist in config file.  Key name: \"" + key +"\"");				
			}
		}
		configListValues.put(key, value);
		writeConfig(configXMLFileLocation);		
	}
	
	public void addStringConfigEntry(String key, String value) throws QPConfig_Exception, IOException
	{
		if(configEntryValues.containsKey(key)) 
		{
			throw new QPConfig_Exception("Unable to add Config Entry.  Key already exists in config file.  Key name: \"" + key + "\"");
		}
		else
		{
			configEntryValues.put(key, value);
			writeConfig(configXMLFileLocation);
		}
	}
	
	public void addListConfigEntry(String key, List<String> value) throws QPConfig_Exception, IOException
	{
		if(configListValues.containsKey(key)) 
		{
			throw new QPConfig_Exception("Unable to add List Config Entry.  Key already exists in config file.  Key name: \"" + key + "\"");
		}
		else
		{
			configListValues.put(key, value);
			writeConfig(configXMLFileLocation);
		}
	}
	
	public void deleteStringConfigEntry(String key) throws QPConfig_Exception, IOException
	{
		if(configEntryValues.containsKey(key))
		{
			configEntryValues.remove(key);
			writeConfig(configXMLFileLocation);
		}
		else
		{
			throw new QPConfig_Exception("Unable to delete String Config Entry.  Key does not exist in config file.  Key name: \"" + key + "\"");
		}
	}
	
	public void deleteListConfigEntry(String key) throws QPConfig_Exception, IOException
	{
		if(configListValues.containsKey(key))
		{
			configListValues.remove(key);
			writeConfig(configXMLFileLocation);
		}
		else
		{
			throw new QPConfig_Exception("Unable to delete List Config Entry.  Key does not exist in config file.  Key name: \"" + key + "\"");
		}
	}
	
	public void saveConfig(String configLocation) throws IOException
	{
		writeConfig(configLocation);
	}
	
	public void saveConfig() throws IOException
	{
		writeConfig(configXMLFileLocation);
	}
	
	private void writeConfig(String configLocation) throws IOException
	{
		if(displayDebugInfo.equals(QPDisplayDebugInfo.displayDebugInfo))
		{
			System.out.println();
			System.out.println("Saving Config file to: " + configLocation);
			System.out.println();
		}
		StringBuilder sb = new StringBuilder();
		sb.append("<?xml version=\"1.0\"?>");
		sb.append("<root>");
		
		//writing Strings
		Set<Map.Entry<String, Object>> set = configEntryValues.entrySet();
	    Iterator<Map.Entry<String, Object>> i = set.iterator();

	    while(i.hasNext())
	    {
	    	sb.append("<entry>");
	    	Map.Entry<String, Object> me = (Map.Entry<String, Object>)i.next();
	    	sb.append("<key>");
	    	sb.append(me.getKey());
	    	sb.append("</key>");
	    	sb.append("<value>");
	    	sb.append(QPEncodeDecode.htmlEncodeValue((String) me.getValue()));
	    	sb.append("</value>");
	      	sb.append("</entry>");
	    }
	    
	    //writing Lists
	    Set<Entry<String, List<String>>> arraySet = configListValues.entrySet();
	    Iterator<Entry<String, List<String>>> iArray = arraySet.iterator();

	    while(iArray.hasNext())
	    {	
	    	sb.append("<list>");
	    	Map.Entry<String, List<String>> arrayMe = (Map.Entry<String, List<String>>)iArray.next();
	    	sb.append("<key>");
	    	sb.append(arrayMe.getKey());
	    	sb.append("</key>");
	    	List<String> currentList = arrayMe.getValue();
	    	for(int cl = 0; cl < currentList.size(); cl++)
	    	{
	    		sb.append("<value>");
	    		sb.append(QPEncodeDecode.htmlEncodeValue(currentList.get(cl)));
	    		sb.append("</value>");
	    	}
	    	sb.append("</list>");
	    }
	    
		sb.append("</root>");
		
		qputility.io.QPWriter writer = new QPWriter();
		writer.writeStringToFile(sb.toString(), configLocation, false, true);		
	}
	
	/**
	 * @param args
	 * @throws QPConfig_Exception 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	public static void main(String[] args) throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
	{
		try
		{
			System.out.println("start");
			System.out.println(QPConfigManager.getInstance("http://styles.qp.gov.bc.ca/bccodes/ConstructionCodesCompareConfig.xml").getStringValue("hierarchyCount"));
		}
		catch (Exception e)
		{
			System.out.println(e.getMessage());
		}
//		String config1 = "D:\\Current Projects\\config manager\\Config (1)eee.xml";
//		
//		String  crap = QPConfigManager.getInstance(config1, true).getStringValue("craP");
//		
//		if(crap != null && crap != "")
//		{
//			System.out.println("found");
//		}
		
		System.out.println("asdf");
//		QPConfigManager.getInstance(config1);
//		QPConfigManager.getInstance(config1).showConfigValues();
		
//		System.out.println("~~~~~~~~~~~``");
//		System.out.println("~~~~~~~~~~~``");
//		System.out.println("~~~~~~~~~~~``");
//		QPConfigManager.getInstance(config2).showConfigValues();
//		System.out.println("~~~~~~~~~~~``");
//		System.out.println("~~~~~~~~~~~``");
//		System.out.println("~~~~~~~~~~~``");
//		
//		QPConfigManager.getInstance(config1).changeStringConfigValue("c1", "my new value1");
//		System.out.println("~~~~~~~~~~~``");
//		System.out.println("~~~~~~~~~~~``");
//		System.out.println("~~~~~~~~~~~``");
//		QPConfigManager.getInstance(config1).showConfigValues();
//		System.out.println("~~~~~~~~~~~``");
//		System.out.println("~~~~~~~~~~~``");
//		System.out.println("~~~~~~~~~~~``");
//		QPConfigManager.getInstance(config2).showConfigValues();
		//QPConfigManager.getInstance(config2).changeStringConfigValue("displayDebugInfo", "my change");
		
		
		
		//System.out.println(QPConfigManager.configXMLFileLocation);
//		QPConfigManager.getInstance().initialize("../ReconSiteFileBuilder\\ReconSiteFileBuilderConfig.xml", QPDisplayDebugInfo.displayDebugInfo);
//		QPConfigManager.getInstance().showConfigValues();
//		ArrayList arrayName = QPConfigManager.getInstance().getArrayValue("arrayName");
//		
//		System.out.println("arrayName");
//		for(int i = 0; i < arrayName.size(); i++)
//		{
//			System.out.println(arrayName.get(i));
//		}
//		
//		arrayName.remove("second value");
//		
//		QPConfigManager.getInstance().changeArrayConfigValue("arrayName", arrayName);
//		
//		ArrayList<String> test = QPConfigManager.getInstance().getArrayValue("arrayName");
//		System.out.println("arrayName2");
//		for(int i = 0; i < test.size(); i++)
//		{
//			System.out.println(test.get(i));
//		}
//		
//		ArrayList test2 = new ArrayList<String>();
//		
//		test2.add("this is a new value");
//		test2.add("this is a new value2");
//		test2.add("this is a new value3");
//		test2.add("this is a new value4");
//		test2.add("this is a new value5");
//		test2.add("this is a new value6");
////		
//		QPConfigManager.getInstance().addArrayConfigEntry("my new array list", test2);
		
		//QPConfigManager.getInstance().deleteArrayConfigEntry("my new array list");
//		QPConfigManager.getInstance().deleteArrayConfigEntry("arrayName");
//		QPConfigManager.getInstance().deleteArrayConfigEntry("testArrayName");
//		QPConfigManager.getInstance().deleteStringConfigEntry("pathToExcludedFiles.xml");
//		QPConfigManager.getInstance().saveConfig();
//		QPConfigManager.updateConfigValue("testing2", "new value");
//		//System.out.println("~~~~~~~~~~~``");
//		QPConfigManager.showConfigValues();
//		//QPConfigManager.removeConfigEntry("testing");
//		//System.out.println("~~~~~~~~~~~``");
//		//QPConfigManager.showConfigValues();
		//QPConfigManager.saveConfig("D:\\Test Environment\\crap.txt");
//		QPConfigManager.initialize();
//		QPConfigManager.saveConfig();
//		QPConfigManager.updateConfigValue("testing2", "new value22");
//		QPConfigManager.saveConfig();
//		//System.out.println(config.getValue("testingee"));
	}
}


//public void initialize() throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
//{
//	populateConfig();
//}
//
//public void initialize(QPDisplayDebugInfo _displayDebugInfo) throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
//{
//	displayDebugInfo = _displayDebugInfo;
//	populateConfig();
//}
//
//public void initialize(String _configXMLFileLocation) throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
//{
//	configXMLFileLocation = _configXMLFileLocation;
//	populateConfig();
//}
//
//public void initialize(String _configXMLFileLocation, QPDisplayDebugInfo _displayDebugInfo) throws QPConfig_Exception, ParserConfigurationException, SAXException, IOException
//{
//	displayDebugInfo = _displayDebugInfo;
//	configXMLFileLocation = _configXMLFileLocation;
//	populateConfig();
//}
