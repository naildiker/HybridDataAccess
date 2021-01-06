package org.hdm.data.service.helper;

import org.hdm.data.service.provider.OracleConnectionInfo;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class OracleHelper
{
	public static String generateConnectionString(OracleConnectionInfo connectionInfo)
	{
		String connectionUrl = "jdbc:oracle:thin:" + connectionInfo.getUsername() +"/" + connectionInfo.getPassword() + "@" + connectionInfo.getServerName() + ":" + connectionInfo.getServerPort() + ":" +
				connectionInfo.getDbName();
		return connectionUrl;
	}

}
