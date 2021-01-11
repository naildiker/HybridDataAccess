package org.hdm.core.service;

import org.apache.jena.ontology.Individual;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.Resource;
import org.hdm.core.management.HdmManager;
import org.hdm.core.objects.*;
import org.hdm.data.service.adapter.MsSqlServerMetaDataAdapter;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;
import org.hdm.data.service.tenant.HDMDataAccessService;

import java.util.ArrayList;
import java.util.List;

public class HDMDataCoordinatorService implements  IHDMDataCoordinator {
    @Override
    public List<IEntity> getEntities(IDataStoreInstance dsi) {

        HDMDataAccessService dataAccessService = new HDMDataAccessService();
        return dataAccessService.getEntities(dsi);

    }

    @Override
    public List<String> getDefinedEntities() {
        HdmManager hdmMan = new HdmManager();
        String queryText = "PREFIX  ds: <" + HdmManager.ns + ">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  DISTINCT ?x " +
                "  WHERE " +
                "  { ?x rdf:type ds:Entity .\n " +
                " ?x ds:isGeneralizedEntityOf ?y. }";

        org.apache.jena.query.Query qry = QueryFactory.create(queryText);
        QueryExecution qe = QueryExecutionFactory.create(qry, hdmMan.getModel());
        org.apache.jena.query.ResultSet rs = qe.execSelect();

        List<String> definedEntityNames= new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            Resource xNode = sol.get("x").asResource();

            definedEntityNames.add(xNode.getLocalName());
        }
        qe.close();
        hdmMan.disconnect();
        return definedEntityNames;
    }

    @Override
    public boolean addDataSource(IDataStoreInstance dsi, DataModel dm, List<IEntity> entities) {
        try {
            Individual dsInstance = null;

            HDMDataAccessService dataAccessService = new HDMDataAccessService();
            dsInstance = (Individual) dataAccessService.importDataStoreInstance(dsi);

            HdmManager hdmMan = new HdmManager();
            dm.setDataSourceInstance(dsi);
            dm.setDsIndividual(dsInstance);
            dm.setCurrentDsIndividual(dsInstance);
            Individual dmInstance = (Individual) hdmMan.addDataModel(dm);
            dm.setOwnIndividual(dmInstance);

            hdmMan.importEntity(dm, entities);
            hdmMan.persist();
            hdmMan.disconnect();

            return true;
        }
        catch(Exception exc){
            return false;
        }
    }

    @Override
    public List<String> getDomains() {
        HdmManager hdmMan = new HdmManager();
        List<String> domainUris= hdmMan.queryInstances("BusinessDomain");
        List<String> domainNames= new ArrayList<>();
        for (String domainUri : domainUris
                ) {
            domainNames.add(domainUri.replaceAll(hdmMan.ns, ""));
        }
        hdmMan.disconnect();
        return domainNames;
    }

    @Override
    public Boolean addEntity(IEntity entity) {
        try {
            HdmManager hdmMan = new HdmManager();
            hdmMan.addEntity(entity);
            hdmMan.persist();
            hdmMan.disconnect();
            return true;
        }
        catch(Exception exc){
            return false;
        }
    }

    @Override
    public List<String> getAttributes() {
        HdmManager hdmMan = new HdmManager();

        String queryText = "PREFIX  ds: <" + HdmManager.ns + ">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  ?x ?y ?z " +
                "  WHERE " +
                "  { ?x rdf:type ds:Column .\n " +
                " ?y ds:hasColumn ?x .\n ?z ds:hasTable ?y . }";

        org.apache.jena.query.Query qry = QueryFactory.create(queryText);
        QueryExecution qe = QueryExecutionFactory.create(qry, hdmMan.getModel());
        org.apache.jena.query.ResultSet rs = qe.execSelect();

        List<String> attributeNames= new ArrayList<>();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            Resource zNode = sol.get("z").asResource();
            Resource yNode = sol.get("y").asResource();
            Resource xNode = sol.get("x").asResource();

            attributeNames.add(zNode.getLocalName() + " -  " + yNode.getLocalName() + " - " + xNode.getLocalName() );
        }
        qe.close();

        queryText = "PREFIX  ds: <"+ HdmManager.ns +">\n " +
                "PREFIX  rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>\n " +
                "  SELECT  ?x ?y ?z " +
                "  WHERE " +
                "  { ?x rdf:type ds:Key.\n " +
                " ?y ds:hasKey ?x .\n ?z ds:hasCollection ?y . }";

        qry = QueryFactory.create(queryText);
        qe = QueryExecutionFactory.create(qry, hdmMan.getModel());
        rs = qe.execSelect();
        while (rs.hasNext()) {
            QuerySolution sol = rs.nextSolution();
            Resource zNode = sol.get("z").asResource();
            Resource yNode = sol.get("y").asResource();
            Resource xNode = sol.get("x").asResource();

            attributeNames.add(zNode.getLocalName() + " -  " + yNode.getLocalName() + " - " + xNode.getLocalName() );
        }
        qe.close();
        return attributeNames;
    }

    public boolean addDomain(Domain domain) {
        HdmManager hdmMan = new HdmManager();
        hdmMan.addDomain(domain);
        hdmMan.persist();
        hdmMan.disconnect();
        return true;
    }

    @Override
    public List<IEntityOccurence> get(String entityName, String keyword) {
        HDMQueryManagementService qryManSrv = new HDMQueryManagementService();
        return qryManSrv.get(entityName, keyword);
    }
}
