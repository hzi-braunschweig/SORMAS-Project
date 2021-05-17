package de.symeda.sormas.api.dashboard;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import de.symeda.sormas.api.caze.CaseClassification;
import de.symeda.sormas.api.caze.CaseCriteria;
import de.symeda.sormas.api.contact.DashboardQuarantineDataDto;
import de.symeda.sormas.api.person.PresentCondition;
import de.symeda.sormas.api.sample.PathogenTestResultType;

@Remote
public interface DashboardFacade {

	List<DashboardCaseDto> getCases(DashboardCriteria dashboardCriteria);

	Map<CaseClassification, Integer> getCasesCountByClassification(DashboardCriteria dashboardCriteria);

	List<DashboardQuarantineDataDto> getQuarantineData(DashboardCriteria dashboardCriteria);

	String getLastReportedDistrictName(DashboardCriteria dashboardCriteria);

	Map<PathogenTestResultType, Long> getTestResultCountByResultType(List<DashboardCaseDto> cases);

	long countCasesConvertedFromContacts(DashboardCriteria dashboardCriteria);

	Map<PresentCondition, Long> getCaseCountPerPersonCondition(CaseCriteria caseCriteria);
}
