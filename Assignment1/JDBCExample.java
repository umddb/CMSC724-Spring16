import java.sql.*;
import org.json.simple.*;

public class JDBCExample 
{

    public static void main(String[] argv) {

        System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

        try {

            Class.forName("org.postgresql.Driver");

        } catch (ClassNotFoundException e) {

            System.out.println("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
            e.printStackTrace();
            return;

        }

        System.out.println("PostgreSQL JDBC Driver Registered!");

        Connection connection = null;

        try {

            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5433/postgres","postgres", "postgres");

        } catch (SQLException e) {

            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;

        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
            return;
        }

        Statement stmt = null;
        String query = "explain (format json) select * from information_schema.tables;";
        try {
            stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String json_plan = rs.getString(1);
		System.out.println("=================================");
                System.out.println(json_plan + "\t");
		System.out.println("=================================");

		JSONObject root = (JSONObject) ((JSONArray) JSONValue.parse(json_plan)).get(0);
		System.out.println("Number of rows expected: " + ((JSONObject) root.get("Plan")).get("Plan Rows"));
            }

            stmt.close();
        } catch (SQLException e ) {
            System.out.println(e);
        }
    }
}
