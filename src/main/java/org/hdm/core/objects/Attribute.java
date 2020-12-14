package org.hdm.core.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class Attribute implements IAttribute
{
	private String name;

	private List<SupportedDataStore> dbTypeList = new ArrayList<SupportedDataStore>();

	private List<String> matchingAttributes = new ArrayList<>();

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

	public List<String> getMatchingAttributes() {
		return matchingAttributes;
	}

	public void setMatchingAttributes(List<String> matchingAttributes) {
		this.matchingAttributes = matchingAttributes;
	}
}
