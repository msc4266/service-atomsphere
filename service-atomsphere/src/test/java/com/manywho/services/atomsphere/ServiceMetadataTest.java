package com.manywho.services.atomsphere;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementBinding;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.sdk.api.draw.elements.type.TypeElementPropertyBinding;
import com.manywho.services.atomsphere.database.ServiceMetadata;


/*

XML NOT SUPPORTED
-----------------

Atomsphere Object GET Component
APIM Object GET Plan Usage
APIM Action Add Plans to Deployment
APIM Action Remove Plans from Deployment
APIM Action Send Changes to Authentication Source
 
 */
public class ServiceMetadataTest {

	@Test
	public void testGetAllTypesMetadata() throws SAXException, IOException, ParserConfigurationException {
		ServiceMetadata serviceMetadata = new ServiceMetadata();
		List<TypeElement> typeElements=serviceMetadata.getAllTypeElements();
		for (TypeElement typeElement:typeElements)
		{
				System.out.println(String.format("Type DeveloperName: %s Id: %s",typeElement.getDeveloperName(), typeElement.getId()));
				for (TypeElementProperty typeElementProperty : typeElement.getProperties())
				{
//					if (typeElementProperty.getDeveloperName()!=null)
					System.out.println(String.format(" Property developerName: %s ID: %s; contentType.Name: %s; typeElementDeveloperName: %s; typeElementId: %s"
							, typeElementProperty.getDeveloperName()
							, typeElementProperty.getId()
							, typeElementProperty.getContentType().name()
							, typeElementProperty.getTypeElementDeveloperName()
							, typeElementProperty.getTypeElementId()));
				}
				for (TypeElementBinding typeElementBinding : typeElement.getBindings())
				{
					System.out.println( " BINDING " + typeElementBinding.getDeveloperName());
					for (TypeElementPropertyBinding typeElementPropertyBinding:typeElementBinding.getPropertyBindings())
					{
						System.out.println(String.format("    TypeElementPropertyDeveloperName: %s;  TypeElementPropertyId: %s", typeElementPropertyBinding.getTypeElementPropertyDeveloperName(), typeElementPropertyBinding.getTypeElementPropertyId()));
					}
				}
		}
		assertTrue(typeElements.size()>0);
	}
	
	//TODO test all CRUD driven by whitelist.json
	//TODO test each action
}
