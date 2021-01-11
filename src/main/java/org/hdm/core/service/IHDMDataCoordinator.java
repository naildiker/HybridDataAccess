package org.hdm.core.service;

import org.hdm.core.objects.*;

import java.util.List;

public interface IHDMDataCoordinator {
    List<IEntity> getEntities(IDataStoreInstance dsi);
    List<String> getDefinedEntities();
    boolean addDataSource(IDataStoreInstance dsi, DataModel dm, List<IEntity> entities);
    List<String> getDomains();
    Boolean addEntity(IEntity entity);
    List<String> getAttributes();
    boolean addDomain(Domain domain);
    List<IEntityOccurence> get(String entityName, String keyword);

}
