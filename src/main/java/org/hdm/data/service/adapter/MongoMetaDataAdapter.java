package org.hdm.data.service.adapter;

import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.hdm.core.data.service.IHDMConnectInfo;
import org.hdm.core.data.service.IHDMMetaDataAdapter;
import org.hdm.core.objects.*;
import org.hdm.data.service.provider.MongoConnectionInfo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * Created by nail.diker on 12/25/2016.
 */
public class MongoMetaDataAdapter implements IHDMMetaDataAdapter
{
	MongoClient mongoClient = null;
	MongoDatabase mongoDb = null;

	public Boolean connect(IHDMConnectInfo connectionInfo)
	{
		try
		{
			MongoConnectionInfo mongoConnectionInfo = (MongoConnectionInfo)connectionInfo;
			mongoClient = new MongoClient( mongoConnectionInfo.getServerName() , mongoConnectionInfo.getServerPort());
			mongoDb = mongoClient.getDatabase(mongoConnectionInfo.getDbName());
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
		List<IEntity> mongoCollections = new ArrayList<IEntity>();
		for (String collectionName : mongoDb.listCollectionNames())
		{
			Collection mongoEntity = new Collection();
			mongoEntity.setName(collectionName);
			mongoEntity.setDbType(SupportedDataStore.MONGO);

			MongoCollection<org.bson.Document> collection = mongoDb.getCollection(collectionName);
			FindIterable<Document> findIterable = collection.find();
			MongoCursor<Document> cursor = findIterable.iterator();

			if (cursor.hasNext())
			{
				Document doc = cursor.next();
				for (String collectionKey : doc.keySet())
				{
					mongoEntity.getKeys().add(new Key(){{ setName(collectionKey); }});
				}
			}

			mongoCollections.add(mongoEntity);
		}
		return mongoCollections;
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
		if (mongoClient != null ) mongoClient.close();
	}
}
