package org.hdm.core.objects;

import java.util.List;

public class DocumentDataStoreInstance implements  IDataStoreInstance {
    private String name;
    private String serverName;
    private String serverPort;
    private String currentDbName;
    private SupportedDataStore dbType;
    private List<String> collectionNames;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public SupportedDataStore getDbType()
    {
        return dbType;
    }

    public void setDbType(SupportedDataStore dbType)
    {
        this.dbType = dbType;
    }

    public List<String> getCollectionNames() {
        return collectionNames;
    }

    public void setCollectionNames(List<String> collectionNames) {
        this.collectionNames = collectionNames;
    }

    public String getCurrentDbName() {
        return currentDbName;
    }

    public void setCurrentDbName(String currentDbName) {
        this.currentDbName = currentDbName;
    }
}
