package com.nixsolutions.ppp;

import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import org.bson.Document;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.function.Consumer;

public class MongoDb {
    private final MongoCollection<Document> collection;

    public MongoDb(MongoCollection<Document> collection) {
        this.collection = collection;
    }

    public void numberOfServingsByMonth(){
        AggregateIterable<Document> documents = collection.aggregate(
                Arrays.asList(
                        Aggregates.unwind("$meals"),
                        Aggregates.group(
                                new Document("month", new Document("$month", "$date")),
                                Accumulators.addToSet("serv", "$meals.name"),
                                Accumulators.push("meals", "$meals")
                        ),
                        Aggregates.unwind("$serv"),
                        Aggregates.project(new Document("month", "$_id.month")
                                .append("mealName", "$serv")
                                .append("meals", new Document("$filter", new Document("input", "$meals")
                                        .append("as", "array")
                                        .append("cond", new Document("$eq", Arrays.asList("$$array.name", "$serv"))))
                                )
                        ),
                        Aggregates.sort(new Document("month", 1)),
                        Aggregates.group("$mealName",
                                Accumulators.push("stats", Document.parse("{ month: '$month', servings: { $sum: '$meals.servings'}}"))
                        )

                )
        ).allowDiskUse(true);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("result_number_of_servings.txt", false));) {
            documents.forEach((Consumer<Document>) document -> {
                try {
                    writer.write(document.toJson() + System.lineSeparator());
                } catch (IOException e) {
                    System.err.println("Error while writing to file");
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            System.err.println("Error during file creation");
            throw new RuntimeException(e);
        }
    }

    public void numberOfOrdersByMonth(){
        AggregateIterable<Document> documents = collection.aggregate(
                Arrays.asList(
                        Aggregates.unwind("$meals"),
                        Aggregates.group(
                                new Document("month", new Document("$month", "$date")),
                                Accumulators.addToSet("serv", "$meals.name"),
                                Accumulators.push("meals", "$meals")
                        ),
                        Aggregates.unwind("$serv"),
                        Aggregates.project(new Document("month", "$_id.month")
                                .append("mealName", "$serv")
                                .append("meals", new Document("$filter", new Document("input", "$meals")
                                        .append("as", "array")
                                        .append("cond", new Document("$eq", Arrays.asList("$$array.name", "$serv"))))
                                )
                        ),
                        Aggregates.sort(new Document("month", 1)),
                        Aggregates.group("$mealName",
                                Accumulators.push("stats", Document.parse("{ month: '$month', orders: { $size: '$meals'}}"))
                        )

                )
        ).allowDiskUse(true);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("result_number_of_orders.txt", false));) {
            documents.forEach((Consumer<Document>) document -> {
                try {
                    writer.write(document.toJson() + System.lineSeparator());
                } catch (IOException e) {
                    System.err.println("Error while writing to file");
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            System.err.println("Error during file creation");
            throw new RuntimeException(e);
        }
    }

    public void top3MealsByMonth(){
        AggregateIterable<Document> documents = collection.aggregate(
                Arrays.asList(
                        Aggregates.unwind("$meals"),
                        Aggregates.group(
                                Document.parse("{ name: '$meals.name', month: { $month: '$date'}}"),
                                Accumulators.sum("servingsCount", "$meals.servings")
                        ),
                        Aggregates.sort(new Document("servingsCount", -1)),
                        Aggregates.group(new Document("month", "$_id.month"),
                                Accumulators.push("topMeals", Document.parse("{ meal: '$_id.name', servingsSold: '$servingsCount'}"))
                        ),
                        Aggregates.project(Document.parse("{ top3meals: { $slice: ['$topMeals', 3]}}")),
                        Aggregates.sort(new Document("_id", 1))

                )
        ).allowDiskUse(true);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter("result_top_3_meals.txt", false));) {
            documents.forEach((Consumer<Document>) document -> {
                try {
                    writer.write(document.toJson() + System.lineSeparator());
                } catch (IOException e) {
                    System.err.println("Error while writing to file");
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            System.err.println("Error during file creation");
            throw new RuntimeException(e);
        }
    }
}
