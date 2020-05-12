import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import org.bson.BsonDocument;
import org.bson.Document;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static Document document;

    public static void main(String[] args) throws IOException, CsvException {

        String csvFilePath = "src/main/resources/mongo.csv";

        CSVReader reader = new CSVReader(new FileReader(csvFilePath));

        List<Student> students = new ArrayList<>();

        List<String[]> testList = reader.readAll();

        for (String[] strings : testList) {
            students.add(new Student(strings[0], Integer.parseInt(strings[1]), strings[2].split(",")));
        }

        MongoClient mongoClient = new MongoClient();

        MongoDatabase testDB = mongoClient.getDatabase("test");

        MongoCollection<Document> mongoStudents = testDB.getCollection("students");

        mongoStudents.drop(); // в случае перезапуска программы, удаляем коллекцию, чтоб не дублировались данные

        for (Student student : students) { // добавляем в БД новые объекты

            document = new Document()
                    .append("name", student.getName())
                    .append("age", student.getAge())
                    .append("courses", student.getCourses());

            mongoStudents.insertOne(document);

        }

        System.out.println("Всего студентов: " + mongoStudents.countDocuments()); // получаем количество студентов из БД


        /*
          Выводим список студентов старше 40 лет с именами, возрастом и их курсами
         */

        BsonDocument mostElderStudentsQuery = BsonDocument.parse("{age: {$gt:40}}");

        FindIterable<Document> studentsElderThan40years = mongoStudents.find(mostElderStudentsQuery);

        System.out.println("Студенты старше 40 лет");

        System.out.println(mongoStudents.countDocuments(mostElderStudentsQuery) + " чел.:");

        for (Document doc : studentsElderThan40years) {

            System.out.println(doc.get("name") + " " + doc.get("age") + " лет " + doc.get("courses"));

        }

        /*
         * Имя самого молодого студента
         */

        FindIterable<Document> youngestStudentQuery = mongoStudents.find().sort(BsonDocument.parse("{age: 1}")).limit(1);
        String youngestStudent = "";

        for (Document doc : youngestStudentQuery) {
            youngestStudent = doc.get("name").toString();
        }

        System.out.println("Самый молодой студент: " + youngestStudent);

        /*
         * Выводим имя и курсы самого возрастного студента
         */

        FindIterable<Document> eldestStudentQuery = mongoStudents.find().sort(BsonDocument.parse("{age: -1}")).limit(1);

        String eldestStudentCourses = "";

        for (Document doc : eldestStudentQuery) {
            eldestStudentCourses = doc.get("courses").toString();
        }

        System.out.println("Cписок курсов самого возрастного студента: " + eldestStudentCourses);

        /*
         * Выводим имена самых молодых студентов
         */

        FindIterable<Document> youngestStudentsQuery = mongoStudents.find(BsonDocument.parse("{age: 18}"));


        System.out.println("Имена самых молодых студентов: ");
        for (Document doc : youngestStudentsQuery) {
            String studentName = doc.get("name").toString() + " " + doc.get("age") + " лет";
            System.out.println(studentName);
        }
    }
}
