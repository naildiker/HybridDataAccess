package org.hdm.core.data.service;

import org.hdm.core.objects.Entity;
import org.hdm.core.objects.IEntity;

import java.util.List;

/**
 * Created by nail.diker on 09/20/2016.
 */
public interface IHDMMetaDataAdapter extends IHDMBaseAdapter
{

	public List<IEntity> getEntities();

	public List<String> getAttributeNames();

	public List<String> getAttributeNames(String entityName);

	public List<String> getRelationshipNames();

}
