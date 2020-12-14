package org.hdm.core.data.service;

import org.hdm.core.objects.IEntity;
import org.hdm.core.objects.IEntityOccurence;

import java.util.List;

/**
 * Created by nail.diker on 09/20/2016.
 */
public interface IHDMDataAdapter extends IHDMBaseAdapter
{
	public List<IEntityOccurence> get(String nativeQueryText);
}
