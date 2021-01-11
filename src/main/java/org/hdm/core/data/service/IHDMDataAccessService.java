package org.hdm.core.data.service;

import org.hdm.core.objects.IDataStoreInstance;
import org.hdm.core.objects.IEntity;
import org.hdm.core.objects.IEntityOccurence;

import java.util.List;

public interface IHDMDataAccessService {
    public Object importDataStoreInstance(IDataStoreInstance dataStoreInstance);
    List<IEntity> getEntities(IDataStoreInstance dataStoreInstance);
    List<IEntityOccurence> get(String nativeQueryText);
}
