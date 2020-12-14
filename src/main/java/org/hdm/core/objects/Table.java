package org.hdm.core.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class Table implements IEntity
{
	private String name;

	private List<Column> columns = new ArrayList<>();

	private List<SupportedDataStore> dbTypeList = new ArrayList<SupportedDataStore>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public List<SupportedDataStore> getDbTypeList()
	{
		return dbTypeList;
	}

	public void setDbType(SupportedDataStore dbType)
	{
		this.dbTypeList.add(dbType);
	}

	public List<Column> getColumns() {
		return columns;
	}

	public void setColumns(List<Column> columns) {
		this.columns = columns;
	}
}
