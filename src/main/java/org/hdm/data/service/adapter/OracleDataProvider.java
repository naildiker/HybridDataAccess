package org.hdm.data.service.adapter;


import org.hdm.core.data.service.IHDMConnectInfo;
import org.hdm.core.data.service.IHDMDataAdapter;
import org.hdm.core.objects.EntityOccurence;
import org.hdm.core.objects.IEntityOccurence;
import org.hdm.data.service.helper.OracleHelper;
import org.hdm.data.service.provider.OracleConnectionInfo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;


/**
 * Created by nail.diker on 12/25/2016.
 */

public class OracleDataProvider implements IHDMDataAdapter
{

	Connection oracleConn = null;

	public Boolean connect(IHDMConnectInfo connectionInfo)
	{
		try
		{
			OracleConnectionInfo oracleConnectionInfo = (OracleConnectionInfo)connectionInfo;
			oracleConn = DriverManager.getConnection(OracleHelper.generateConnectionString(oracleConnectionInfo));
			if (oracleConn != null) {
				System.out.println("Connected with connection #1");
			}
			return true;
		} catch (SQLException ex) {
			ex.printStackTrace();
			return false;
		}
	}

	public List<IEntityOccurence> get(String nativeQueryText)
	{
		List<IEntityOccurence> collValues = new ArrayList<IEntityOccurence>();
		String sql = nativeQueryText;
		try
		{

		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return collValues;
	}

	public void close()
	{
		try {
			if (oracleConn != null && !oracleConn.isClosed())  oracleConn.close();
		} catch(Exception e)
		{ e.printStackTrace();}
	}
}
