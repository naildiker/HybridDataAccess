package org.hdm.core.management;

import org.apache.jena.ontology.*;
import org.apache.jena.query.*;
import org.apache.jena.rdf.model.*;
import org.apache.jena.util.FileManager;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.hdm.core.objects.*;

import java.io.*;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 6/5/2017.
 */
public class HdmManager implements IHDMManager
{
	//public static String ns = "http://www.semanticweb.org/nail.diker/ontologies/2016/2/untitled-ontology-4#";
	public static String ns = "http://www.semanticweb.org/nail.diker/ontologies/2020/6/hybrid-data-management-ontology#";
	static String filename = "c:\\TezRdfXml.owl";
	OntModel model = null;

	public HdmManager()
	{
		model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

		InputStream in = FileManager.get().open(filename);

		if (in == null)
			throw new IllegalArgumentException("File: " + filename + " not found");

		model.read(in, null);
	}

	@Override
	public Object importDataStoreInstance(IDataStoreInstance dataStoreInstance) {


		Resource dataStoreInstanceResource = model.getResource(ns + "DBMSOccurence");

		Individual ind = model.createIndividual(ns+ dataStoreInstance.getName(), dataStoreInstanceResource);
		ind.setOntClass(dataStoreInstanceResource);

		Individual dataStoreInstanceInd = null;
        if (dataStoreInstance.getDbType() == SupportedDataStore.MSSQLSERVER) {
            RelationalDataStoreInstance rdsi = (RelationalDataStoreInstance)dataStoreInstance;
            dataStoreInstanceInd = model.getIndividual(ns + "Microsoft_SQL_Server");

            ObjectProperty instnceOfProperty = model.getObjectProperty(ns + "isInstanceOf");
            ind.addProperty(instnceOfProperty, dataStoreInstanceInd);

			DatatypeProperty serverAddressProperty = model.getDatatypeProperty(ns + "hasIPAddress");
            ind.addProperty(serverAddressProperty, rdsi.getServerName());

            DatatypeProperty portProperty = model.getDatatypeProperty(ns + "hasPort");
            ind.addProperty(portProperty, rdsi.getServerPort());

            DatatypeProperty usernameProperty = model.getDatatypeProperty(ns + "hasUsername");
            ind.addProperty(usernameProperty, rdsi.getUsername());

            DatatypeProperty passwordProperty = model.getDatatypeProperty(ns + "hasPassword");
            ind.addProperty(passwordProperty, rdsi.getPassword());
        }
        else if (dataStoreInstance.getDbType() == SupportedDataStore.MONGO) {
            DocumentDataStoreInstance ddsi = (DocumentDataStoreInstance)dataStoreInstance;
            dataStoreInstanceInd = model.getIndividual(ns + "MongoDB");

            ObjectProperty instnceOfProperty = model.getObjectProperty(ns + "isInstanceOf");
            ind.addProperty(instnceOfProperty, dataStoreInstanceInd);

            DatatypeProperty serverAddressProperty = model.getDatatypeProperty(ns + "hasIPAddress");
            ind.addProperty(serverAddressProperty, ddsi.getServerName());

            DatatypeProperty portProperty = model.getDatatypeProperty(ns + "hasPort");
            ind.addProperty(portProperty, ddsi.getServerPort());

        }

		return ind;
	}

	@Override
	public Model getModel() {
		return model;
	}

	@Override
	public boolean addEntity(IEntity entity) {
		Entity domainEntity = (Entity) entity;

		Resource entityResource = model.getResource(ns + "Entity");
		Individual entityInstance = model.createIndividual(ns + domainEntity.getName(), entityResource);
		entityInstance.setOntClass(entityResource);

		Resource domainResource = model.getResource(ns + domainEntity.getDomainName());
		ObjectProperty hasEntityProperty = model.getObjectProperty(ns + "hasEntity");

		domainResource.addProperty(hasEntityProperty, entityInstance);

		for (Attribute entityAttrribute : ((Entity) entity).getAttributes()
			 ) {
			addAttribute(entityInstance, entityAttrribute);
		}
		return true;
	}

	public boolean addAttribute(Individual entityInstance, IAttribute attribute)
	{
		Attribute entityAttribute = (Attribute) attribute;
		Resource attributeResource = model.getResource(ns + "Attribute");
		Individual attributeInstance = model.createIndividual(ns + entityAttribute.getName(), attributeResource);
		attributeInstance.setOntClass(attributeResource);

		ObjectProperty hasAttributeProperty = model.getObjectProperty(ns + "hasAttribute");
		entityInstance.addProperty(hasAttributeProperty, attributeInstance);


		//TODO : Tanımlanan attribute ile match edenleri eşleştir.
		ObjectProperty isGeneralizedAttributeOfProperty = model.getObjectProperty(ns + "isGeneralizedAttributeOf");
		ObjectProperty isGeneralizedEntityOfProperty = model.getObjectProperty(ns + "isGeneralizedEntityOf");
		for (String dsAttributeName: ((Attribute) attribute).getMatchingAttributes()
			 ) {
		    if (dsAttributeName != null) {
                String[] attributeParts = dsAttributeName.split(" - ");
                Resource dsAttributeRes = model.getResource(ns + attributeParts[2].trim());
                attributeInstance.addProperty(isGeneralizedAttributeOfProperty, dsAttributeRes);

                Resource dsEntityRes = model.getResource(ns + attributeParts[1].trim());
                entityInstance.addProperty(isGeneralizedEntityOfProperty, dsEntityRes);

                Statement statement = entityInstance.getProperty(isGeneralizedEntityOfProperty);
                if (statement != null) {
                    //RDFList statementList = statement.getList();
                }
            }
		}

		return true;
	}

	@Override
	public boolean addDomain(Domain domain) {

		Resource domainResource = model.getResource(ns + "BusinessDomain");
		Individual domainInstance = model.createIndividual(ns + domain.getName(), domainResource);
		domainInstance.setOntClass(domainResource);

		return true;
	}

	@Override
	public Object addDataModel(DataModel dataModel) {

		Resource dataModelResource = model.getResource(ns + "DataModelOccurence");
		Individual dataModelInstance = model.createIndividual(ns + dataModel.getName(), dataModelResource);
		dataModelInstance.setOntClass(dataModelResource);

		Resource databaseResource = model.getResource(ns + "Database");
		Individual dbInstance = model.createIndividual(ns+ dataModel.getDbName(), databaseResource);
		dbInstance.setOntClass(databaseResource);

		ObjectProperty hasDatabaseProperty = model.getObjectProperty(ns + "hasDatabase");
		dataModel.getCurrentDsIndividual().addProperty(hasDatabaseProperty, dbInstance);

		ObjectProperty isDatabaseOfProperty = model.getObjectProperty(ns + "isDatabaseOf");
		dataModelInstance.addProperty(isDatabaseOfProperty, dbInstance);

		dataModel.setCurrentDbIndividual(dbInstance);

		ObjectProperty hasDataModelProperty = model.getObjectProperty(ns + "hasDataModelOccurence");
		for (Individual dsIndividual : dataModel.getDsIndividuals()
			 ) {
			dsIndividual.addProperty(hasDataModelProperty, dataModelInstance);
		}

		return dataModelInstance;
	}

	public boolean importEntity(DataModel dm, List<IEntity> entityList)
	{
	    if ( dm.getDbTypeList().contains(SupportedDataStore.MSSQLSERVER)) {
            Resource tableResource = model.getResource(ns + "Table");
            Resource columnResource = model.getResource(ns + "Column");
            for (IEntity entity : entityList) {
                Table table = (Table) entity;
                if (table != null) {
                    Individual ind = model.createIndividual(ns + table.getName(), tableResource);
                    ind.setOntClass(tableResource);
                    ind.addLabel(table.getName(), "EN");

                    ObjectProperty hasEntityProperty = model.getObjectProperty(ns + "hasEntity");
                    dm.getOwnIndividual().addProperty(hasEntityProperty, ind);

                    ObjectProperty hasTableProperty = model.getObjectProperty(ns + "hasTable");
                    dm.getCurrentDbIndividual().addProperty(hasTableProperty, ind);

                    for (Column tableColumn : table.getColumns()
                            ) {
                        Individual columnInstance = model.createIndividual(ns + table.getName() + ".." + tableColumn.getName(), columnResource);
                        columnInstance.setOntClass(columnResource);
                        columnInstance.addLabel(tableColumn.getName(), "EN");

                        ObjectProperty hasAttributeProperty = model.getObjectProperty(ns + "hasAttribute");
                        ind.addProperty(hasAttributeProperty, columnInstance);

                        ObjectProperty hasColumnProperty = model.getObjectProperty(ns + "hasColumn");
                        ind.addProperty(hasColumnProperty, columnInstance);

                    }
                }
            }
        }
        else
        {
            Resource collectionResource = model.getResource(ns + "Collection");
            Resource keyResource = model.getResource(ns + "Key");
            for (IEntity entity : entityList) {
                Collection collection = (Collection) entity;
                Individual ind = model.createIndividual(ns + collection.getName(), collectionResource);
                ind.setOntClass(collectionResource);
                ind.addLabel(collection.getName(), "EN");

                ObjectProperty hasEntityProperty = model.getObjectProperty(ns + "hasEntity");
                dm.getOwnIndividual().addProperty(hasEntityProperty, ind);

                ObjectProperty hasCollectionProperty = model.getObjectProperty(ns + "hasCollection");
                dm.getCurrentDbIndividual().addProperty(hasCollectionProperty, ind);

                for (Key collKey : collection.getKeys()
                        ) {
                    Individual keyInstance = model.createIndividual(ns + collection.getName() + ".." + collKey.getName(), keyResource);
                    keyInstance.setOntClass(keyResource);
                    keyInstance.addLabel(collKey.getName(), "EN");

                    ObjectProperty hasAttributeProperty = model.getObjectProperty(ns + "hasAttribute");
                    ind.addProperty(hasAttributeProperty, keyInstance);

                    ObjectProperty hasColumnProperty = model.getObjectProperty(ns + "hasKey");
                    ind.addProperty(hasColumnProperty, keyInstance);

                }
            }
        }
		return true;
	}

	public void persist()
	{

		File owlFile = new File("c:\\TezRdfXml.owl");
		FileOutputStream output = null;
		try
		{
			output = new FileOutputStream(owlFile);
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		RDFWriter owlWriter = model.getWriter("RDF/XML");

		owlWriter.write(model, output, null);

		try
		{
			output.flush();
			output.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public void disconnect()
	{
		model.close();
	}

	public List<String> queryInstances(String resourceName)
	{
		List<String> resultUris = new ArrayList<String>();
		Resource individ = model.getResource(ns + resourceName);
		ExtendedIterator<Individual> iter = model.listIndividuals(individ);
		while(iter.hasNext())
		{
			resultUris.add(iter.next().getURI());
			//System.out.println("Relational Database Instances : " + iter.next().getURI());
		}
		return resultUris;
	}


	public boolean query(String queryText) {

		Query qry = QueryFactory.create(queryText);
		QueryExecution qe = QueryExecutionFactory.create(qry, model);
		org.apache.jena.query.ResultSet rs = qe.execSelect();

		while (rs.hasNext()) {
			QuerySolution sol = rs.nextSolution();
			Resource yNode = sol.get("y").asResource();
			Resource xNode = sol.get("x").asResource();

		}

		qe.close();

		return true;
	}
}
