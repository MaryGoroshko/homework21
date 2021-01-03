import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class Main {

    public static void main(String[] args) {
        new Main().run();
    }

    public void run() {

        try (Connection connection = DriverManager.getConnection("jdbc:sqlite:students.db");
             BufferedReader input = new BufferedReader(new InputStreamReader(System.in))) {

            String number = "";

            while (!"6".equals(number)) {

                System.out.println(menu);

                number = input.readLine().trim();
                int choice = Integer.parseInt(number);

                switch (choice) {
                    case 1:
                        createTable(connection);
                        System.out.println("Table has been created.");
                        break;
                    case 2:
                        System.out.println("Enter student name");
                        String name = input.readLine();
                        System.out.println("Enter student last name");
                        String lastName = input.readLine();
                        System.out.println("Enter group name");
                        String group = input.readLine();
                        System.out.println("Enter faculty");
                        String faculty = input.readLine();

                        insert(connection, name, lastName, group, faculty);
                        System.out.println("New student added successfully.");
                        break;
                    case 3:
                        selectAll(connection);
                        break;
                    case 4:
                        System.out.println("Enter text to search:");
                        String search = input.readLine();
                        search(connection, search);
                        break;
                    case 5:
                        deleteTable(connection);
                        System.out.println("Table has been deleted.");
                        break;
                    case 6:
                        System.out.println("Exit.");
                        break;
                    default:
                        break;
                }
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println(e + " not number. Please enter a number from 1 to 6.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private final String menu = "Enter a number from 1 to 6: \n" +
            "1 - Create table\n" +
            "2 - Add new student\n" +
            "3 - Show all students sorted by name from A to Z.\n" +
            "4 - Table search\n" +
            "5 - Delete table\n" +
            "6 - Exit";

    private void createTable(Connection connection) throws SQLException {

        final String CreateStudentsTable = "CREATE TABLE IF NOT EXISTS students (" +
                " id INTEGER PRIMARY KEY AUTOINCREMENT," +
                " name VARCHAR(50)," +
                " last_name VARCHAR(50)," +
                " group_name VARCHAR(10)," +
                " faculty VARCHAR(50)" +
                ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(CreateStudentsTable);
        }
    }

    private void deleteTable(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate("DROP TABLE IF EXISTS students");
        }
    }

    private void insert(Connection connection, String name, String lastName, String group, String faculty) throws SQLException {

        final String insertStudent =
                "INSERT INTO students(name,last_name,group_name,faculty) VALUES(" + "'" + name + "'," + "'" + lastName + "',"
                        + "'" + group + "'," + "'" + faculty + "'" + ")";

        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(insertStudent);
        }

//        second option for adding students via Prepared Statement
//        final String insertStudent =
//                "INSERT INTO students(name,last_name,group_name,faculty) VALUES(?,?,?,?)";
//        try (PreparedStatement preparedStatement = connection.prepareStatement(insertStudent)) {
//            preparedStatement.setString(1, name);
//            preparedStatement.setString(2, lastName);
//            preparedStatement.setString(3, group);
//            preparedStatement.setString(4, faculty);
//            preparedStatement.executeUpdate();
//        } catch (SQLException e) {
//            throw new RuntimeException("Failed to insert student", e);
//        }
    }

    private void selectAll(Connection connection) throws SQLException {

        try (Statement statement = connection.createStatement()) {

            ResultSet resultSet = statement.executeQuery("SELECT * FROM students ORDER BY name");

            System.out.format("|%4s|%15s|%15s|%15s|%15s|%n", "id", "name", "last_name", "group_name", "faculty");
            System.out.println("|----|---------------|---------------|---------------|---------------|");

            while (resultSet.next()) {
                int id = resultSet.getInt(1);
                String name = resultSet.getString("name");
                String lastName = resultSet.getString("last_name");
                String groupName = resultSet.getString("group_name");
                String faculty = resultSet.getString("faculty");
                System.out.format("|%4d|%15s|%15s|%15s|%15s|%n", id, name, lastName, groupName, faculty);
            }
        }
    }

    private void search(Connection connection, String search) throws SQLException {

        PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM students " +
                "WHERE ? IN (name,last_name,group_name,faculty) ORDER BY name");
        preparedStatement.setString(1, search);
        ResultSet resultSet = preparedStatement.executeQuery();

        System.out.format("|%4s|%15s|%15s|%15s|%15s|%n", "id", "name", "last_name", "group_name", "faculty");
        System.out.println("|----|---------------|---------------|---------------|---------------|");

        while (resultSet.next()) {
            int id = resultSet.getInt("id");
            String name = resultSet.getString("name");
            String lastName = resultSet.getString("last_name");
            String groupName = resultSet.getString("group_name");
            String faculty = resultSet.getString("faculty");
            System.out.format("|%4d|%15s|%15s|%15s|%15s|%n", id, name, lastName, groupName, faculty);
        }
    }
}
