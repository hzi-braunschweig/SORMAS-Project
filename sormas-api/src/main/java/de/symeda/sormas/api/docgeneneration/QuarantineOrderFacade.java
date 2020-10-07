package de.symeda.sormas.api.docgeneneration;

import java.util.List;
import java.util.Properties;

import javax.ejb.Remote;

@Remote
public interface QuarantineOrderFacade {

	byte[] getGeneratedDocument(String templateName, String caseUuid, Properties extraProperties);

	List<String> getAvailableTemplates();

	List<String> getAdditionalVariables(String templateName);

	void writeQuarantineTemplate(String fileName, byte[] document);

	boolean deleteQuarantineTemplate(String fileName);

}
