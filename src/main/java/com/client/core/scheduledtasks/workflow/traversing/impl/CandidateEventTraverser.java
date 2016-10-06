package com.client.core.scheduledtasks.workflow.traversing.impl;

import com.bullhornsdk.data.api.BullhornData;
import com.client.core.scheduledtasks.model.helper.StandardEvent;
import com.client.core.scheduledtasks.model.helper.impl.CandidateScheduledTaskHelper;
import com.client.core.scheduledtasks.tools.enumeration.EventType;
import com.client.core.scheduledtasks.workflow.traversing.AbstractScheduledTasksTraverser;
import com.client.core.soap.model.SubscriptionEvent;

/**
 * A Traverser is passed through a Node work flow and it's instance variables are set for future Event handling.
 * 
 * @author magnus.palm
 * 
 */

public class CandidateEventTraverser extends AbstractScheduledTasksTraverser<CandidateScheduledTaskHelper> {

	public CandidateEventTraverser(StandardEvent event) {
		super(new CandidateScheduledTaskHelper(event),EventType.getType(event.getEventType()));
	
	}

	public CandidateEventTraverser(StandardEvent event, BullhornData bullhornData) {
		super(new CandidateScheduledTaskHelper(event,bullhornData),EventType.getType(event.getEventType()));
	}

}
