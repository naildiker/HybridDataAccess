package org.hdm.data.service.adapter;

import org.hdm.core.data.service.IHDMConnectInfo;
import org.hdm.core.data.service.IHDMMetaDataAdapter;
import org.hdm.core.objects.Entity;
import org.hdm.core.objects.IEntity;
import org.hdm.core.objects.SupportedDataStore;
import org.hdm.data.service.provider.Neo4jConnectionInfo;
import org.neo4j.driver.v1.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class Neo4jMetaDataAdapter implements IHDMMetaDataAdapter
{
	Session session = null;
	Driver driver = null;

	public Boolean connect(IHDMConnectInfo connectionInfo)
	{
		try
		{
			Neo4jConnectionInfo neo4jConnectionInfo = (Neo4jConnectionInfo)connectionInfo;
			driver = GraphDatabase.driver( neo4jConnectionInfo.getUrl(), AuthTokens.basic(neo4jConnectionInfo.getUsername(), neo4jConnectionInfo.getPassword()));
			session = driver.session();
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
		StatementResult result = session.run( "START n=node(*) RETURN distinct labels(n) AS labelName");
		List<IEntity> labels = new ArrayList<>();
		while ( result.hasNext() )
		{
			Record record = result.next();
			for (Object labelObject : record.get( "labelName" ).asList())
			{
				final String labelName = labelObject.toString();
				if (! labels.contains(labelName))
				{
					//labels.add(new Entity(){{ setName(labelName); setDbType(SupportedDataStore.NEO4J);}});
					labels.add(new Entity(){{ setName(labelName); }});
				}
			}
		}
		return labels;
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
		if (session != null ) session.close();
		if ( driver != null ) driver.close();
	}
}
