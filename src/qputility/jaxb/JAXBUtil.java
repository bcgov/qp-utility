package qputility.jaxb;

import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;

import qputility.xml.QPXml;

/**
 * @author chris.ditcher
 * Class containing methods for marshalling and unmarshalling jaxb
 * xml and classes.
 */
public class JAXBUtil {
	
	/**
	 * Method for un-marshalling xml into a java object.
	 * @param is a valid input stream for an xml file conforming to object schema 
	 * @param classesToBeBound the class (or classes) bound to the context
	 * @return The object type they xml was marshalled into
	 * @throws JAXBException
	 */
	@SuppressWarnings("rawtypes")
	public static Object unMarshall(InputStream is, Class... classesToBeBound) throws JAXBException {
		JAXBContext context = JAXBContext.newInstance(classesToBeBound);
		Unmarshaller unMarshaller = context.createUnmarshaller();
		return unMarshaller.unmarshal(is);
	}
	
	/**
	 * @author chris.ditcher
	 * Marshals a given object to a XML Document.
	 * @param object	A JAXB bean object.
	 * @return			A document object containing the marshaled content.
	 * @throws JAXBException	An error occurred during the marshaling.
	 * @throws ParserConfigurationException	An error occurred while creating the xml document.
	 */
	public static Document marshalToDom(Object object) throws JAXBException, ParserConfigurationException {
		Document doc = null;
		doc = QPXml.getEmptyXMLDoc();
		JAXBContext jc = JAXBContext.newInstance(object.getClass());
		Marshaller m = jc.createMarshaller();
		m.marshal(object, doc);
		return doc;
	}
	

	/**
	 * @author chris.ditcher
	 * Unmarshals a Document Object to the fully qualified class name
	 * provided.
	 * @param doc the xml you wish to unmarshal
	 * @param className the fully qualified class name ex: "com.myorg.MyBean"
	 * @return the Object of the type specified in the className parameter
	 * @throws JAXBException if your bean is bunk
	 * @throws ClassNotFoundException if your class path in not configured correctly
	 */
	public static Object unmarshal(Document doc, String className) throws JAXBException, ClassNotFoundException {
		JAXBContext jc;
		jc = JAXBContext.newInstance(Class.forName(className));
		Unmarshaller u = jc.createUnmarshaller();
		return u.unmarshal(doc);
	}

	/**
	 * @author chris.ditcher
	 * When marshalling classes that have member variables that may have List
	 * objects parameterized with an interface, this method avoids jax-b context
	 * errors when referencing the implementing classes.
	 * 
	 * @param object
	 * @param instances
	 * @return
	 * @throws JAXBException
	 * @throws ParserConfigurationException
	 */
	public static Document marshalToDom(Object object, Class<?>[] instances)
			throws JAXBException, ParserConfigurationException {
		if (instances.length == 0 || instances == null) {
			throw new JAXBException("Instances class array can not be empty.");
		}
		Document doc = null;
		doc = QPXml.getEmptyXMLDoc();
		JAXBContext jc = JAXBContext.newInstance(instances);
		Marshaller m = jc.createMarshaller();
		m.marshal(object, doc);
		return doc;
	}

}
