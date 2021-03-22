/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2020 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <https://www.gnu.org/licenses/>.
 */

package de.symeda.sormas.api.event;

import java.io.Serializable;
import java.util.Date;

import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.importexport.ExportGroup;
import de.symeda.sormas.api.importexport.ExportGroupType;
import de.symeda.sormas.api.importexport.ExportProperty;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.Order;
import de.symeda.sormas.api.utils.YesNoUnknown;

public class EventExportDto implements Serializable {

	public static final String I18N_PREFIX = "EventExport";

	public static final String UUID = "uuid";
	public static final String EXTERNAL_ID = "externalId";
	public static final String EVENT_STATUS = "eventStatus";
	public static final String EVENT_MANAGEMENT_STATUS = "eventManagementStatus";
	public static final String RISK_LEVEL = "riskLevel";
	public static final String EVENT_INVESTIGATION_STATUS = "eventInvestigationStatus";
	public static final String DISEASE = "disease";
	public static final String DISEASE_DETAILS = "diseaseDetails";
	public static final String START_DATE = "startDate";
	public static final String END_DATE = "endDate";
	public static final String EVOLUTION_DATE = "evolutionDate";
	public static final String EVOLUTION_COMMENT = "evolutionComment";
	public static final String EVENT_TITLE = "eventTitle";
	public static final String EVENT_DESC = "eventDesc";
	public static final String PORT_GROUP_TYPE = "port_groupType";
	public static final String DISEASE_TRANSMISSION_MODE = "diseaseTransmissionMode";
	public static final String NOSOCOMIAL = "nosocomial";
	public static final String TRANSREGIONAL_OUTBREAK = "transregionalOutbreak";
	public static final String MEANS_OF_TRANSPORT = "meansOfTransport";
	public static final String REGION = "region";
	public static final String DISTRICT = "district";
	public static final String COMMUNITY = "community";
	public static final String CITY = "city";
	public static final String STREET = "street";
	public static final String HOUSE_NUMBER = "houseNumber";
	public static final String ADDITIONAL_INFORMATION = "additionalInformation";
	public static final String SRC_TYPE = "srcType";
	public static final String SRC_INSTITUTIONAL_PARTNER_TYPE = "srcInstitutionalPartnerType";
	public static final String SRC_FIRSTNAME = "srcFirstname";
	public static final String SRC_LASTNAME = "srcLastname";
	public static final String SRC_TEL_NO = "srcTelNo";
	public static final String SRC_EMAIL = "srcEmail";
	public static final String SRC_MEDIA_WEBSITE = "srcMedia_website";
	public static final String SRC_MEDIA_NAME = "srcMediaName";
	public static final String SRC_MEDIA_DETAILS = "srcMediaDetails";
	public static final String REPORT_DATETIME = "reportDatetime";
	public static final String REPORTING_USER = "reportingUser";
	public static final String RESPONSIBLE_USER = "responsibleUser";
	public static final String PARTICIPANT_COUNT = "participantCount";
	public static final String CASE_COUNT = "caseCount";
	public static final String DEATH_COUNT = "deathCount";
	public static final String CONTACT_COUNT = "contactCount";
	public static final String CONTACT_COUNT_SOURCE_IN_EVENT = "contactCountSourceInEvent";
	public static final String EXTERNAL_TOKEN = "externalToken";

	private String uuid;
	private String externalId;
	private String externalToken;
	private EventStatus eventStatus;
	private RiskLevel riskLevel;
	private EventInvestigationStatus eventInvestigationStatus;
	private long participantCount;
	private long caseCount;
	private long deathCount;
	private long contactCount;
	private long contactCountSourceInEvent;
	private Disease disease;
	private String diseaseDetails;
	private Date startDate;
	private Date endDate;
	private Date evolutionDate;
	private String evolutionComment;
	private String eventTitle;
	private String eventDesc;
	private EventGroupsIndexDto eventGroups;
	private DiseaseTransmissionMode diseaseTransmissionMode;
	private YesNoUnknown nosocomial;
	private YesNoUnknown transregionalOutbreak;
	private final String meansOfTransport;
	private String region;
	private String district;
	private String community;
	private String city;
	private String street;
	private String houseNumber;
	private String additionalInformation;
	private EventSourceType srcType;
	private String srcInstitutionalPartnerType;
	private String srcFirstName;
	private String srcLastName;
	private String srcTelNo;
	private String srcEmail;
	private String srcMediaWebsite;
	private String srcMediaName;
	private String srcMediaDetails;
	private Date reportDateTime;
	private UserReferenceDto reportingUser;
	private UserReferenceDto responsibleUser;
	private EventManagementStatus eventManagementStatus;

	private EventJurisdictionDto jurisdiction;

	public EventExportDto(
		String uuid,
		String externalId,
		String externalToken,
		EventStatus eventStatus,
		RiskLevel riskLevel,
		EventInvestigationStatus eventInvestigationStatus,
		Disease disease,
		String diseaseDetails,
		Date startDate,
		Date endDate,
		Date evolutionDate,
		String evolutionComment,
		String eventTitle,
		String eventDesc,
		DiseaseTransmissionMode diseaseTransmissionMode,
		YesNoUnknown nosocomial,
		YesNoUnknown transregionalOutbreak,
		MeansOfTransport meansOfTransport,
		String meansOfTransportDetails,
		String regionUuid,
		String region,
		String districtUuid,
		String district,
		String communityUuid,
		String community,
		String city,
		String street,
		String houseNumber,
		String additionalInformation,
		EventSourceType srcType,
		InstitutionalPartnerType srcInstitutionalPartnerType,
		String srcInstitutionalPartnerTypeDetails,
		String srcFirstName,
		String srcLastName,
		String srcTelNo,
		String srcEmail,
		String srcMediaWebsite,
		String srcMediaName,
		String srcMediaDetails,
		Date reportDateTime,
		String reportingUserUuid,
		String reportingUserFirstName,
		String reportingUserLastName,
		String responsibleUserUuid,
		String responsibleUserFirstName,
		String responsibleUserLastName,
		EventManagementStatus eventManagementStatus) {
		this.uuid = uuid;
		this.externalId = externalId;
		this.externalToken = externalToken;
		this.eventStatus = eventStatus;
		this.riskLevel = riskLevel;
		this.eventInvestigationStatus = eventInvestigationStatus;
		this.disease = disease;
		this.diseaseDetails = diseaseDetails;
		this.startDate = startDate;
		this.endDate = endDate;
		this.evolutionDate = evolutionDate;
		this.evolutionComment = evolutionComment;
		this.eventTitle = eventTitle;
		this.eventDesc = eventDesc;
		this.diseaseTransmissionMode = diseaseTransmissionMode;
		this.nosocomial = nosocomial;
		this.transregionalOutbreak = transregionalOutbreak;
		this.meansOfTransport = EventHelper.buildMeansOfTransportString(meansOfTransport, meansOfTransportDetails);
		this.region = region;
		this.district = district;
		this.community = community;
		this.city = city;
		this.street = street;
		this.houseNumber = houseNumber;
		this.additionalInformation = additionalInformation;
		this.srcType = srcType;
		this.srcInstitutionalPartnerType =
			EventHelper.buildInstitutionalPartnerTypeString(srcInstitutionalPartnerType, srcInstitutionalPartnerTypeDetails);
		this.srcFirstName = srcFirstName;
		this.srcLastName = srcLastName;
		this.srcTelNo = srcTelNo;
		this.srcMediaWebsite = srcMediaWebsite;
		this.srcMediaName = srcMediaName;
		this.srcMediaDetails = srcMediaDetails;
		this.reportDateTime = reportDateTime;
		this.reportingUser = new UserReferenceDto(reportingUserUuid, reportingUserFirstName, reportingUserLastName, null);
		this.responsibleUser = new UserReferenceDto(responsibleUserUuid, responsibleUserFirstName, responsibleUserLastName, null);
		this.eventManagementStatus = eventManagementStatus;

		this.jurisdiction = new EventJurisdictionDto(reportingUserUuid, responsibleUserUuid, regionUuid, districtUuid, communityUuid);
	}

	@Order(0)
	@ExportProperty(UUID)
	@ExportGroup(ExportGroupType.CORE)
	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	@Order(1)
	@ExportProperty(EXTERNAL_ID)
	@ExportGroup(ExportGroupType.CORE)
	public String getExternalId() {
		return externalId;
	}

	@Order(2)
	@ExportProperty(EVENT_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public EventStatus getEventStatus() {
		return eventStatus;
	}

	public void setEventStatus(EventStatus eventStatus) {
		this.eventStatus = eventStatus;
	}

	@Order(3)
	@ExportProperty(EVENT_MANAGEMENT_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public EventManagementStatus getEventManagementStatus() {
		return eventManagementStatus;
	}

	public void setEventManagementStatus(EventManagementStatus eventManagementStatus) {
		this.eventManagementStatus = eventManagementStatus;
	}

	@Order(4)
	@ExportProperty(RISK_LEVEL)
	@ExportGroup(ExportGroupType.CORE)
	public RiskLevel getRiskLevel() {
		return riskLevel;
	}

	public void setRiskLevel(RiskLevel riskLevel) {
		this.riskLevel = riskLevel;
	}

	@Order(5)
	@ExportProperty(EVENT_INVESTIGATION_STATUS)
	@ExportGroup(ExportGroupType.CORE)
	public EventInvestigationStatus getEventInvestigationStatus() {
		return eventInvestigationStatus;
	}

	public void setEventInvestigationStatus(EventInvestigationStatus eventInvestigationStatus) {
		this.eventInvestigationStatus = eventInvestigationStatus;
	}

	@Order(6)
	@ExportProperty(DISEASE)
	@ExportGroup(ExportGroupType.CORE)
	public Disease getDisease() {
		return disease;
	}

	public void setDisease(Disease disease) {
		this.disease = disease;
	}

	@Order(7)
	@ExportProperty(DISEASE_DETAILS)
	@ExportGroup(ExportGroupType.CORE)
	public String getDiseaseDetails() {
		return diseaseDetails;
	}

	public void setDiseaseDetails(String diseaseDetails) {
		this.diseaseDetails = diseaseDetails;
	}

	@Order(8)
	@ExportProperty(START_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	@Order(9)
	@ExportProperty(END_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getEndDate() {
		return endDate;
	}

	@Order(10)
	@ExportProperty(EVOLUTION_DATE)
	@ExportGroup(ExportGroupType.CORE)
	public Date getEvolutionDate() {
		return evolutionDate;
	}

	public void setEvolutionDate(Date evolutionDate) {
		this.evolutionDate = evolutionDate;
	}

	@Order(11)
	@ExportProperty(EVOLUTION_COMMENT)
	@ExportGroup(ExportGroupType.CORE)
	public String getEvolutionComment() {
		return evolutionComment;
	}

	public void setEvolutionComment(String evolutionComment) {
		this.evolutionComment = evolutionComment;
	}

	@Order(12)
	@ExportProperty(EVENT_TITLE)
	@ExportGroup(ExportGroupType.CORE)
	public String getEventTitle() {
		return eventTitle;
	}

	public void setEventTitle(String eventTitle) {
		this.eventTitle = eventTitle;
	}

	@Order(13)
	@ExportProperty(EVENT_DESC)
	@ExportGroup(ExportGroupType.CORE)
	public String getEventDesc() {
		return eventDesc;
	}

	public void setEventDesc(String eventDesc) {
		this.eventDesc = eventDesc;
	}

	@Order(14)
	@ExportProperty(PORT_GROUP_TYPE)
	@ExportGroup(ExportGroupType.EVENT_GROUP)
	public EventGroupsIndexDto getEventGroups() {
		return eventGroups;
	}

	public void setEventGroups(EventGroupsIndexDto eventGroups) {
		this.eventGroups = eventGroups;
	}

	@Order(15)
	@ExportProperty(DISEASE_TRANSMISSION_MODE)
	@ExportGroup(ExportGroupType.CORE)
	public DiseaseTransmissionMode getDiseaseTransmissionMode() {
		return diseaseTransmissionMode;
	}

	@Order(16)
	@ExportProperty(NOSOCOMIAL)
	@ExportGroup(ExportGroupType.CORE)
	public YesNoUnknown getNosocomial() {
		return nosocomial;
	}

	@Order(17)
	@ExportProperty(TRANSREGIONAL_OUTBREAK)
	@ExportGroup(ExportGroupType.CORE)
	public YesNoUnknown getTransregionalOutbreak() {
		return transregionalOutbreak;
	}

	@Order(18)
	@ExportProperty(MEANS_OF_TRANSPORT)
	@ExportGroup(ExportGroupType.CORE)
	public String getMeansOfTransport() {
		return meansOfTransport;
	}

	@Order(19)
	@ExportProperty(REGION)
	@ExportGroup(ExportGroupType.CORE)
	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	@Order(20)
	@ExportProperty(DISTRICT)
	@ExportGroup(ExportGroupType.CORE)
	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	@Order(21)
	@ExportProperty(COMMUNITY)
	@ExportGroup(ExportGroupType.CORE)
	public String getCommunity() {
		return community;
	}

	public void setCommunity(String community) {
		this.community = community;
	}

	@Order(22)
	@ExportProperty(CITY)
	@ExportGroup(ExportGroupType.CORE)
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	@Order(23)
	@ExportProperty(STREET)
	@ExportGroup(ExportGroupType.CORE)
	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	@Order(24)
	@ExportProperty(HOUSE_NUMBER)
	@ExportGroup(ExportGroupType.CORE)
	public String getHouseNumber() {
		return houseNumber;
	}

	public void setHouseNumber(String houseNumber) {
		this.houseNumber = houseNumber;
	}

	@Order(25)
	@ExportProperty(ADDITIONAL_INFORMATION)
	@ExportGroup(ExportGroupType.CORE)
	public String getAdditionalInformation() {
		return additionalInformation;
	}

	public void setAdditionalInformation(String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	@Order(26)
	@ExportProperty(SRC_TYPE)
	@ExportGroup(ExportGroupType.CORE)
	public EventSourceType getSrcType() {
		return srcType;
	}

	@Order(27)
	@ExportProperty(SRC_INSTITUTIONAL_PARTNER_TYPE)
	@ExportGroup(ExportGroupType.CORE)
	public String getSrcInstitutionalPartnerType() {
		return srcInstitutionalPartnerType;
	}

	@Order(28)
	@ExportProperty(SRC_FIRSTNAME)
	@ExportGroup(ExportGroupType.CORE)
	public String getSrcFirstName() {
		return srcFirstName;
	}

	public void setSrcFirstName(String srcFirstName) {
		this.srcFirstName = srcFirstName;
	}

	@Order(29)
	@ExportProperty(SRC_LASTNAME)
	@ExportGroup(ExportGroupType.CORE)
	public String getSrcLastName() {
		return srcLastName;
	}

	public void setSrcLastName(String srcLastName) {
		this.srcLastName = srcLastName;
	}

	@Order(30)
	@ExportProperty(SRC_TEL_NO)
	@ExportGroup(ExportGroupType.CORE)
	public String getSrcTelNo() {
		return srcTelNo;
	}

	public void setSrcTelNo(String srcTelNo) {
		this.srcTelNo = srcTelNo;
	}

	@Order(31)
	@ExportProperty(SRC_EMAIL)
	@ExportGroup(ExportGroupType.CORE)
	public String getSrcEmail() {
		return srcEmail;
	}

	@Order(32)
	@ExportProperty(SRC_MEDIA_WEBSITE)
	@ExportGroup(ExportGroupType.CORE)
	public String getSrcMediaWebsite() {
		return srcMediaWebsite;
	}

	@Order(33)
	@ExportProperty(SRC_MEDIA_NAME)
	@ExportGroup(ExportGroupType.CORE)
	public String getSrcMediaName() {
		return srcMediaName;
	}

	@Order(34)
	@ExportProperty(SRC_MEDIA_DETAILS)
	@ExportGroup(ExportGroupType.CORE)
	public String getSrcMediaDetails() {
		return srcMediaDetails;
	}

	@Order(35)
	@ExportProperty(REPORT_DATETIME)
	@ExportGroup(ExportGroupType.CORE)
	public Date getReportDateTime() {
		return reportDateTime;
	}

	public void setReportDateTime(Date reportDateTime) {
		this.reportDateTime = reportDateTime;
	}

	@Order(36)
	@ExportProperty(REPORTING_USER)
	@ExportGroup(ExportGroupType.CORE)
	public UserReferenceDto getReportingUser() {
		return reportingUser;
	}

	public void setReportingUser(UserReferenceDto reportingUser) {
		this.reportingUser = reportingUser;
	}

	@Order(37)
	@ExportProperty(RESPONSIBLE_USER)
	@ExportGroup(ExportGroupType.CORE)
	public UserReferenceDto getResponsibleUser() {
		return responsibleUser;
	}

	public void setResponsibleUser(UserReferenceDto responsibleUser) {
		this.responsibleUser = responsibleUser;
	}

	@Order(38)
	@ExportProperty(PARTICIPANT_COUNT)
	@ExportGroup(ExportGroupType.CORE)
	public long getParticipantCount() {
		return participantCount;
	}

	public void setParticipantCount(long participantCount) {
		this.participantCount = participantCount;
	}

	@Order(39)
	@ExportProperty(CASE_COUNT)
	@ExportGroup(ExportGroupType.CORE)
	public long getCaseCount() {
		return caseCount;
	}

	public void setCaseCount(long caseCount) {
		this.caseCount = caseCount;
	}

	@Order(40)
	@ExportProperty(DEATH_COUNT)
	@ExportGroup(ExportGroupType.CORE)
	public long getDeathCount() {
		return deathCount;
	}

	public void setDeathCount(long deathCount) {
		this.deathCount = deathCount;
	}

	@Order(41)
	@ExportProperty(CONTACT_COUNT)
	@ExportGroup(ExportGroupType.CORE)
	public long getContactCount() {
		return contactCount;
	}

	public void setContactCount(long contactCount) {
		this.contactCount = contactCount;
	}

	@Order(42)
	@ExportProperty(CONTACT_COUNT_SOURCE_IN_EVENT)
	@ExportGroup(ExportGroupType.CORE)
	public long getContactCountSourceInEvent() {
		return contactCountSourceInEvent;
	}

	@Order(43)
	@ExportProperty(EXTERNAL_TOKEN)
	@ExportGroup(ExportGroupType.CORE)
	public String getExternalToken() {
		return externalToken;
	}

	public void setContactCountSourceInEvent(long contactCountSourceInEvent) {
		this.contactCountSourceInEvent = contactCountSourceInEvent;
	}

	public EventJurisdictionDto getJurisdiction() {
		return jurisdiction;
	}
}
