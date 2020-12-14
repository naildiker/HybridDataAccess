package org.hdm.data.service.adapter;

import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.hdm.core.data.service.IHDMConnectInfo;
import org.hdm.core.data.service.IHDMDataAdapter;
import org.hdm.core.objects.EntityOccurence;
import org.hdm.core.objects.IEntity;

import org.hdm.core.objects.IEntityOccurence;
import org.hdm.data.service.provider.MongoConnectionInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static com.mongodb.client.model.Filters.*;
import static com.mongodb.client.model.Projections.exclude;
import static com.mongodb.client.model.Projections.fields;
import static com.mongodb.client.model.Projections.include;

/**
 * Created by nail.diker on 12/25/2016.
 */

public class MongoDataProvider implements IHDMDataAdapter {

    MongoClient mongoClient = null;
    MongoDatabase mongoDb = null;

    public Boolean connect(IHDMConnectInfo connectionInfo) {
        try {
            MongoConnectionInfo mongoConnectionInfo = (MongoConnectionInfo) connectionInfo;
            mongoClient = new MongoClient(mongoConnectionInfo.getServerName(), mongoConnectionInfo.getServerPort());
            mongoDb = mongoClient.getDatabase(mongoConnectionInfo.getDbName());
            return true;
        } catch (Exception exc) {
            System.out.println(exc.getMessage());
            return false;
        }
    }

    public List<IEntityOccurence> get(String nativeQueryText) {
        List<IEntityOccurence> collValues = new ArrayList<IEntityOccurence>();
        String sql = nativeQueryText;
        try {
            boolean collectionExists = mongoDb.listCollectionNames()
                    .into(new ArrayList<String>()).contains(nativeQueryText);
            if (collectionExists) {
                MongoCollection<Document> collection = mongoDb.getCollection(nativeQueryText);
                //FindIterable<Document> iterator = collection.find(or(eq("name","nail"),eq("surname", "nail")));
                FindIterable<Document> iterator = collection.find().projection(
                        fields(and(include("surname", "name"), exclude("_id")))
                );
                iterator.forEach((Consumer<? super Document>) document -> {
                    String value = "";
                    for (Map.Entry<String, Object> set : document.entrySet()) {
                        value += set.getKey() + " : " + set.getValue() + ", ";
                        System.out.format("%s: %s%n", set.getKey(), set.getValue());
                    }
                    String finalValue = value.substring(0, value.length() - 2);
                    collValues.add(new EntityOccurence() {{
                        setString(finalValue);
                    }});
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return collValues;
    }

    public void close() {
        if (mongoClient != null) try {
            mongoClient.close();
        } catch (Exception e) {
        }
    }
}
