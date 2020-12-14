package org.hdm.data.service.adapter;

import org.hdm.core.data.service.IHDMConnectInfo;
import org.hdm.core.data.service.IHDMDataAdapter;
import org.hdm.core.data.service.IHDMMetaDataAdapter;
import org.hdm.core.objects.*;
import org.hdm.data.service.helper.MsSqlServerHelper;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;
import sun.awt.SunHints;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */

public class MsSqlServerDataProvider implements IHDMDataAdapter
{

	Connection con = null;

	public Boolean connect(IHDMConnectInfo connectionInfo)
	{
		try
		{
			MsSqlServerConnectionInfo msSqlServerConnectionInfo = (MsSqlServerConnectionInfo)connectionInfo;
			con = DriverManager.getConnection(MsSqlServerHelper.generateConnectionString(msSqlServerConnectionInfo));
			return true;
		}
		catch(Exception exc)
		{
			System.out.println(exc.getMessage());
			return false;
		}
	}

	public List<IEntityOccurence> get(String nativeQueryText)
	{
		List<IEntityOccurence> tableValues = new ArrayList<IEntityOccurence>();
		String sql = nativeQueryText;
		Statement stmt = null;
		try
		{
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String value = "";
				for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++)
				{
					value += rs.getMetaData().getColumnLabel(i) + " : " + rs.getString(i)+ ", ";
					System.out.println(value);
				}
				String finalValue = value.substring(0,value.length()-2);
				tableValues.add(new EntityOccurence() {{setString(finalValue);}} );
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return tableValues;
	}

	public void close()
	{
		if (con != null) try { con.close(); } catch(Exception e) {}
	}
}
