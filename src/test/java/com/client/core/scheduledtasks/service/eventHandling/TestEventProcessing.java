package com.client.core.scheduledtasks.service.eventHandling;

import com.client.BaseTest;
import com.client.core.ApplicationSettings;
import com.client.core.scheduledtasks.dao.BullhornLogDAO;
import com.client.core.scheduledtasks.model.helper.StandardEvent;
import com.client.core.scheduledtasks.tools.enumeration.EventType;
import com.client.core.scheduledtasks.workers.EventProcessing;
import com.google.common.collect.Lists;
import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.transaction.TransactionConfiguration;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

@TransactionConfiguration(transactionManager = "transactionManager", defaultRollback = false)
public class TestEventProcessing extends BaseTest {

    @Autowired
    @Qualifier("appSettings")
    private ApplicationSettings appSettings;

	@Autowired
	private BullhornLogDAO bullhornLogDAO;

	private List<StandardEvent> subscriptionEvents;
	private String subscriptionName;
	private int updatingUserID;
	private int corporationID;

	public TestEventProcessing() {
		super();
	}

	@Before
	public void populateEvents() {

		subscriptionName = "TestSubscription";
		updatingUserID = getTestEntities().getCorporateUserId();
		corporationID = getTestEntities().getClientCorporationId();

		List<String> eventTypes = createEventTypes();
		Map<String, Integer>  entityTypes = createEntityTypes();
		subscriptionEvents = new ArrayList<StandardEvent>();

		int eventID = 1;
		int requestID = 1;

		for (Map.Entry<String, Integer> entity : entityTypes.entrySet()) {
			for (String eventType : eventTypes) {
				eventID++;
				requestID++;
				StandardEvent event = instantiateTestEvent(eventType, entity.getKey(), entity.getValue(), null, "" + eventID, ""
						+ requestID);
				subscriptionEvents.add(event);
			}
		}
	}

	private Map<String, Integer> createEntityTypes() {

		Map<String, Integer> entityTypes = new LinkedHashMap<String, Integer>();
		entityTypes.put("Appointment", getTestEntities().getAppointmentId());
		entityTypes.put("Candidate", getTestEntities().getCandidateId());
		entityTypes.put("CandidateEducation", getTestEntities().getCandidateEducationId());
		entityTypes.put("CandidateWorkHistory", getTestEntities().getCandidateWorkHistoryId());
		entityTypes.put("CandidateReference", getTestEntities().getCandidateReferenceId());
		entityTypes.put("ClientContact", getTestEntities().getClientContactId());
		entityTypes.put("ClientCorporation", getTestEntities().getClientCorporationId());
		entityTypes.put("CorporateUser", getTestEntities().getCorporateUserId());
		entityTypes.put("JobOrder", getTestEntities().getJobOrderId());
		entityTypes.put("JobSubmission", getTestEntities().getJobSubmissionId());
		entityTypes.put("Note", getTestEntities().getNoteId());
		entityTypes.put("Placement", getTestEntities().getPlacementId());
		entityTypes.put("PlacementChangeRequest", getTestEntities().getPlacementChangeRequestId());
		entityTypes.put("PlacementCommission", getTestEntities().getPlacementCommissionId());
		entityTypes.put("Sendout", getTestEntities().getSendoutId());
		entityTypes.put("Task", getTestEntities().getTaskId());
		return entityTypes;
	}

	private List<String> createEventTypes() {
		List<String> eventTypes = new ArrayList<String>();
		eventTypes.add(EventType.DELETE.value());
		eventTypes.add(EventType.INSERT.value());
		eventTypes.add(EventType.UPDATE.value());
		return eventTypes;
	}

	private StandardEvent instantiateTestEvent(String eventType, String entityType, int entityID, String[] updatedProperties,
			String eventId, String requestID) {
		StandardEvent se = new StandardEvent();
		se.setEventType(eventType);
		se.setEventId(eventId);
		se.setRequestId(Integer.parseInt(requestID));
		se.setEntityName(entityType);
		se.setEntityId(entityID);
		se.setUpdatedProperties(Lists.newArrayList(updatedProperties));
		se.setEventTimestamp(DateTime.now());
		se.setSubscriptionName(subscriptionName);
		se.setError(false);
		se.setUpdatingUserId(updatingUserID);

		return se;
	}

	@Test
	public void testSetup() {

		assertTrue("Not set up correctly" + subscriptionEvents.size(), 48 == subscriptionEvents.size());
	}

	@Test
	public void testProcessEvents() {
		boolean error = false;
		// configurable based on # threads to process concurrently
		// set to 1 in stax-web numEventThreads if single threaded processing is required
		ExecutorService exec = Executors.newFixedThreadPool(Integer.parseInt(appSettings.getNumEventThreads()));
		// loop through each event
		for (StandardEvent event : subscriptionEvents) {
			error = false;
            EventProcessing processEvent = EventProcessing.instantiateRunnable(corporationID, bullhornLogDAO, event);
			// send to exec service
			try {
				exec.execute(processEvent);
			} catch (Exception e) {
				error = true;

			}

			assertFalse("Error with entity: " + event.getEntityName() + ", event type: " + event.getEventType(), error);
		}

		// shutdown pool, wait until it's done
		exec.shutdown();
		while (!exec.isTerminated()) {
		}
		// null it out
		exec = null;

	}

}
