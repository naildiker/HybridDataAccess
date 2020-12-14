package org.hdm.data.service.provider;

import org.hdm.core.data.service.IHDMConnectInfo;

public class MsSqlServerConnectionInfo  implements IHDMConnectInfo
{
	private String serverName;

	private int serverPort;

	private String dbName;

	private String username;

	private String password;

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

	public String getUsername()
	{
		return username;
	}

	public void setUsername(String username)
	{
		this.username = username;
	}

	public String getPassword()
	{
		return password;
	}

	public void setPassword(String password)
	{
		this.password = password;
	}

}
