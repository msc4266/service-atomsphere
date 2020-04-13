package com.manywho.services.atomsphere;

import static org.junit.Assert.*;
import org.junit.Test;

import java.io.IOException;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.manywho.sdk.api.draw.elements.type.TypeElement;
import com.manywho.sdk.api.draw.elements.type.TypeElementProperty;
import com.manywho.services.atomsphere.database.ServiceMetadata;

public class ServiceMetadataTest {

	@Test
	public void testGetAllTypesMetadata() throws SAXException, IOException, ParserConfigurationException {
		ServiceMetadata serviceMetadata = new ServiceMetadata();
		List<TypeElement> typeElements=serviceMetadata.getAllTypesMetadata();
		for (TypeElement typeElement:typeElements)
		{
				System.out.println(typeElement.getDeveloperName());
				for (TypeElementProperty typeElementProperty : typeElement.getProperties())
				{
					System.out.println( " " + typeElementProperty.getDeveloperName() + " " + typeElementProperty.getContentType().name());
				}
		}
		assertTrue(typeElements.size()>0);
	}
	
	//TODO test all CRUD driven by whitelist.json
	//TODO test each action
}
