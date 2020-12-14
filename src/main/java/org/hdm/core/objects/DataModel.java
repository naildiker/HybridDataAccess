package org.hdm.core.objects;

import org.apache.jena.ontology.Individual;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class DataModel
{
	private String name;

	private List<SupportedDataStore> dbTypeList = new ArrayList<SupportedDataStore>();

	private String dbName;

	private List<IDataStoreInstance> dsList = new ArrayList<IDataStoreInstance>();

	private List<Individual> dsIndividual = new ArrayList<Individual>();
	private Individual dmInstance;
	private Individual currentDsIndividual;
	private Individual currentDbIndividual;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<SupportedDataStore> getDbTypeList()
	{
		return dbTypeList;
	}

	public void setDbType(SupportedDataStore dbType)
	{
		this.dbTypeList.add(dbType);
	}

	public String getDbName() {
		return dbName;
	}

	public void setDbName(String dbName) {
		this.dbName = dbName;
	}

	public List<IDataStoreInstance> getDataSourceInstanceList() {
		return dsList;
	}

	public void setDataSourceInstance(IDataStoreInstance ds) {
		this.dsList.add(ds);
	}

	public List<Individual> getDsIndividuals() {
		return dsIndividual;
	}

	public void setDsIndividual(Individual dsIndividual) {
		this.dsIndividual.add(dsIndividual);
	}

	public void setOwnIndividual(Individual dmInstance) {
		this.setDmInstance(dmInstance);
	}

	public Individual getOwnIndividual() {
		return dmInstance;
	}

	public void setDmInstance(Individual dmInstance) {
		this.dmInstance = dmInstance;
	}

	public Individual getCurrentDsIndividual() {
		return currentDsIndividual;
	}

	public void setCurrentDsIndividual(Individual currentDsIndividual) {
		this.currentDsIndividual = currentDsIndividual;
	}

	public Individual getCurrentDbIndividual() {
		return currentDbIndividual;
	}

	public void setCurrentDbIndividual(Individual currentDbIndividual) {
		this.currentDbIndividual = currentDbIndividual;
	}
}
