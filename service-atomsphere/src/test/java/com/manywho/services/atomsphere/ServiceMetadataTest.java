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

Component
Plan Usage
Add Plans to Deployment
Remove Plans from Deployment
Send Changes to Authentication Source

COMPLEX TYPES NOT SUPPORTED
---------------------------
21000: ON CONFLICT DO UPDATE command cannot affect row a second time

Account has unsupported Type: License referenced by Account.standard
Account has unsupported Type: License referenced by Account.smallBusiness
Account has unsupported Type: License referenced by Account.enterprise
Account has unsupported Type: License referenced by Account.tradingPartner

AtomConnectionFieldExtensionSummary has unsupported Type: FieldSummary referenced by ConnectionFieldExtensionSummary.field

AtomConnectorVersions has unsupported Type: ConnectorVersion referenced by AtomConnectorVersions.ConnectorVersion

AtomCounters has unsupported Type: Counter referenced by AtomCounters.counter

AtomExtensions has unsupported Type: Connections referenced by Overrides.connections
AtomExtensions has unsupported Type: Operations referenced by Overrides.operations
AtomExtensions has unsupported Type: TradingPartners referenced by Overrides.tradingPartners
AtomExtensions has unsupported Type: SharedCommunications referenced by Overrides.sharedCommunications
AtomExtensions has unsupported Type: CrossReferences referenced by Overrides.crossReferences
AtomExtensions has unsupported Type: OverrideProcessProperties referenced by Overrides.processProperties
AtomExtensions has unsupported Type: PGPCertificates referenced by Overrides.PGPCertificates

AtomMapExtension has unsupported Type: MapExtension referenced by AtomMapExtension.Map

AtomMapExtensionsSummary has unsupported Type: MapExtensionBrowseData referenced by MapExtensionsSummary.SourceFieldSet
AtomMapExtensionsSummary has unsupported Type: MapExtensionBrowseData referenced by MapExtensionsSummary.DestinationFieldSet

AtomSecurityPolicies has unsupported Type: AtomSecurityPoliciesType referenced by AtomSecurityPolicies.common
AtomSecurityPolicies has unsupported Type: AtomSecurityPoliciesType referenced by AtomSecurityPolicies.runner
AtomSecurityPolicies has unsupported Type: AtomSecurityPoliciesType referenced by AtomSecurityPolicies.worker
AtomSecurityPolicies has unsupported Type: AtomSecurityPoliciesType referenced by AtomSecurityPolicies.browser

AtomStartupProperties has unsupported Type: Property referenced by AtomStartupProperties.Property

AuditLog has unsupported Type: AuditLogProperty referenced by AuditLog.AuditLogProperty

Cloud has unsupported Type: CloudAtom referenced by Cloud.Atom

Component has unsupported Type: EncryptedValues referenced by Component.encryptedValues

EnvironmentConnectionFieldExtensionSummary has unsupported Type: FieldSummary referenced by ConnectionFieldExtensionSummary.field

EnvironmentExtensions has unsupported Type: Connections referenced by Overrides.connections
EnvironmentExtensions has unsupported Type: Operations referenced by Overrides.operations
EnvironmentExtensions has unsupported Type: TradingPartners referenced by Overrides.tradingPartners
EnvironmentExtensions has unsupported Type: SharedCommunications referenced by Overrides.sharedCommunications
EnvironmentExtensions has unsupported Type: CrossReferences referenced by Overrides.crossReferences
EnvironmentExtensions has unsupported Type: OverrideProcessProperties referenced by Overrides.processProperties
EnvironmentExtensions has unsupported Type: PGPCertificates referenced by Overrides.PGPCertificates

EnvironmentMapExtension has unsupported Type: MapExtension referenced by EnvironmentMapExtension.Map

EnvironmentMapExtensionsSummary has unsupported Type: MapExtensionBrowseData referenced by MapExtensionsSummary.SourceFieldSet
EnvironmentMapExtensionsSummary has unsupported Type: MapExtensionBrowseData referenced by MapExtensionsSummary.DestinationFieldSet

PersistedProcessProperties has unsupported Type: DeployedProcess referenced by PersistedProcessProperties.Process

Process has unsupported Type: ProcessIntegrationPackInfo referenced by Process.IntegrationPack

ProcessSchedules has unsupported Type: Schedule referenced by ProcessSchedules.Schedule
ProcessSchedules has unsupported Type: ScheduleRetry referenced by ProcessSchedules.Retry

Role has unsupported Type: Privileges referenced by Role.Privileges

TradingPartnerComponent has unsupported Type: ContactInfo referenced by TradingPartnerComponent.ContactInfo
TradingPartnerComponent has unsupported Type: PartnerInfo referenced by TradingPartnerComponent.PartnerInfo
TradingPartnerComponent has unsupported Type: PartnerCommunication referenced by TradingPartnerComponent.PartnerCommunication
TradingPartnerComponent has unsupported Type: PartnerDocumentTypes referenced by TradingPartnerComponent.PartnerDocumentTypes

TradingPartnerProcessingGroup has unsupported Type: ProcessingGroupTradingPartners referenced by TradingPartnerProcessingGroup.TradingPartners
TradingPartnerProcessingGroup has unsupported Type: ProcessingGroupDefaultRouting referenced by TradingPartnerProcessingGroup.DefaultRouting
TradingPartnerProcessingGroup has unsupported Type: ProcessingGroupPartnerBasedRouting referenced by TradingPartnerProcessingGroup.PartnerRouting
TradingPartnerProcessingGroup has unsupported Type: ProcessingGroupDocumentBasedRouting referenced by TradingPartnerProcessingGroup.DocumentRouting

AuthenticationSourceGroup has unsupported Type: AuthSourceRole referenced by AuthenticationSourceGroup.roles

AuthenticationSourceUser has unsupported Type: AuthSourceGroup referenced by AuthenticationSourceUser.groups
AuthenticationSourceUser has unsupported Type: Credential referenced by AuthenticationSourceUser.credential

PublishedApi has unsupported Type: RestInfo referenced by PublishedApi.rest
PublishedApi has unsupported Type: SoapInfo referenced by PublishedApi.soap11
PublishedApi has unsupported Type: SoapInfo referenced by PublishedApi.soap12
PublishedApi has unsupported Type: ODataInfo referenced by PublishedApi.odata2
 
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
