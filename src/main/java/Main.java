import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.bson.Document;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Document document;

    public static void main(String[] args) throws IOException, CsvValidationException {

        String csvFilePath = "src/main/resources/mongo.csv";

        CSVReader reader = new CSVReader(new FileReader(csvFilePath));

        List<Student> students = new ArrayList<>();

        while (reader.readNext() != null) { // парсим CSV файл и добавляем полученные объекты в лист

            String[] strings = reader.readNext();

            students.add(new Student(
                    strings[0], Integer.parseInt(strings[1]), strings[2].split(",")
            ));
        }

        MongoClient mongoClient = new MongoClient();

        MongoDatabase testDB = mongoClient.getDatabase("test");

        MongoCollection<Document> mongoStudents = testDB.getCollection("students");



        for (Student student : students) { // добавляем в БД новые объекты

            document = new Document()
                    .append("name", student.getName())
                    .append("age", student.getAge())
                    .append("courses", student.getCourses());

            mongoStudents.insertOne(document);

        }

        FindIterable<Document> documents = mongoStudents.find();

        System.out.println("Всего студентов: " + mongoStudents.countDocuments()); // получаем студентов из БД


    }

}
