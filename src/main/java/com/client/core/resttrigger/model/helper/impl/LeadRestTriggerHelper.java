package com.client.core.resttrigger.model.helper.impl;

import com.bullhornsdk.data.model.entity.core.standard.Lead;
import com.client.core.base.model.helper.impl.LeadTriggerHelper;
import com.client.core.base.model.relatedentity.BullhornRelatedEntity;
import com.client.core.base.model.relatedentity.LeadRelatedEntity;
import com.client.core.base.util.TriggerUtil;
import com.client.core.resttrigger.model.helper.RestTriggerHelper;

import java.util.Map;
import java.util.Set;

public class LeadRestTriggerHelper extends LeadTriggerHelper implements RestTriggerHelper<Lead> {

	private final Integer entityID;
	private final Map<String, Object> valuesChanged;

	public LeadRestTriggerHelper(Integer entityID, Map<String, Object> valuesChanged, Integer updatingUserID,
								 Map<? extends BullhornRelatedEntity, Set<String>> relatedEntityFields) {
		super(updatingUserID, relatedEntityFields);
		this.entityID = entityID;
		this.valuesChanged = valuesChanged;
	}

	@Override
	public Set<String> getPopulatedFields() {
		return valuesChanged.keySet();
	}

	@Override
	public Integer getEntityID() {
		return entityID;
	}

	@Override
	public Map<String, Object> getValuesChanged() {
		return valuesChanged;
	}

	@Override
	protected Lead instantiateNewEntity() {
		return TriggerUtil.populateEntity(entityID, Lead.class, valuesChanged, Lead::new, getFields(LeadRelatedEntity.LEAD));
	}

}
