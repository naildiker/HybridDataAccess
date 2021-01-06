package org.hdm.data.service.adapter;

import org.hdm.core.data.service.IHDMConnectInfo;
import org.hdm.core.data.service.IHDMMetaDataAdapter;
import org.hdm.core.objects.Column;
import org.hdm.core.objects.IEntity;
import org.hdm.core.objects.SupportedDataStore;
import org.hdm.core.objects.Table;
import org.hdm.data.service.helper.OracleHelper;
import org.hdm.data.service.provider.OracleConnectionInfo;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class OracleMetaDataAdapter implements IHDMMetaDataAdapter
{

	Connection con = null;

	public Boolean connect(IHDMConnectInfo connectionInfo)
	{
		try
		{
			OracleConnectionInfo oracleConnectionInfo = (OracleConnectionInfo)connectionInfo;
			con = DriverManager.getConnection(OracleHelper.generateConnectionString(oracleConnectionInfo));
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
		String sql = "SELECT owner, table_name FROM user_tables;";
		Statement stmt = null;
		try
		{
			stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				Table ent = new Table();
				ent.setName(rs.getString("table_name"));
				ent.setDbType(SupportedDataStore.ORACLE);


				sql =  "SELECT table_name, column_name, data_type, data_length\n" +
						"FROM USER_TAB_COLUMNS\n" +
						"WHERE table_name = '" + rs.getString("table_name") + "'";

				Statement stmtColumns = con.createStatement();
				ResultSet rsColumns = stmtColumns.executeQuery(sql);
				while (rsColumns.next())
				{
					Column col =new Column();
					col.setName(rsColumns.getString("column_name"));
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
