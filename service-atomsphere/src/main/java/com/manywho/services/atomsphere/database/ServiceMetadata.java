package com.manywho.services.atomsphere.database;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.UUID;

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
	
	Element atomsphereXSDTopElement;
	Element apimXSDTopElement;
	JSONArray atomsphereObjectList;
	JSONArray apimObjectList;
	Logger logger;
	List<TypeElement> _typeElements;
//    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);
	static final String atomsphereXSDFile = "atomsphere.xsd";
	static final String atomsphereObjectListFile = "atomsphereObjectList.json";
	static final String apimXSDFile = "apim.xsd"; //https://api.boomi.com/apim/api/soap/v1/boomi_davehock-T9DOG4?xsd=3
	//TODO can we get the object list from the top level elements of the xsd? Still need a way to designate the odd primary key/id though
	static final String apimObjectListFile = "apimObjectList.json";
	String _componentType = null;
	String _fullComponentName = null;
	Element componentTopElement = null;
	DocumentBuilderFactory factory;
	DocumentBuilder builder;
	
	public ServiceMetadata () throws SAXException, IOException, ParserConfigurationException
	{
		factory = DocumentBuilderFactory.newInstance();
		builder = factory.newDocumentBuilder();
		Document doc = builder.parse(this.getClass().getClassLoader().getResourceAsStream(atomsphereXSDFile));
		atomsphereXSDTopElement = doc.getDocumentElement();
		doc = builder.parse(this.getClass().getClassLoader().getResourceAsStream(apimXSDFile));
		apimXSDTopElement = doc.getDocumentElement();
		InputStream is = this.getClass().getClassLoader().getResourceAsStream(atomsphereObjectListFile);
		atomsphereObjectList = (new JSONObject(new JSONTokener(is)).getJSONArray("objectlist"));
		is=this.getClass().getClassLoader().getResourceAsStream(apimObjectListFile);
		apimObjectList = (new JSONObject(new JSONTokener(is)).getJSONArray("objectlist"));
        logger = Logger.getLogger(this.getClass().getName());
        logger.info("ServiceMetadata TODO can we cache this?");
        initAllTypeElements();
	}

	//Note apim and atomsphere have no entity name clashes
	public JSONObject getObjectListEntry(String entityName)
	{
		for (Object obj : atomsphereObjectList)
		{
			JSONObject jObj = (JSONObject) obj;
			if (jObj.getString("name").contentEquals(entityName))
				return jObj;
		}
		for (Object obj : apimObjectList)
		{
			JSONObject jObj = (JSONObject) obj;
			if (jObj.getString("name").contentEquals(entityName))
				return jObj;
		}
		return null;
	}
	
	public boolean isAPIManagerEntity(String entityName)
	{
		for (Object obj : apimObjectList)
		{
			JSONObject jObj = (JSONObject) obj;
			if (jObj.getString("name").contentEquals(entityName))
				return true;
		}
		return false;
	}

	//TODO FUGLY but needed by Database to now which to use for the mObject.externalId
	public String getPrimaryKey(String entityName)
	{
		JSONObject entry = getObjectListEntry(entityName);
		if (entry==null)
			return null;
		return entry.getString("primaryKey");
	}

	public List<TypeElement> getAllTypeElements()
	{
		return _typeElements;
	}
	
	public TypeElement findTypeElement(String name)
	{
		for(TypeElement typeElement:_typeElements)
		{
			if (typeElement.getDeveloperName().contentEquals(name))
				return typeElement;
		}
		return null;
	}
	
	private void initAllTypeElements() throws SAXException, IOException, ParserConfigurationException
	{
		_typeElements = Lists.newArrayList();
		
		for (Object obj : atomsphereObjectList)
		{
			JSONObject entity = (JSONObject)obj;
			String name = entity.getString("name");			
			this.getTypeElementsForObject(null, name, this.atomsphereXSDTopElement, "Atomsphere");
			if ("Component".contentEquals(name))
			{
				//TODO repeat this for every component type
				//TODO process shapes have "configuration" which needs expanding for every shape type...yuck
				injectComponentType("process");
			}
 		}    
		for (Object obj : apimObjectList)
		{
			JSONObject entity = (JSONObject)obj;
			String name = entity.getString("name");			
			this.getTypeElementsForObject(null, name, this.apimXSDTopElement, "API Manager");
 		}    
		resolveComplexPropertyIDReferences(_typeElements);
	}
	
	private void injectComponentType(String string) throws SAXException, IOException, ParserConfigurationException {
		_fullComponentName=null;
		_componentType="process";
		Document doc = builder.parse(this.getClass().getClassLoader().getResourceAsStream("component_schemas/" + _componentType + "/" + _componentType + ".xsd"));
		componentTopElement = doc.getDocumentElement();	
		this.getTypeElementsForObject(null, _componentType, componentTopElement, "Component");
		
		//now we need to create another component where the "object" has "process" as a child reference
		_fullComponentName = "Component___" + _componentType;
		this.getTypeElementsForObject(null, "Component", atomsphereXSDTopElement, _fullComponentName);
	}

	public boolean getTypeElementsForObject(String parentType, String typeName, Element xsdTopElement, String developerSummary) throws SAXException, IOException, ParserConfigurationException
	{
		boolean success=false;
		TypeElement typeElement = new TypeElement();
		String developerName = typeName;
		if (parentType!=null) {
			typeElement.setDeveloperSummary("Parent type: " + parentType);			
		} else {
			typeElement.setDeveloperSummary(developerSummary);
			if (_fullComponentName!=null)
			{
				developerName = this._fullComponentName;
			}
		}
		typeElement.setDeveloperName(developerName);

    	List<TypeElementProperty> typeElementProperties = Lists.newArrayList();
    	typeElement.setProperties(typeElementProperties);
    	
    	List<TypeElementBinding> typeElementBindings = Lists.newArrayList();
    	typeElement.setBindings(typeElementBindings);
    	
		List<TypeElementPropertyBinding> typeElementPropertyBindings = Lists.newArrayList();
		TypeElementBinding typeElementBinding = new TypeElementBinding(typeElement.getDeveloperName(), "The binding for " + typeElement.getDeveloperName(), typeElement.getDeveloperName(), typeElementPropertyBindings);
		typeElementBindings.add(typeElementBinding);

		populatePropertiesForComplexType(typeName, typeElement, typeElementPropertyBindings, xsdTopElement);
		
		//TODO FUGLY way to add this oddball query filter only property for Process
		if ("Process".contentEquals(typeName))
		{
			//Add filter-only fields
			this.addPropertyAndBinding("integrationPackInstanceId", ContentType.String, typeElement, typeElementPropertyBindings, "xs:string");
			this.addPropertyAndBinding("integrationPackId", ContentType.String, typeElement, typeElementPropertyBindings, "xs:string");
		}
		typeElement.setId(UUID.randomUUID());
       	if (typeElement.getProperties().size()>0)
       	{
       		if (findTypeElement(developerName)==null) //No duplicates
       			_typeElements.add(typeElement);
       		else
       			logger.info("Type used multiple times: " + typeName + " " + parentType);
       		success = true;
       	}
       	else
       		logger.fine("Type has no properties and is excluded: " + typeElement.getDeveloperName());

       	return success;
	}
	
	private Element findComplexType(String typeName, Element xsdTopElement)
	{
        NodeList items = xsdTopElement.getElementsByTagName("xs:complexType");
    	if (items != null) {
           	for (int x=0; x<items.getLength(); x++)
        	{
           		Node item = items.item(x);
           		if (item.hasAttributes())
           		{
           			Node type = item.getAttributes().getNamedItem("name");
           			String typeNameCandidate = type.getNodeValue();
           			if (typeName !=null && typeNameCandidate.contentEquals(typeName))
           				return (Element) item;
           		}
        	}
    	}
    	return null;
	}
	
	//TODO resolve extension recursively to get elements and attributes from them
    private void populatePropertiesForComplexType(String typeName, TypeElement typeElement, List<TypeElementPropertyBinding> typeElementPropertyBindings, Element xsdTopElement) throws SAXException, IOException, ParserConfigurationException {

    	//TODO There are filter only fields that don't appear in root of object type. IE if typeName == PROCESS add a filterable root item named integrationPackInstanceId
    	//TODO these fields are specified in xsd <xs:annotation><xs:appinfo><filter ignore="true" xmlns="http://www.boomi.com/connector/annotation"><field name="integrationPackOverrideName"/><field name="integrationPackName"/><field name="integrationPackId"/></filter></xs:appinfo></xs:annotation>
    	//TODO This can be done by hacking the xsd or we could enhance to add missing fields from the <xs:annotation><xs:appinfo><filter elements
        //Find type element for Entity
		Element complexType = findComplexType(typeName, xsdTopElement);
		if (complexType==null)
		{
			NodeList elements = xsdTopElement.getElementsByTagName("xs:element");
			for (int j=0; j<elements.getLength(); j++)
			{
				Node xsdElement = elements.item(j);
				if (xsdElement.hasAttributes())
				{
					Node nameElement = xsdElement.getAttributes().getNamedItem("name");
					if (nameElement!=null && typeName.contentEquals(nameElement.getTextContent()))
					{
						NodeList complexTypes = ((Element)xsdElement).getElementsByTagName("xs:complexType");
						complexType = (Element) complexTypes.item(0);
						logger.info(complexType.toString());
						break;
					}
				}
				{
//					addPropertyAndBindingFromXSD(typeName, (Node)xsdElement, typeElement, typeElementPropertyBindings, xsdTopElement);
				}
			}
		}
		if (complexType!=null)
		{
				NodeList elements = complexType.getElementsByTagName("xs:element");
   				
				//JSON payloads have elements and attributes as side by side elements
				for (int j=0; j<elements.getLength(); j++)
				{
					Node xsdElement = elements.item(j);
					if (xsdElement.hasAttributes() && xsdElement.getAttributes().getNamedItem("type")!=null)
					{
						addPropertyAndBindingFromXSD(typeName, (Node)xsdElement, typeElement, typeElementPropertyBindings, xsdTopElement);
					}
				}
				NodeList attributes = complexType.getElementsByTagName("xs:attribute");
				for (int j=0; j<attributes.getLength(); j++)
				{
					Node xsdAttribute = attributes.item(j);
					if (xsdAttribute.hasAttributes())
					{
						addPropertyAndBindingFromXSD(typeName, (Node)xsdAttribute, typeElement, typeElementPropertyBindings, xsdTopElement);
					}
				}           	
				NodeList extensions = complexType.getElementsByTagName("xs:extension");
				for (int j=0; j<extensions.getLength(); j++)
				{
					Node xsdAttribute = extensions.item(j);
					if (xsdAttribute.hasAttributes() && xsdAttribute.getAttributes().getNamedItem("base")!=null)
					{
						String extensionName = xsdAttribute.getAttributes().getNamedItem("base").getNodeValue();
						extensionName=getLocalName(extensionName);
						populatePropertiesForComplexType(extensionName, typeElement, typeElementPropertyBindings, xsdTopElement);
					}
				}           	
		} else {
			if (!typeName.contentEquals("BaseType"))
				logger.fine("***Warning type not found:" + typeName);
		}
    }
    
	void addPropertyAndBindingFromXSD(String typeName, Node xsdElement, TypeElement typeElement, List<TypeElementPropertyBinding> typeElementPropertyBindings, Element xsdTopElement) throws SAXException, IOException, ParserConfigurationException
	{
		String developerName = xsdElement.getAttributes().getNamedItem("name").getNodeValue();
		if (xsdElement.getAttributes().getNamedItem("type")==null)
		{
			logger.warning("addPropertyAndBindingFromXSD" + typeName + " " + developerName );
			return;
		}
		String xsdType = xsdElement.getAttributes().getNamedItem("type").getNodeValue();
		
		boolean isList=false;
		Node maxOccursAttribute = xsdElement.getAttributes().getNamedItem("maxOccurs");
		if (maxOccursAttribute!=null && maxOccursAttribute.getNodeValue().contentEquals("unbounded"))
			isList=true;

		ContentType contentType;
		if ("object".contentEquals(developerName) && this._fullComponentName!=null)
		{
			logger.info("object found");
			//TODO we set the type of object to componentType
			//TODO at runtime we need to remove the child element of object because it will have an extra level
			// for example object/process/shape becomes object/shape
			contentType=ContentType.Object;
			xsdType=_componentType;
		} else 
		
			contentType = contentTypeFromXSDType(typeName, xsdType, isList, xsdTopElement);
		
		if (contentType==null) { //if no type we default to String
			contentType=ContentType.String;
			logger.fine(String.format("%s has unsupported Type: %s referenced by %s.%s", typeElement.getDeveloperName(), getLocalName(xsdType), typeName, developerName));
		}
		addPropertyAndBinding(developerName, contentType, typeElement, typeElementPropertyBindings, xsdType);				
	}
	
	private void addPropertyAndBinding(String developerName, ContentType contentType, TypeElement typeElement,
			List<TypeElementPropertyBinding> typeElementPropertyBindings, String xsdType) {
		TypeElementProperty typeElementProperty = new TypeElementProperty();
		typeElement.getProperties().add(typeElementProperty);
		typeElementProperty.setDeveloperName(developerName);
		typeElementProperty.setContentType(contentType);	
		typeElementProperty.setTypeElementId(typeElement.getId());
		typeElementProperty.setId(UUID.randomUUID());
		
		TypeElementPropertyBinding typeElementPropertyBinding = new TypeElementPropertyBinding(developerName, developerName, getLocalName(xsdType));
		typeElementPropertyBindings.add(typeElementPropertyBinding);
		typeElementPropertyBinding.setTypeElementPropertyId(typeElementProperty.getId());
		//TODO I think we wait until the end and loop to apply binding ids at that time
		if (contentType==ContentType.Object || contentType == ContentType.List)
		{
			String complexTypeName = getLocalName(xsdType);
			typeElementProperty.setTypeElementDeveloperName(complexTypeName);
		}			
	}

	void resolveComplexPropertyIDReferences(List<TypeElement> typeElements)
	{
		for (TypeElement typeElement : typeElements)
		{
			for (TypeElementProperty typeElementProperty : typeElement.getProperties())
			{
				if (typeElementProperty.getContentType() == ContentType.List || typeElementProperty.getContentType() == ContentType.Object)
				{
					TypeElement propertyTypeElement = findComplexTypeElement(typeElements, typeElementProperty.getTypeElementDeveloperName());
					typeElementProperty.setTypeElementId(propertyTypeElement.getId());
				}
			}
		}
	}

	TypeElement findComplexTypeElement(List<TypeElement> typeElements, String developerName)
	{
		for (TypeElement typeElement : typeElements)
		{
			if (typeElement.getDeveloperName().contentEquals(developerName))
				return typeElement;
		}
		return null;
	}

	private ContentType contentTypeFromXSDType(String parentType, String xsdType, boolean isList, Element xsdTopElement) throws SAXException, IOException, ParserConfigurationException
    {
    	ContentType  contentType = null;
    	switch (xsdType)
    	{
    	case "xs:string":
    	case "xs:anyType":
    	case "xs:base64Binary":
    		contentType = ContentType.String;   	
    		break;
    	case "xs:int":
    	case "xs:integer":
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
    		xsdType = getLocalName(xsdType);
    		if (xsdType!=null)
    		{
        		contentType = resolveComplexType(parentType, xsdType, xsdTopElement);
        		//recursive to get sub types 
        		//License, ConnectorVersion, Counter, MapExtension, AtomSecurityPoliciesType, 
        		//Property, AuditLogProperty, CloudAtom, DocumentCountAccountGroup, ExecutionCountAccountGroup, DeployedProcess, ProcessIntegrationPackInfo...     		
            	if (contentType==null)
        		{
        			if(getTypeElementsForObject(parentType, xsdType, xsdTopElement, ""))
        			{
        				if (isList)
            				contentType = ContentType.List;
        				else
        					contentType = ContentType.Object;
        			}
        		}      		
    		}
    		break;
    	}
    	return contentType;
    }
	
	String getLocalName(String name)
	{
		String local;
		String localName[] = name.split(":");
		if (localName.length==2)
			local = localName[1];
		else
			local = localName[0];
		return local;
	}
	
	ContentType resolveComplexType(String parentType, String xsdType, Element xsdTopElement) throws SAXException, IOException, ParserConfigurationException
	{
		ContentType contentType = null;
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
           					contentType=this.contentTypeFromXSDType(parentType, subType,false,xsdTopElement);
           				}
           				break;
           			}
           		}
        	}
    	}
        return contentType;
	}	
	
	public static TypeElementProperty getProperty(TypeElement typeElement, String propertyName)
	{
		for(TypeElementProperty property:typeElement.getProperties())
		{
			if (property.getDeveloperName().contentEquals(propertyName))
				return property;
		}
		return null;
	}
	
	public static ContentType getPropertyType(TypeElement typeElement, String propertyName)
	{
		TypeElementProperty property = getProperty(typeElement, propertyName);
		if (property!=null)
			return property.getContentType();
		return ContentType.String;
	}
}
