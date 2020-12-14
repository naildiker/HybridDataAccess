package org.hdm.core.management;

import org.apache.jena.ontology.Individual;
import org.apache.jena.rdf.model.Model;
import org.hdm.core.objects.DataModel;
import org.hdm.core.objects.Domain;
import org.hdm.core.objects.IDataStoreInstance;
import org.hdm.core.objects.IEntity;

import java.util.List;

/**
 * Created by nail.diker on 6/5/2017.
 */
public interface IHDMManager
{

	public Object importDataStoreInstance(IDataStoreInstance dataStoreInstance);

	public Model getModel();

	public boolean addEntity(IEntity entity);

	public boolean addDomain(Domain domain);

	public Object addDataModel(DataModel dataModel);

	public boolean importEntity(DataModel dataModel, List<IEntity> entityList);

	public List<String> queryInstances(String resourceName);

	public void persist();

	public void disconnect();
}