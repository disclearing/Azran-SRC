package me.invakid.azran.db;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public enum DatabaseManager {

    INSTANCE;

    DatabaseManager() {}

    private MongoClient client;

    private MongoDatabase database;
    private MongoCollection<Document> detections;

    public void init() {
        client = new MongoClient(new MongoClientURI("mongodb+srv://AzranClient:K10pJQ1oriXTNVi1@azrancluster-cgmkd.mongodb.net/test"));

        database = client.getDatabase("results");
        detections = database.getCollection("detections");
    }

    public void addDetection(String ip, String line) {
        detections.insertOne(new Document("ip", ip).append("string", line));
    }

}
