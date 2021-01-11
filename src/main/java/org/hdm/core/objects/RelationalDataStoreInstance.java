package org.hdm.core.objects;

import java.util.ArrayList;
import java.util.List;

public class RelationalDataStoreInstance implements  IDataStoreInstance {
    private String name;
    private String serverName;
    private String serverPort;
    private String username;
    private String password;
    private String dataModelType;
    private SupportedDataStore dbType;
    private List<String> dbNames;
    private String currentDbName;

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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getDataModelType() {
        return dataModelType;
    }

    public void setDataModelType(String dataModelType) {
        this.dataModelType = dataModelType;
    }

    public SupportedDataStore getDbType()
    {
        return dbType;
    }

    public void setDbType(SupportedDataStore dbType)
    {
        this.dbType = dbType;
    }

    public List<String> getDbNames() {
        return dbNames;
    }

    public void setDbNames(List<String> dbNames) {
        this.dbNames = dbNames;
    }

    public String getCurrentDbName() {
        return currentDbName;
    }

    public void setCurrentDbName(String currentDbName) {
        this.currentDbName = currentDbName;
    }
}
