/*
 * SORMAS® - Surveillance Outbreak Response Management & Analysis System
 * Copyright © 2016-2021 Helmholtz-Zentrum für Infektionsforschung GmbH (HZI)
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

package de.symeda.sormas.backend.importexport;

import static de.symeda.sormas.api.campaign.data.CampaignFormDataDto.FORM_DATE;
import static de.symeda.sormas.api.caze.CaseDataDto.CASE_ORIGIN;
import static de.symeda.sormas.api.caze.CaseDataDto.COMMUNITY;
import static de.symeda.sormas.api.caze.CaseDataDto.DENGUE_FEVER_TYPE;
import static de.symeda.sormas.api.caze.CaseDataDto.DISEASE;
import static de.symeda.sormas.api.caze.CaseDataDto.DISEASE_DETAILS;
import static de.symeda.sormas.api.caze.CaseDataDto.DISTRICT;
import static de.symeda.sormas.api.caze.CaseDataDto.EPID_NUMBER;
import static de.symeda.sormas.api.caze.CaseDataDto.FACILITY_TYPE;
import static de.symeda.sormas.api.caze.CaseDataDto.HEALTH_FACILITY;
import static de.symeda.sormas.api.caze.CaseDataDto.HEALTH_FACILITY_DETAILS;
import static de.symeda.sormas.api.caze.CaseDataDto.PERSON;
import static de.symeda.sormas.api.caze.CaseDataDto.PLAGUE_TYPE;
import static de.symeda.sormas.api.caze.CaseDataDto.POINT_OF_ENTRY;
import static de.symeda.sormas.api.caze.CaseDataDto.POINT_OF_ENTRY_DETAILS;
import static de.symeda.sormas.api.caze.CaseDataDto.RABIES_TYPE;
import static de.symeda.sormas.api.caze.CaseDataDto.REGION;
import static de.symeda.sormas.api.caze.CaseDataDto.REPORT_DATE;
import static de.symeda.sormas.api.caze.CaseDataDto.RESPONSIBLE_COMMUNITY;
import static de.symeda.sormas.api.caze.CaseDataDto.RESPONSIBLE_DISTRICT;
import static de.symeda.sormas.api.caze.CaseDataDto.RESPONSIBLE_REGION;
import static de.symeda.sormas.api.caze.CaseDataDto.SYMPTOMS;

import java.beans.PropertyDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.inject.Provider;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.opencsv.CSVWriter;

import de.symeda.sormas.api.AgeGroup;
import de.symeda.sormas.api.Disease;
import de.symeda.sormas.api.EntityDto;
import de.symeda.sormas.api.ImportIgnore;
import de.symeda.sormas.api.campaign.data.CampaignFormDataDto;
import de.symeda.sormas.api.campaign.form.CampaignFormElementType;
import de.symeda.sormas.api.campaign.form.CampaignFormMetaDto;
import de.symeda.sormas.api.caze.CaseDataDto;
import de.symeda.sormas.api.caze.CaseOrigin;
import de.symeda.sormas.api.caze.DengueFeverType;
import de.symeda.sormas.api.caze.PlagueType;
import de.symeda.sormas.api.caze.RabiesType;
import de.symeda.sormas.api.contact.ContactDto;
import de.symeda.sormas.api.customizableenum.CustomizableEnum;
import de.symeda.sormas.api.event.EventDto;
import de.symeda.sormas.api.event.EventGroupReferenceDto;
import de.symeda.sormas.api.event.EventParticipantDto;
import de.symeda.sormas.api.facility.FacilityDto;
import de.symeda.sormas.api.facility.FacilityReferenceDto;
import de.symeda.sormas.api.facility.FacilityType;
import de.symeda.sormas.api.feature.FeatureType;
import de.symeda.sormas.api.i18n.I18nProperties;
import de.symeda.sormas.api.i18n.Validations;
import de.symeda.sormas.api.importexport.ImportColumn;
import de.symeda.sormas.api.importexport.ImportExportUtils;
import de.symeda.sormas.api.importexport.ImportFacade;
import de.symeda.sormas.api.infrastructure.PointOfEntryDto;
import de.symeda.sormas.api.infrastructure.PointOfEntryReferenceDto;
import de.symeda.sormas.api.infrastructure.PopulationDataDto;
import de.symeda.sormas.api.person.PersonDto;
import de.symeda.sormas.api.person.PersonReferenceDto;
import de.symeda.sormas.api.person.Sex;
import de.symeda.sormas.api.region.AreaDto;
import de.symeda.sormas.api.region.AreaReferenceDto;
import de.symeda.sormas.api.region.CommunityDto;
import de.symeda.sormas.api.region.CommunityReferenceDto;
import de.symeda.sormas.api.region.ContinentDto;
import de.symeda.sormas.api.region.ContinentReferenceDto;
import de.symeda.sormas.api.region.CountryDto;
import de.symeda.sormas.api.region.CountryReferenceDto;
import de.symeda.sormas.api.region.DistrictDto;
import de.symeda.sormas.api.region.DistrictReferenceDto;
import de.symeda.sormas.api.region.RegionDto;
import de.symeda.sormas.api.region.RegionReferenceDto;
import de.symeda.sormas.api.region.SubcontinentDto;
import de.symeda.sormas.api.region.SubcontinentReferenceDto;
import de.symeda.sormas.api.sample.PathogenTestDto;
import de.symeda.sormas.api.sample.SampleDto;
import de.symeda.sormas.api.symptoms.SymptomsDto;
import de.symeda.sormas.api.user.UserDto;
import de.symeda.sormas.api.user.UserReferenceDto;
import de.symeda.sormas.api.utils.CSVCommentLineValidator;
import de.symeda.sormas.api.utils.CSVUtils;
import de.symeda.sormas.api.utils.DataHelper;
import de.symeda.sormas.api.utils.DateHelper;
import de.symeda.sormas.api.utils.DependingOnFeatureType;
import de.symeda.sormas.api.utils.fieldvisibility.checkers.CountryFieldVisibilityChecker;
import de.symeda.sormas.backend.campaign.form.CampaignFormMetaFacadeEjb.CampaignFormMetaFacadeEjbLocal;
import de.symeda.sormas.backend.common.ConfigFacadeEjb.ConfigFacadeEjbLocal;
import de.symeda.sormas.backend.common.EnumService;
import de.symeda.sormas.backend.disease.DiseaseConfigurationFacadeEjb.DiseaseConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.feature.FeatureConfigurationFacadeEjb.FeatureConfigurationFacadeEjbLocal;
import de.symeda.sormas.backend.region.AreaFacadeEjb.AreaFacadeEjbLocal;
import de.symeda.sormas.backend.region.CountryFacadeEjb;
import de.symeda.sormas.backend.region.CountryFacadeEjb.CountryFacadeEjbLocal;
import de.symeda.sormas.backend.region.Region;
import de.symeda.sormas.backend.region.RegionFacadeEjb;
import de.symeda.sormas.backend.region.RegionService;
import de.symeda.sormas.backend.user.UserFacadeEjb.UserFacadeEjbLocal;

@Stateless(name = "ImportFacade")
public class ImportFacadeEjb implements ImportFacade {

	private final Logger logger = LoggerFactory.getLogger(getClass());

	private static final String PERSON_PREFIX = "person.";

	private static final List<String> PERSON_COLUMNS_TO_REMOVE = Arrays.asList(
		PersonDto.PLACE_OF_BIRTH_COMMUNITY,
		PersonDto.PLACE_OF_BIRTH_DISTRICT,
		PersonDto.PLACE_OF_BIRTH_FACILITY,
		PersonDto.PLACE_OF_BIRTH_FACILITY_DETAILS,
		PersonDto.PLACE_OF_BIRTH_FACILITY_TYPE,
		PersonDto.PLACE_OF_BIRTH_REGION,
		PersonDto.GESTATION_AGE_AT_BIRTH,
		PersonDto.BIRTH_WEIGHT,
		PersonDto.PRESENT_CONDITION,
		PersonDto.DEATH_DATE,
		PersonDto.DEATH_PLACE_DESCRIPTION,
		PersonDto.DEATH_PLACE_TYPE,
		PersonDto.CAUSE_OF_DEATH,
		PersonDto.CAUSE_OF_DEATH_DETAILS,
		PersonDto.CAUSE_OF_DEATH_DISEASE,
		PersonDto.CAUSE_OF_DEATH_DISEASE_DETAILS,
		PersonDto.BURIAL_CONDUCTOR,
		PersonDto.BURIAL_DATE,
		PersonDto.BURIAL_PLACE_DESCRIPTION,
		PersonDto.ADDRESSES,
		PersonDto.SYMPTOM_JOURNAL_STATUS);

	@EJB
	private ConfigFacadeEjbLocal configFacade;
	@EJB
	private FeatureConfigurationFacadeEjbLocal featureConfigurationFacade;
	@EJB
	private DiseaseConfigurationFacadeEjbLocal diseaseConfigurationFacade;
	@EJB
	private CampaignFormMetaFacadeEjbLocal campaignFormMetaFacade;
	@EJB
	private UserFacadeEjbLocal userFacade;
	@EJB
	private AreaFacadeEjbLocal areaFacade;
	@EJB
	private EnumService enumService;
	@EJB
	private RegionService regionService;
	@EJB
	private CountryFacadeEjbLocal countryFacade;

	private static final String CASE_IMPORT_TEMPLATE_FILE_NAME = "import_case_template.csv";
	private static final String EVENT_IMPORT_TEMPLATE_FILE_NAME = "import_event_template.csv";
	private static final String EVENT_PARTICIPANT_IMPORT_TEMPLATE_FILE_NAME = "import_eventparticipant_template.csv";
	private static final String CASE_CONTACT_IMPORT_TEMPLATE_FILE_NAME = "import_case_contact_template.csv";
	private static final String CASE_LINE_LISTING_IMPORT_TEMPLATE_FILE_NAME = "import_line_listing_template.csv";
	private static final String POINT_OF_ENTRY_IMPORT_TEMPLATE_FILE_NAME = "import_point_of_entry_template.csv";
	private static final String POPULATION_DATA_IMPORT_TEMPLATE_FILE_NAME = "import_population_data_template.csv";
	private static final String CONTINENT_IMPORT_TEMPLATE_FILE_NAME = "import_continent_template.csv";
	private static final String SUBCONTINENT_IMPORT_TEMPLATE_FILE_NAME = "import_subcontinent_template.csv";
	private static final String AREA_IMPORT_TEMPLATE_FILE_NAME = "import_area_template.csv";
	private static final String COUNTRY_IMPORT_TEMPLATE_FILE_NAME = "import_country_template.csv";
	private static final String REGION_IMPORT_TEMPLATE_FILE_NAME = "import_region_template.csv";
	private static final String DISTRICT_IMPORT_TEMPLATE_FILE_NAME = "import_district_template.csv";
	private static final String COMMUNITY_IMPORT_TEMPLATE_FILE_NAME = "import_community_template.csv";
	private static final String FACILITY_IMPORT_TEMPLATE_FILE_NAME = "import_facility_template.csv";
	private static final String CONTACT_IMPORT_TEMPLATE_FILE_NAME = "import_contact_template.csv";
	private static final String CAMPAIGN_FORM_IMPORT_TEMPLATE_FILE_NAME = "import_campaign_form_data_template.csv";
	private static final String TRAVEL_ENTRY_IMPORT_TEMPLATE_FILE_NAME = "import_travel_entry_template.csv";

	private static final String ALL_COUNTRIES_IMPORT_FILE_NAME = "sormas_import_all_countries.csv";
	private static final String ALL_SUBCONTINENTS_IMPORT_FILE_NAME = "sormas_import_all_subcontinents.csv";
	private static final String ALL_CONTINENTS_IMPORT_FILE_NAME = "sormas_import_all_continents.csv";

	@Override
	public void generateCaseImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, CaseDataDto.class, "", separator);
		appendListOfFields(importColumns, SampleDto.class, "", separator);
		appendListOfFields(importColumns, PathogenTestDto.class, "", separator);

		writeTemplate(Paths.get(getCaseImportTemplateFilePath()), importColumns, true);
	}

	private void addPrimaryPhoneAndEmail(char separator, List<ImportColumn> importColumns) {
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.PHONE, String.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.EMAIL_ADDRESS, String.class, separator));
	}

	@Override
	public void generateEventImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		ArrayList<String> columnsToRemove = new ArrayList<>(Arrays.asList(EventDto.SORMAS_TO_SORMAS_ORIGIN_INFO, EventDto.OWNERSHIP_HANDED_OVER));
		if (featureConfigurationFacade.isFeatureDisabled(FeatureType.EVENT_HIERARCHIES)) {
			columnsToRemove.add(EventDto.SUPERORDINATE_EVENT);
		}

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, EventDto.class, "", separator);
		importColumns = importColumns.stream().filter(column -> keepColumn(column, "", columnsToRemove)).collect(Collectors.toList());
		if (featureConfigurationFacade.isFeatureEnabled(FeatureType.EVENT_GROUPS)) {
			importColumns.add(ImportColumn.from(EventGroupReferenceDto.class, EventDto.EVENT_GROUP, String.class, separator));
		}

		importColumns.add(ImportColumn.from(EventParticipantDto.class, EventParticipantDto.INVOLVEMENT_DESCRIPTION, String.class, separator));
		importColumns.add(ImportColumn.from(EventParticipantDto.class, EventParticipantDto.REGION, String.class, separator));
		importColumns.add(ImportColumn.from(EventParticipantDto.class, EventParticipantDto.DISTRICT, String.class, separator));

		appendListOfFields(importColumns, PersonDto.class, PERSON_PREFIX, separator);
		importColumns =
			importColumns.stream().filter(column -> keepColumn(column, PERSON_PREFIX, PERSON_COLUMNS_TO_REMOVE)).collect(Collectors.toList());

		writeTemplate(Paths.get(getEventImportTemplateFilePath()), importColumns, true);
	}

	@Override
	public void generateEventParticipantImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		importColumns.add(ImportColumn.from(EventParticipantDto.class, EventParticipantDto.INVOLVEMENT_DESCRIPTION, String.class, separator));
		importColumns.add(ImportColumn.from(EventParticipantDto.class, EventParticipantDto.REGION, String.class, separator));
		importColumns.add(ImportColumn.from(EventParticipantDto.class, EventParticipantDto.DISTRICT, String.class, separator));

		appendListOfFields(importColumns, PersonDto.class, "person.", separator);
		addPrimaryPhoneAndEmail(separator, importColumns);

		importColumns =
			importColumns.stream().filter(column -> keepColumn(column, PERSON_PREFIX, PERSON_COLUMNS_TO_REMOVE)).collect(Collectors.toList());

		writeTemplate(Paths.get(getEventParticipantImportTemplateFilePath()), importColumns, true);
	}

	@Override
	public void generateCampaignFormImportTemplateFile(String campaignFormUuid) throws IOException {

		createExportDirectoryIfNecessary();

		List<ImportColumn> importColumns = new ArrayList<>();
		char separator = configFacade.getCsvSeparator();

		/* importColumns.add(ImportColumn.from(CampaignFormDataDto.class, CAMPAIGN, CampaignReferenceDto.class, separator)); */
		importColumns.add(ImportColumn.from(CampaignFormDataDto.class, FORM_DATE, Date.class, separator));
		importColumns.add(ImportColumn.from(CampaignFormDataDto.class, REGION, RegionReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CampaignFormDataDto.class, DISTRICT, DistrictReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CampaignFormDataDto.class, COMMUNITY, CommunityReferenceDto.class, separator));

		CampaignFormMetaDto campaignFormMetaDto = campaignFormMetaFacade.getCampaignFormMetaByUuid(campaignFormUuid);
		campaignFormMetaDto.getCampaignFormElements()
			.stream()
			.filter(
				e -> !(CampaignFormElementType.SECTION.name().equalsIgnoreCase(e.getType())
					|| CampaignFormElementType.LABEL.name().equalsIgnoreCase(e.getType())))
			.forEach(formElement -> importColumns.add(new ImportColumn(formElement.getId(), formElement.getCaption(), formElement.getType())));
		writeTemplate(Paths.get(getCampaignFormImportTemplateFilePath()), importColumns, false);

	}

	@Override
	public void generateCaseContactImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, ContactDto.class, "", separator);

		List<String> columnsToRemove = Arrays.asList(
			ContactDto.CAZE,
			ContactDto.DISEASE,
			ContactDto.DISEASE_DETAILS,
			ContactDto.RESULTING_CASE,
			ContactDto.CASE_ID_EXTERNAL_SYSTEM,
			ContactDto.CASE_OR_EVENT_INFORMATION);
		importColumns = importColumns.stream().filter(column -> !columnsToRemove.contains(column.getColumnName())).collect(Collectors.toList());

		writeTemplate(Paths.get(getCaseContactImportTemplateFilePath()), importColumns, false);
	}

	@Override
	public void generateContactImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, ContactDto.class, "", separator);
		List<String> columnsToRemove = Arrays.asList(ContactDto.CAZE, ContactDto.RESULTING_CASE);
		importColumns = importColumns.stream().filter(column -> !columnsToRemove.contains(column.getColumnName())).collect(Collectors.toList());

		writeTemplate(Paths.get(getContactImportTemplateFilePath()), importColumns, false);
	}

	@Override
	public void generateCaseLineListingImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		importColumns.add(ImportColumn.from(CaseDataDto.class, DISEASE, Disease.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, DISEASE_DETAILS, String.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, PLAGUE_TYPE, PlagueType.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, DENGUE_FEVER_TYPE, DengueFeverType.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, RABIES_TYPE, RabiesType.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.FIRST_NAME, String.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.LAST_NAME, String.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.SEX, Sex.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.BIRTH_DATE_DD, Integer.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.BIRTH_DATE_MM, Integer.class, separator));
		importColumns.add(ImportColumn.from(PersonDto.class, PERSON + "." + PersonDto.BIRTH_DATE_YYYY, Integer.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, EPID_NUMBER, String.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, REPORT_DATE, Date.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, CASE_ORIGIN, CaseOrigin.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, RESPONSIBLE_REGION, RegionReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, RESPONSIBLE_DISTRICT, DistrictReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, RESPONSIBLE_COMMUNITY, CommunityReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, FACILITY_TYPE, FacilityType.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, HEALTH_FACILITY, FacilityReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, HEALTH_FACILITY_DETAILS, String.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, POINT_OF_ENTRY, PointOfEntryReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, POINT_OF_ENTRY_DETAILS, String.class, separator));
		importColumns.add(ImportColumn.from(CaseDataDto.class, SYMPTOMS + "." + SymptomsDto.ONSET_DATE, Date.class, separator));

		writeTemplate(Paths.get(getCaseLineListingImportTemplateFilePath()), importColumns, false);
	}

	@Override
	public void generatePointOfEntryImportTemplateFile() throws IOException {
		generateImportTemplateFile(PointOfEntryDto.class, Paths.get(getPointOfEntryImportTemplateFilePath()));
	}

	@Override
	public void generatePopulationDataImportTemplateFile() throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		importColumns.add(ImportColumn.from(PopulationDataDto.class, PopulationDataDto.REGION, RegionReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, PopulationDataDto.DISTRICT, DistrictReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, PopulationDataDto.COMMUNITY, CommunityReferenceDto.class, separator));
		importColumns.add(ImportColumn.from(RegionDto.class, RegionDto.GROWTH_RATE, Float.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "TOTAL", Integer.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "MALE_TOTAL", Integer.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "FEMALE_TOTAL", Integer.class, separator));
		importColumns.add(ImportColumn.from(PopulationDataDto.class, "OTHER_TOTAL", Integer.class, separator));
		for (AgeGroup ageGroup : AgeGroup.values()) {
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "TOTAL_" + ageGroup.name(), Integer.class, separator));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "MALE_" + ageGroup.name(), Integer.class, separator));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "FEMALE_" + ageGroup.name(), Integer.class, separator));
			importColumns.add(ImportColumn.from(PopulationDataDto.class, "OTHER_" + ageGroup.name(), Integer.class, separator));
		}

		writeTemplate(Paths.get(getPopulationDataImportTemplateFilePath()), importColumns, false);
	}

	private void createExportDirectoryIfNecessary() throws IOException {

		try {
			Files.createDirectories(Paths.get(configFacade.getGeneratedFilesPath()));
		} catch (IOException e) {
			logger.error("Generated files directory doesn't exist and creation failed.");
			throw e;
		}
	}

	@Override
	public void generateAreaImportTemplateFile() throws IOException {
		generateImportTemplateFile(AreaDto.class, Paths.get(getAreaImportTemplateFilePath()));
	}

	@Override
	public void generateContinentImportTemplateFile() throws IOException {
		generateImportTemplateFile(ContinentDto.class, Paths.get(getContinentImportTemplateFilePath()));
	}

	@Override
	public void generateSubcontinentImportTemplateFile() throws IOException {
		generateImportTemplateFile(SubcontinentDto.class, Paths.get(getSubcontinentImportTemplateFilePath()));
	}

	@Override
	public void generateCountryImportTemplateFile() throws IOException {
		generateImportTemplateFile(CountryDto.class, Paths.get(getCountryImportTemplateFilePath()));
	}

	@Override
	public void generateRegionImportTemplateFile() throws IOException {
		generateImportTemplateFile(RegionDto.class, Paths.get(getRegionImportTemplateFilePath()));
	}

	@Override
	public void generateDistrictImportTemplateFile() throws IOException {
		generateImportTemplateFile(DistrictDto.class, Paths.get(getDistrictImportTemplateFilePath()));
	}

	@Override
	public void generateCommunityImportTemplateFile() throws IOException {
		generateImportTemplateFile(CommunityDto.class, Paths.get(getCommunityImportTemplateFilePath()));
	}

	@Override
	public void generateFacilityImportTemplateFile() throws IOException {
		generateImportTemplateFile(FacilityDto.class, Paths.get(getFacilityImportTemplateFilePath()));
	}

	private <T extends EntityDto> void generateImportTemplateFile(Class<T> clazz, Path filePath) throws IOException {

		createExportDirectoryIfNecessary();

		char separator = configFacade.getCsvSeparator();

		List<ImportColumn> importColumns = new ArrayList<>();
		appendListOfFields(importColumns, clazz, "", separator);

		writeTemplate(filePath, importColumns, false);
	}

	@Override
	public String getCaseImportTemplateFileName() {
		return getImportTemplateFileName(CASE_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCaseImportTemplateFilePath() {
		return getImportTemplateFilePath(CASE_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getEventImportTemplateFileName() {
		return getImportTemplateFileName(EVENT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getEventImportTemplateFilePath() {
		return getImportTemplateFilePath(EVENT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getEventParticipantImportTemplateFileName() {
		return getImportTemplateFileName(EVENT_PARTICIPANT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getEventParticipantImportTemplateFilePath() {
		return getImportTemplateFilePath(EVENT_PARTICIPANT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCampaignFormImportTemplateFilePath() {
		return getImportTemplateFilePath(CAMPAIGN_FORM_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCaseContactImportTemplateFileName() {
		return getImportTemplateFileName(CASE_CONTACT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCaseContactImportTemplateFilePath() {
		return getImportTemplateFilePath(CASE_CONTACT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCaseLineListingImportTemplateFileName() {
		return getImportTemplateFileName(CASE_LINE_LISTING_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCaseLineListingImportTemplateFilePath() {
		return getImportTemplateFilePath(CASE_LINE_LISTING_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getContactImportTemplateFileName() {
		return getImportTemplateFileName(CONTACT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getContactImportTemplateFilePath() {
		return getImportTemplateFilePath(CONTACT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getPointOfEntryImportTemplateFileName() {
		return getImportTemplateFileName(POINT_OF_ENTRY_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getPointOfEntryImportTemplateFilePath() {
		return getImportTemplateFilePath(POINT_OF_ENTRY_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getPopulationDataImportTemplateFileName() {
		return getImportTemplateFileName(POPULATION_DATA_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getPopulationDataImportTemplateFilePath() {
		return getImportTemplateFilePath(POPULATION_DATA_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getAreaImportTemplateFileName() {
		return getImportTemplateFileName(AREA_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getAreaImportTemplateFilePath() {
		return getImportTemplateFilePath(AREA_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getContinentImportTemplateFileName() {
		return getImportTemplateFileName(CONTINENT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getContinentImportTemplateFilePath() {
		return getImportTemplateFilePath(CONTINENT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getSubcontinentImportTemplateFileName() {
		return getImportTemplateFileName(SUBCONTINENT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getSubcontinentImportTemplateFilePath() {
		return getImportTemplateFilePath(SUBCONTINENT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCountryImportTemplateFileName() {
		return getImportTemplateFileName(COUNTRY_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCountryImportTemplateFilePath() {
		return getImportTemplateFilePath(COUNTRY_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getRegionImportTemplateFileName() {
		return getImportTemplateFileName(REGION_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getRegionImportTemplateFilePath() {
		return getImportTemplateFilePath(REGION_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getDistrictImportTemplateFileName() {
		return getImportTemplateFileName(DISTRICT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getDistrictImportTemplateFilePath() {
		return getImportTemplateFilePath(DISTRICT_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCommunityImportTemplateFileName() {
		return getImportTemplateFileName(COMMUNITY_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getCommunityImportTemplateFilePath() {
		return getImportTemplateFilePath(COMMUNITY_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getFacilityImportTemplateFileName() {
		return getImportTemplateFileName(FACILITY_IMPORT_TEMPLATE_FILE_NAME);
	}

	@Override
	public String getFacilityImportTemplateFilePath() {
		return getImportTemplateFilePath(FACILITY_IMPORT_TEMPLATE_FILE_NAME);
	}

	private String getImportTemplateFileName(String baseFilename) {
		String instanceName = DataHelper.cleanStringForFileName(configFacade.getSormasInstanceName().toLowerCase());
		return instanceName + "_" + baseFilename;
	}

	private String getImportTemplateFilePath(String baseFilename) {
		Path exportDirectory = Paths.get(configFacade.getGeneratedFilesPath());
		return exportDirectory.resolve(getImportTemplateFileName(baseFilename)).toString();
	}

	@Override
	public URI getAllCountriesImportFilePath() {
		try {
			return this.getClass().getClassLoader().getResource(ALL_COUNTRIES_IMPORT_FILE_NAME).toURI();
		} catch (URISyntaxException e) {
			logger.warn("Cannot get countries import file path: ", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public URI getAllSubcontinentsImportFilePath() {
		try {
			return this.getClass().getClassLoader().getResource(ALL_SUBCONTINENTS_IMPORT_FILE_NAME).toURI();
		} catch (URISyntaxException e) {
			logger.warn("Cannot get subcontinents import file path: ", e);
			throw new RuntimeException(e);
		}
	}

	@Override
	public URI getAllContinentsImportFilePath() {
		try {
			return this.getClass().getClassLoader().getResource(ALL_CONTINENTS_IMPORT_FILE_NAME).toURI();
		} catch (URISyntaxException e) {
			logger.warn("Cannot get continents import file path: ", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Builds a list of all fields in the case and its relevant sub entities. IMPORTANT: The order
	 * is not guaranteed; at the time of writing, clazz.getDeclaredFields() seems to return the
	 * fields in the order of declaration (which is what we need here), but that could change
	 * in the future.
	 */
	private void appendListOfFields(List<ImportColumn> importColumns, Class<?> clazz, String prefix, char separator) {

		for (Field field : clazz.getDeclaredFields()) {
			if (Modifier.isStatic(field.getModifiers())) {
				continue;
			}

			String currentCountry = configFacade.getCountryCode();
			CountryFieldVisibilityChecker visibilityChecker = new CountryFieldVisibilityChecker(currentCountry);
			if (!visibilityChecker.isVisible(field)) {
				continue;
			}

			Method readMethod;
			try {
				readMethod = clazz.getDeclaredMethod("get" + WordUtils.capitalize(field.getName()));
			} catch (NoSuchMethodException e) {
				try {
					readMethod = clazz.getDeclaredMethod("is" + WordUtils.capitalize(field.getName()));
				} catch (NoSuchMethodException f) {
					continue;
				}
			}

			// Fields without a getter or whose getters are declared in a superclass are ignored
			if (readMethod.getDeclaringClass() != clazz) {
				continue;
			}
			// Fields with the @ImportIgnore annotation are ignored
			if (readMethod.isAnnotationPresent(ImportIgnore.class)) {
				continue;
			}
			// Fields that are depending on a certain feature type to be active may be ignored
			if (readMethod.isAnnotationPresent(DependingOnFeatureType.class)) {
				List<FeatureType> activeServerFeatures = featureConfigurationFacade.getActiveServerFeatureTypes();
				if (!activeServerFeatures.isEmpty()
					&& !activeServerFeatures.contains(readMethod.getAnnotation(DependingOnFeatureType.class).featureType())) {
					continue;
				}
			}
			// List types are ignored
			if (Collection.class.isAssignableFrom(field.getType())) {
				continue;
			}
			// Certain field types are ignored
			if (field.getType() == UserReferenceDto.class) {
				continue;
			}
			// Other non-infrastructure EntityDto/ReferenceDto classes, recursively call this method to include fields of the sub-entity
			if (EntityDto.class.isAssignableFrom(field.getType()) && !isInfrastructureClass(field.getType())) {
				appendListOfFields(
					importColumns,
					field.getType(),
					StringUtils.isEmpty(prefix) ? field.getName() + "." : prefix + field.getName() + ".",
					separator);
			} else if (PersonReferenceDto.class.isAssignableFrom(field.getType()) && !isInfrastructureClass(field.getType())) {
				appendListOfFields(
					importColumns,
					PersonDto.class,
					StringUtils.isEmpty(prefix) ? field.getName() + "." : prefix + field.getName() + ".",
					separator);
				addPrimaryPhoneAndEmail(separator, importColumns);
			} else {
				importColumns.add(ImportColumn.from(clazz, prefix + field.getName(), field.getType(), separator));
			}
		}
	}

	private boolean isInfrastructureClass(Class<?> clazz) {

		return clazz == RegionReferenceDto.class
			|| clazz == DistrictReferenceDto.class
			|| clazz == CommunityReferenceDto.class
			|| clazz == FacilityReferenceDto.class
			|| clazz == PointOfEntryReferenceDto.class
			|| clazz == CountryReferenceDto.class
			|| clazz == SubcontinentReferenceDto.class
			|| clazz == ContinentReferenceDto.class;
	}

	/**
	 * Writes the given line as a comment line
	 *
	 * @param csvWriter
	 *            file writer
	 * @param line
	 *            line to write
	 */
	private void writeCommentLine(CSVWriter csvWriter, String[] line) {
		String[] commentedLine = Arrays.copyOf(line, line.length);
		commentedLine[0] = CSVCommentLineValidator.DEFAULT_COMMENT_LINE_PREFIX + commentedLine[0];
		csvWriter.writeNext(commentedLine, false);
	}

	/**
	 * Writes template files with the following lines:
	 * <ul>
	 * <li><code>entityNames</code> - only if <code>includeEntityNames</code> is <code>true</code></li>
	 * <li><code>columnNames</code> - represent the DTO properties that can be filled</li>
	 * <li><code>captions</code> - (commented) internationalized caption for each field</li>
	 * <li><code>dataDescription</code> - (commented) data examples or description for each field</li>
	 * </ul>
	 *
	 * @param templatePath
	 *            path to write the template to
	 * @param importColumns
	 *            details about each CSV column
	 * @param includeEntityNames
	 *            weather to include the <code>entityNames</code> or not
	 * @throws IOException
	 */
	private void writeTemplate(Path templatePath, List<ImportColumn> importColumns, boolean includeEntityNames) throws IOException {
		try (CSVWriter writer = CSVUtils.createCSVWriter(
			new OutputStreamWriter(new FileOutputStream(templatePath.toString()), StandardCharsets.UTF_8.newEncoder()),
			configFacade.getCsvSeparator())) {
			if (includeEntityNames) {
				writer.writeNext(importColumns.stream().map(ImportColumn::getEntityName).toArray(String[]::new));
			}
			writer.writeNext(importColumns.stream().map(ImportColumn::getColumnName).toArray(String[]::new));
			writeCommentLine(writer, importColumns.stream().map(ImportColumn::getCaption).toArray(String[]::new));
			writeCommentLine(writer, importColumns.stream().map(ImportColumn::getDataDescription).toArray(String[]::new));
			writer.flush();
		}
	}

	@Override
	public String getImportTemplateContent(String templateFilePath) throws IOException {
		Charset charset = StandardCharsets.UTF_8;
		String content = new String(Files.readAllBytes(Paths.get(templateFilePath)), charset);
		return resolvePlaceholders(content);
	}

	/**
	 * Replaces placeholders in the given file content.
	 * The placeholders are resolved using dynamic data. For any static data extend {@link ImportColumn}.
	 *
	 * @param content
	 *            file content.
	 * @return
	 * @see ImportFacade#ACTIVE_DISEASES_PLACEHOLDER
	 */
	private String resolvePlaceholders(String content) {
		Map<String, Provider<String>> placeholderResolvers = new HashMap<>();
		placeholderResolvers.put(
			ImportFacade.ACTIVE_DISEASES_PLACEHOLDER,
			() -> StringUtils.join(
				diseaseConfigurationFacade.getAllActiveDiseases().stream().map(Disease::getName).collect(Collectors.toList()),
				ImportExportUtils.getCSVSeparatorDifferentFromCurrent(configFacade.getCsvSeparator())));

		for (Map.Entry<String, Provider<String>> placeholderResolver : placeholderResolvers.entrySet()) {
			content = content.replace(placeholderResolver.getKey(), placeholderResolver.getValue().get());
		}

		return content;
	}

	private boolean keepColumn(ImportColumn column, String prefix, List<String> columnsToExclude) {
		String columnName = column.getColumnName();
		for (String columnToExclude : columnsToExclude) {
			String prefixColumnName = prefix + columnToExclude;
			if (prefixColumnName.equals(columnName) || columnName.startsWith(prefixColumnName + ".")) {
				return false;
			}
		}
		return true;
	}

	public boolean executeDefaultInvoke(PropertyDescriptor pd, Object element, String entry, String[] entryHeaderPath, boolean allowForeignRegions)
		throws InvocationTargetException, IllegalAccessException, ParseException, ImportErrorException, EnumService.InvalidEnumCaptionException {
		Class<?> propertyType = pd.getPropertyType();

		if (propertyType.isEnum()) {
			Enum enumValue = null;
			Class<Enum> enumType = (Class<Enum>) propertyType;
			try {
				enumValue = Enum.valueOf(enumType, entry.toUpperCase());
			} catch (IllegalArgumentException e) {
				// ignore
			}

			if (enumValue == null) {
				enumValue = enumService.getEnumByCaption(enumType, entry);
			}

			pd.getWriteMethod().invoke(element, enumValue);
			return true;
		}
		if (propertyType.getSuperclass() != null && propertyType.getSuperclass() == CustomizableEnum.class) {
			try {
				Object customizableEnum = propertyType.newInstance();
				((CustomizableEnum) customizableEnum).setValue(entry);
				pd.getWriteMethod().invoke(element, customizableEnum);
				return true;
			} catch (InstantiationException e) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importErrorCustomizableEnumValue, entry, buildEntityProperty(entryHeaderPath)));
			}
		}
		if (propertyType.isAssignableFrom(Date.class)) {
			// If the string is smaller than the length of the expected date format, throw an exception
			if (entry.length() < 10) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(
						Validations.importInvalidDate,
						buildEntityProperty(entryHeaderPath),
						DateHelper.getAllowedDateFormats(I18nProperties.getUserLanguage().getDateFormat())));
			} else {
				pd.getWriteMethod().invoke(element, DateHelper.parseDateWithException(entry, I18nProperties.getUserLanguage().getDateFormat()));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(Integer.class)) {
			pd.getWriteMethod().invoke(element, Integer.parseInt(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(Double.class)) {
			pd.getWriteMethod().invoke(element, Double.parseDouble(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(Float.class)) {
			pd.getWriteMethod().invoke(element, Float.parseFloat(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(Boolean.class) || propertyType.isAssignableFrom(boolean.class)) {
			pd.getWriteMethod().invoke(element, Boolean.parseBoolean(entry));
			return true;
		}
		if (propertyType.isAssignableFrom(CountryReferenceDto.class)) {
			List<CountryReferenceDto> countries = countryFacade.getReferencesByName(entry, false);
			if (countries.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (countries.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importCountryNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, countries.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(AreaReferenceDto.class)) {
			List<AreaReferenceDto> areas = areaFacade.getByName(entry, false);
			if (areas.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (areas.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importAreaNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else {
				pd.getWriteMethod().invoke(element, areas.get(0));
				return true;
			}
		}
		if (propertyType.isAssignableFrom(RegionReferenceDto.class)) {
			List<Region> regions = regionService.getByName(entry, false);
			if (regions.isEmpty()) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			} else if (regions.size() > 1) {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importRegionNotUnique, entry, buildEntityProperty(entryHeaderPath)));
			} else if (allowForeignRegions) {
				pd.getWriteMethod().invoke(element, RegionFacadeEjb.toReferenceDto(regions.get(0)));
				return true;
			} else {
				Region region = regions.get(0);
				CountryReferenceDto serverCountry = countryFacade.getServerCountry();

				if (region.getCountry() != null && !CountryFacadeEjb.toReferenceDto(region.getCountry()).equals(serverCountry)) {
					throw new ImportErrorException(
						I18nProperties.getValidationError(Validations.importRegionNotInServerCountry, entry, buildEntityProperty(entryHeaderPath)));
				} else {
					pd.getWriteMethod().invoke(element, RegionFacadeEjb.toReferenceDto(region));
					return true;
				}
			}
		}
		if (propertyType.isAssignableFrom(UserReferenceDto.class)) {
			UserDto user = userFacade.getByUserName(entry);
			if (user != null) {
				pd.getWriteMethod().invoke(element, user.toReference());
				return true;
			} else {
				throw new ImportErrorException(
					I18nProperties.getValidationError(Validations.importEntryDoesNotExist, entry, buildEntityProperty(entryHeaderPath)));
			}
		}
		if (propertyType.isAssignableFrom(String.class)) {
			pd.getWriteMethod().invoke(element, entry);
			return true;
		}

		return false;
	}

	public String buildEntityProperty(String[] entityPropertyPath) {
		return String.join(".", entityPropertyPath);
	}

	@LocalBean
	@Stateless
	public static class ImportFacadeEjbLocal extends ImportFacadeEjb {

	}
}
