package org.hdm.data.service.provider;

import org.hdm.core.data.service.IHDMConnectInfo;

/**
 * Created by nail.diker on 12/25/2016.
 */
public abstract class AbstractDbServerBasedConnectionInfo implements IHDMConnectInfo
{
	private String serverName;

	private int serverPort;

	private String dbName;

	public String getServerName()
	{
		return serverName;
	}

	public void setServerName(String serverName)
	{
		this.serverName = serverName;
	}

	public int getServerPort()
	{
		return serverPort;
	}

	public void setServerPort(int serverPort)
	{
		this.serverPort = serverPort;
	}

	public String getDbName()
	{
		return dbName;
	}

	public void setDbName(String dbName)
	{
		this.dbName = dbName;
	}
}
