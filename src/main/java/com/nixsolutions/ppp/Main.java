package com.nixsolutions.ppp;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

import java.io.*;
import java.util.Properties;


public class Main {
    public static final String FILE_PROPERTY = "db.properties";

    public static void main(String[] args) {
        Properties prop = new Properties();
        try (InputStream inputStream = Main.class.getClassLoader().getResourceAsStream(FILE_PROPERTY)){
        if(inputStream == null) {
            throw new FileNotFoundException("Property file : " + FILE_PROPERTY + " not found");
        }
            prop.load(inputStream);
        } catch (IOException e) {
            System.err.println("Exception " + e);
        }

        MongoClient mongoClient = new MongoClient(new MongoClientURI(prop.getProperty("mongodb.url")));

        MongoDatabase database = mongoClient.getDatabase("yeshenko");

        MongoCollection<Document> collection = database.getCollection("orders");
        MongoDb mongoDb = new MongoDb(collection);
        System.out.println("Start the aggregate");
        mongoDb.numberOfServingsByMonth();
        mongoDb.numberOfOrdersByMonth();
        mongoDb.top3MealsByMonth();
        System.out.println("finish the aggregate");
    }


    private Properties getPropertyValue() throws IOException {
        Properties prop = new Properties();

        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(FILE_PROPERTY);

        if(inputStream == null) {
            throw new FileNotFoundException("Property file : " + FILE_PROPERTY + " not found");
        }

        prop.load(inputStream);

        return prop;
    }
}

