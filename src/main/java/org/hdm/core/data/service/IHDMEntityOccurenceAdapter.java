package org.hdm.core.data.service;

import org.hdm.core.objects.EntityOccurence;

import java.util.List;

/**
 * Created by nail.diker on 09/20/2016.
 */
public interface IHDMEntityOccurenceAdapter extends IHDMBaseAdapter
{
	public List<EntityOccurence> getEntityOccurences(String entityName);
}
