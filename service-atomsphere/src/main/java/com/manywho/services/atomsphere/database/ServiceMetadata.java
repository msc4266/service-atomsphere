package com.manywho.services.atomsphere.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.google.common.collect.Lists;
import com.manywho.sdk.api.ContentType;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.draw.elements.type.TypeElementPropertyBinding;
import java.util.logging.Logger;

public class ServiceMetadata {
	
	Element xsdTopElement;
	JSONArray objectWhitelist;
	Logger logger;
//    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
	

	public ServiceMetadata () throws SAXException, IOException, ParserConfigurationException
	{
		DocumentBuilderFactory factory =
				DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
		Document doc = builder.parse(this.getClass().getClassLoader().getResourceAsStream("atomsphere.xsd"));
		xsdTopElement = doc.getDocumentElement();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream("objectWhitelist.json");
		objectWhitelist = (new JSONObject(new JSONTokener(is)).getJSONArray("whitelist"));
        logger = Logger.getLogger(this.getClass().getName());
	}

	public JSONObject getWhitelistEntry(String entityName)
	{
		for (Object obj : objectWhitelist)
		{
			JSONObject jObj = (JSONObject) obj;
			if (jObj.getString("name").contentEquals(entityName))
				return jObj;
		}
		return null;
	}
	
	public boolean supportsCreate(String entityName)
	{
		return getWhitelistEntry(entityName).getBoolean("supportsCreate");
	}
	
	public boolean supportsUpdate(String entityName)
	{
		return getWhitelistEntry(entityName).getBoolean("supportsUpdate");
	}
	
	public boolean supportsDelete(String entityName)
	{
		return getWhitelistEntry(entityName).getBoolean("supportsDelete");
	}
	
	public boolean supportsQuery(String entityName)
	{
		return getWhitelistEntry(entityName).getBoolean("supportsQuery");
	}
	
	public boolean supportsGet(String entityName)
	{
		return getWhitelistEntry(entityName).getBoolean("supportsGet");
	}
	
	public String getPrimaryKey(String entityName)
	{
		JSONObject entry = getWhitelistEntry(entityName);
		return entry.getString("primaryKey");
	}
	
	public List<TypeElement> getAllTypesMetadata() throws SAXException, IOException, ParserConfigurationException {
		List<TypeElement> typeElements = Lists.newArrayList();
		
		for (Object obj : objectWhitelist)
		{
			JSONObject entity = (JSONObject)obj;
			String name = entity.getString("name");
			
			TypeElement typeElement = this.getTypeElement(name);

           	if (typeElement.getProperties().size()>0)
           		typeElements.add(typeElement);
           	else
           		logger.warning("Type has no properties and is excluded: " + typeElement.getDeveloperName());
 		}    
		return typeElements;
	}
	
	public TypeElement getTypeElement(String typeName) throws SAXException, IOException, ParserConfigurationException
	{
		TypeElement typeElement = new TypeElement();
		typeElement.setDeveloperName(typeName);
		populatePropertiesForEntity(typeElement);
		return typeElement;
	}
	
    private void populatePropertiesForEntity(TypeElement typeElement) throws SAXException, IOException, ParserConfigurationException {
    	List<TypeElementProperty> typeElementProperties = Lists.newArrayList();
    	typeElement.setProperties(typeElementProperties);
    	List<TypeElementBinding> typeElementBindings = Lists.newArrayList();
    	typeElement.setBindings(typeElementBindings);
		List<TypeElementPropertyBinding> typeElementPropertyBindings = Lists.newArrayList();
		typeElementBindings.add(new TypeElementBinding(typeElement.getDeveloperName(), "The binding for " + typeElement.getDeveloperName(), typeElement.getDeveloperName(), typeElementPropertyBindings));

        //Find type element for Entity
        NodeList items = xsdTopElement.getElementsByTagName("xs:complexType");
        boolean entityFound=false;
    	if (items != null) {
           	for (int x=0; x<items.getLength(); x++)
        	{
           		Node item = items.item(x);
           		if (item.hasAttributes())
           		{
           			Node type = item.getAttributes().getNamedItem("name");
           			String typeName = type.getNodeValue();
           			if (typeName !=null && typeName.contentEquals(typeElement.getDeveloperName()))
           			{
           				entityFound=true;
           				Element complexTypeElement = (Element) item;
           				NodeList elements = complexTypeElement.getElementsByTagName("xs:element");
           				
           				//JSON payloads have elements and attributes as side by side elements
           				for (int j=0; j<elements.getLength(); j++)
           				{
           					Node xsdElement = elements.item(j);
           					if (xsdElement.hasAttributes() && xsdElement.getAttributes().getNamedItem("type")!=null)
           					{
           						addPropertyAndBinding(typeElement.getDeveloperName(), (Node)xsdElement, typeElement, typeElementPropertyBindings);
            				}
          				}
           				NodeList attributes = complexTypeElement.getElementsByTagName("xs:attribute");
           				for (int j=0; j<attributes.getLength(); j++)
           				{
           					Node xsdAttribute = attributes.item(j);
           					if (xsdAttribute.hasAttributes())
           					{
           						addPropertyAndBinding(typeElement.getDeveloperName(), (Node)xsdAttribute, typeElement, typeElementPropertyBindings);
           					}
          				}           				
           				break;
           			}
           		}
        	}
    	}
    	if (entityFound==false)
    		System.out.println("***Warning entity not found:" + typeElement.getDeveloperName());
    }
    
	void addPropertyAndBinding(String typeName, Node xsdElement, TypeElement typeElement, List<TypeElementPropertyBinding> typeElementPropertyBindings)
	{
			String xsdType = xsdElement.getAttributes().getNamedItem("type").getNodeValue();
			String developerName = xsdElement.getAttributes().getNamedItem("name").getNodeValue();
			ContentType contentType = contentTypeFromXSDType(xsdType);
			if (contentType!=null)
			{
				//TODO just simple types for now
				TypeElementProperty typeElementProperty = new TypeElementProperty();
				typeElement.getProperties().add(typeElementProperty);
				typeElementProperty.setDeveloperName(developerName);
				typeElementProperty.setContentType(contentType);				
				TypeElementPropertyBinding typeElementPropertyBinding = new TypeElementPropertyBinding(developerName, developerName, xsdType);
				typeElementPropertyBindings.add(typeElementPropertyBinding);
			} else {
				logger.warning(String.format("Unsupported Type: " + typeName+"."+developerName + " " + xsdType));
			}
	}

	private ContentType contentTypeFromXSDType(String xsdType)
    {
    	ContentType  contentType = null;
    	switch (xsdType)
    	{
    	case "xs:string":
    		contentType = ContentType.String;   	
    		break;
    	case "xs:int":
    	case "xs:long":
    	case "xs:double":
    		contentType = ContentType.Number;   	
    		break;
    	case "xs:dateTime":
    		contentType = ContentType.DateTime;   	
    		break;
    	case "xs:boolean":
    		contentType = ContentType.Boolean;   	
    		break;
    	default:
    		contentType = resolveSimpleType(xsdType);
    		break;
    	}
    	return contentType;
    }
	
	ContentType resolveSimpleType(String xsdType)
	{
		ContentType contentType = null;
		String localName[] = xsdType.split(":");
		if (localName.length==2)
		{
			xsdType = localName[1];
	        NodeList simpleTypes = xsdTopElement.getElementsByTagName("xs:simpleType");
	    	if (simpleTypes != null) {
	           	for (int x=0; x<simpleTypes.getLength(); x++)
	        	{
	           		Node item = simpleTypes.item(x);
	           		if (item.hasAttributes())
	           		{
	           			Node type = item.getAttributes().getNamedItem("name");
	           			String typeName = type.getNodeValue();
	           			if (typeName !=null && typeName.contentEquals(xsdType))
	           			{
	           				NodeList restrictions = ((Element)item).getElementsByTagName("xs:restriction");
	           				if (restrictions.getLength()==1)
	           				{
	           					Node restriction = restrictions.item(0);
	           					String subType=restriction.getAttributes().getNamedItem("base").getNodeValue();
	           					contentType=this.contentTypeFromXSDType(subType);
	           				}
	           				break;
	           			}
	           		}
	        	}
	    	}
		}
        return contentType;
	}	
}
