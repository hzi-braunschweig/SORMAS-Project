package de.symeda.sormas.backend.common.messaging;

public enum MessageSubject {

	CASE_CLASSIFICATION_CHANGED,
	CASE_INVESTIGATION_DONE,
	EVENT_PARTICIPANT_CASE_CLASSIFICATION_CONFIRMED,
	LAB_RESULT_ARRIVED,
	LAB_RESULT_SPECIFIED,
	LAB_SAMPLE_SHIPPED,
	CONTACT_SYMPTOMATIC,
	TASK_START,
	TASK_DUE,
	VISIT_COMPLETED,
	DISEASE_CHANGED;
}
