package org.hdm.data.service.helper;

import org.hdm.data.service.provider.MsSqlServerConnectionInfo;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class MsSqlServerHelper
{
	public static String generateConnectionString(MsSqlServerConnectionInfo connectionInfo)
	{
		String connectionUrl = "jdbc:sqlserver://" + connectionInfo.getServerName() + ":" + connectionInfo.getServerPort() + ";" +
				"databaseName=" + connectionInfo.getDbName()  + ";user=" + connectionInfo.getUsername() + ";password=" + connectionInfo.getPassword();
		return connectionUrl;
	}

}
