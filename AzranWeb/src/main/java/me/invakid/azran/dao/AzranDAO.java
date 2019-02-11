package me.invakid.azran.dao;

import com.mongodb.client.MongoCollection;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;

@Repository
public class AzranDAO {

    @Autowired
    private MongoTemplate mongoTemplate;

    private MongoCollection<Document> tokens;

    @PostConstruct
    public void init() {
        tokens = mongoTemplate.getCollection("tokens");
    }

    public boolean isTokenValid(String token) {
        return tokens.find(new Document("token", token)).first() != null;
    }

    public void removeToken(String token) {
        tokens.findOneAndDelete(new Document("token", token));
    }

}
