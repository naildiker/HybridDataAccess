package org.hdm.core.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class Entity implements IEntity
{
	private String name;

	private String domainName;

	private List<Attribute> attributes = new ArrayList<>();

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public String getDomainName() {
		return domainName;
	}

	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}

	public List<Attribute> getAttributes() {
		return attributes;
	}

	public void setAttributes(List<Attribute> attributes) {
		this.attributes = attributes;
	}
}
