package org.hdm.core.objects;

public interface IDataStoreInstance {

    SupportedDataStore getDbType();
    String getName();
}
