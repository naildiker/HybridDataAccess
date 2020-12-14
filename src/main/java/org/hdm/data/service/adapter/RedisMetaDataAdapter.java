package org.hdm.data.service.adapter;

import org.hdm.core.data.service.IHDMConnectInfo;
import org.hdm.core.data.service.IHDMMetaDataAdapter;
import org.hdm.core.objects.Entity;
import org.hdm.core.objects.IEntity;

import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class RedisMetaDataAdapter implements IHDMMetaDataAdapter
{

	public Boolean connect(IHDMConnectInfo connectionInfo)
	{
		return false;
	}

	public List<IEntity> getEntities()
	{
		return null;
	}

	public List<String> getAttributeNames()
	{
		return null;
	}

	public List<String> getAttributeNames(String entityName)
	{
		return null;
	}

	public List<String> getRelationshipNames()
	{
		return null;
	}

	public void close()
	{
	}
}
