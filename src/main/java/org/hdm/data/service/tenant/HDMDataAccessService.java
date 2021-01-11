package org.hdm.data.service.tenant;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFWriter;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.util.FileManager;
import org.hdm.core.data.service.IHDMDataAccessService;
import org.hdm.core.objects.*;
import org.hdm.data.service.adapter.MongoDataProvider;
import org.hdm.data.service.adapter.MongoMetaDataAdapter;
import org.hdm.data.service.adapter.MsSqlServerDataProvider;
import org.hdm.data.service.adapter.MsSqlServerMetaDataAdapter;
import org.hdm.data.service.provider.MongoConnectionInfo;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;

import java.io.*;
import java.util.List;

public class HDMDataAccessService implements IHDMDataAccessService {

    public static String ns = "http://www.semanticweb.org/nail.diker/ontologies/2020/6/hybrid-data-management-ontology#";
    static String filename = "c:\\TezRdfXml.owl";
    OntModel model = null;

    public HDMDataAccessService()
    {
    }

    @Override
    public Object importDataStoreInstance(IDataStoreInstance dataStoreInstance) {

        connect();

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

        persist();
        disconnect();

        return ind;
    }

    @Override
    public List<IEntity> getEntities(IDataStoreInstance dataStoreInstance) {
        List<IEntity> entityNames = null;
        if (dataStoreInstance.getDbType().equals(SupportedDataStore.MSSQLSERVER)) {
            RelationalDataStoreInstance rdsi = (RelationalDataStoreInstance) dataStoreInstance;
            MsSqlServerMetaDataAdapter msSqlAdapter = new MsSqlServerMetaDataAdapter();
            msSqlAdapter.connect(new MsSqlServerConnectionInfo() {{
                setServerName(rdsi.getServerName());
                setServerPort((int) Double.parseDouble(rdsi.getServerPort()));
                setDbName(rdsi.getCurrentDbName());
                setUsername(rdsi.getUsername());
                setPassword(rdsi.getPassword());
            }});
            entityNames = msSqlAdapter.getEntities();

            msSqlAdapter.close();
        }else if (dataStoreInstance.getDbType().equals(SupportedDataStore.MONGO)) {
            DocumentDataStoreInstance ddsi = (DocumentDataStoreInstance) dataStoreInstance;
            MongoMetaDataAdapter mongoMetaAdapter = new MongoMetaDataAdapter();
            mongoMetaAdapter.connect(new MongoConnectionInfo() {{
                setServerName(ddsi.getServerName());
                setServerPort((int) Double.parseDouble(ddsi.getServerPort()));
                setDbName(ddsi.getCurrentDbName());
            }});
            entityNames = mongoMetaAdapter.getEntities();
            mongoMetaAdapter.close();
        }
        return entityNames;
    }

    @Override
    public List<IEntityOccurence> get(IDataStoreInstance dataStoreInstance, String nativeQueryText) {

        List<IEntityOccurence> resultList = null;

        if (dataStoreInstance.getDbType().equals(SupportedDataStore.MSSQLSERVER)) {
            RelationalDataStoreInstance rdsi = (RelationalDataStoreInstance) dataStoreInstance;
            MsSqlServerDataProvider msSqlAdapter = new MsSqlServerDataProvider();
            MsSqlServerConnectionInfo connectionInfo = new MsSqlServerConnectionInfo();
            connectionInfo.setServerName(rdsi.getServerName());
            connectionInfo.setServerPort((int)Double.parseDouble(rdsi.getServerPort()));
            connectionInfo.setDbName(rdsi.getCurrentDbName());
            connectionInfo.setUsername(rdsi.getUsername());
            connectionInfo.setPassword(rdsi.getPassword());
            msSqlAdapter.connect(connectionInfo);
            resultList = msSqlAdapter.get(nativeQueryText);
            msSqlAdapter.close();
        }
        else if (dataStoreInstance.getDbType().equals(SupportedDataStore.MONGO)) {
            DocumentDataStoreInstance ddsi = (DocumentDataStoreInstance) dataStoreInstance;
            MongoDataProvider mongoDataProvider = new MongoDataProvider();
            MongoConnectionInfo connectionInfo = new MongoConnectionInfo();
            connectionInfo.setServerName(ddsi.getServerName());
            connectionInfo.setServerPort((int) Double.parseDouble(ddsi.getServerPort()));
            connectionInfo.setDbName(ddsi.getCurrentDbName());
            mongoDataProvider.connect(connectionInfo);
            resultList = mongoDataProvider.get(nativeQueryText);
            mongoDataProvider.close();
        }
        return  resultList;
    }

    private void connect() {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);

        InputStream in = FileManager.get().open(filename);

        if (in == null)
            throw new IllegalArgumentException("File: " + filename + " not found");

        model.read(in, null);
    }

    private void persist()
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

    private void disconnect()
    {
        model.close();
    }

}
