package org.hdm.data.service.adapter;

import org.hdm.core.data.service.IHDMConnectInfo;
import org.hdm.core.data.service.IHDMMetaDataAdapter;
import org.hdm.core.objects.*;
import org.hdm.data.service.helper.MsSqlServerHelper;
import org.hdm.data.service.provider.MsSqlServerConnectionInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class MsSqlServerMetaDataAdapter implements IHDMMetaDataAdapter
{

	Connection con = null;

	public Boolean connect(IHDMConnectInfo connectionInfo)
	{
		try
		{
			MsSqlServerConnectionInfo msSqlServerConnectionInfo = (MsSqlServerConnectionInfo)connectionInfo;
			//Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			con = DriverManager.getConnection(MsSqlServerHelper.generateConnectionString(msSqlServerConnectionInfo));
			return true;
		}
		catch(Exception exc)
		{
			System.out.println(exc.getMessage());
			return false;
		}
	}

	public List<IEntity> getEntities()
	{
		List<IEntity> tableNames = new ArrayList<IEntity>();
		String sql = "SELECT * FROM sys.tables";
		Statement stmt = null;
		try
		{
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Table ent = new Table();
				ent.setName(rs.getString("name"));
				ent.setDbType(SupportedDataStore.MSSQLSERVER);


				sql =  "SELECT * FROM sys.all_columns WHERE object_id = " + rs.getString("object_id");

				Statement stmtColumns = con.createStatement();
				ResultSet rsColumns = stmtColumns.executeQuery(sql);
				while (rsColumns.next())
				{
					Column col =new Column();
					col.setName(rsColumns.getString("name"));
					ent.getColumns().add(col);
				}
				rsColumns.close();
				stmtColumns.close();
				tableNames.add(ent);
			}
			rs.close();
			stmt.close();
		}
		catch (SQLException e)
		{
			e.printStackTrace();
		}

		return tableNames;
	}

	public List<String> getAttributeNames()
	{
		return null;
	}

	public List<String> getAttributeNames(String entityName)
	{
		return null;
	}

	public List<String> getRelationshipNames()
	{
		return null;
	}

	public void close()
	{
		if (con != null) try { con.close(); } catch(Exception e) {}
	}
}
