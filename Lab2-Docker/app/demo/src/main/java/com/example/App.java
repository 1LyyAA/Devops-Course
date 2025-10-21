package com.example;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class App {
    public static void main(String[] args) {
        String url = "jdbc:postgresql://db:5432/postgres";
        String username = "postgres";
        String password = "1234";
        
        try (Connection connection = DriverManager.getConnection(url, username, password)) {
            System.out.println("Successfully connected to PostgreSQL!");
            
            // Создаем схему public если она не существует
            try (Statement stmt = connection.createStatement()) {
                System.out.println("Creating schema 'public'...");
                stmt.execute("CREATE SCHEMA IF NOT EXISTS public");
                System.out.println("Schema 'public' created or already exists.");
            } catch (SQLException schemaEx) {
                System.err.println("Schema creation error: " + schemaEx.getMessage());
            }
            
            // Проверяем существование таблицы Users и её структуру
            System.out.println("Checking if table 'users' exists...");
            if (tableExists(connection, "users")) {
                System.out.println("Table 'users' already exists. Dropping old table...");
                dropUsersTable(connection);
                System.out.println("Old table dropped.");
            }
            
            System.out.println("Creating new table 'users'...");
            createUsersTable(connection);
            System.out.println("Table 'users' created successfully!");
            
            // Запускаем интерактивный режим
            runInteractiveMode(connection);
            
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
        }
    }
    
    /**
     * Проверяет существование таблицы в базе данных
     */
    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        DatabaseMetaData metaData = connection.getMetaData();
        try (ResultSet resultSet = metaData.getTables(null, "public", tableName.toLowerCase(), new String[]{"TABLE"})) {
            return resultSet.next();
        }
    }
    
    /**
     * Удаляет таблицу Users
     */
    private static void dropUsersTable(Connection connection) throws SQLException {
        String dropTableSQL = "DROP TABLE IF EXISTS public.users CASCADE";
        
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(dropTableSQL);
        }
    }
    
    /**
     * Создает таблицу Users
     */
    private static void createUsersTable(Connection connection) throws SQLException {
        String createTableSQL = """
            CREATE TABLE public.users (
                userid SERIAL PRIMARY KEY,
                username VARCHAR(50) NOT NULL UNIQUE
            )
            """;
        
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(createTableSQL);
        }
    }
    
    /**
     * Запускает интерактивный режим работы с пользователями
     */
    private static void runInteractiveMode(Connection connection) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("\n=== User Management System ===");
        System.out.println("Available commands:");
        System.out.println("  addUser - Add new user");
        System.out.println("  showUsers - Show all users");
        System.out.println("  exit - Exit program");
        System.out.println("===============================");
        
        while (true) {
            System.out.print("\nEnter command: ");
            String command = scanner.nextLine().trim().toLowerCase();
            
            try {
                switch (command) {
                    case "adduser":
                        addUser(connection, scanner);
                        break;
                    case "showusers":
                        showUsers(connection);
                        break;
                    case "exit":
                        System.out.println("Goodbye!");
                        return;
                    default:
                        System.out.println("Unknown command: " + command);
                        System.out.println("Available: addUser, showUsers, exit");
                }
            } catch (SQLException e) {
                System.err.println("Database error: " + e.getMessage());
            }
        }
    }
    
    /**
     * Добавляет нового пользователя
     */
    private static void addUser(Connection connection, Scanner scanner) throws SQLException {
        System.out.print("Enter username: ");
        String username = scanner.nextLine().trim();
        
        if (username.isEmpty()) {
            System.out.println("Error: Username is required!");
            return;
        }
        
        String insertSQL = "INSERT INTO public.users (username) VALUES (?)";
        
        try (PreparedStatement stmt = connection.prepareStatement(insertSQL)) {
            stmt.setString(1, username);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("✅ User '" + username + "' added successfully!");
            } else {
                System.out.println("❌ Failed to add user.");
            }
        } catch (SQLException e) {
            if (e.getMessage().contains("unique")) {
                System.out.println("❌ Error: Username already exists!");
            } else {
                throw e;
            }
        }
    }
    
    /**
     * Показывает всех пользователей
     */
    private static void showUsers(Connection connection) throws SQLException {
        String selectSQL = "SELECT userid, username FROM public.users ORDER BY userid";
        
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(selectSQL)) {
            
            System.out.println("\n=== All Users ===");
            System.out.printf("%-8s %-20s%n", "User ID", "Username");
            System.out.println("-".repeat(30));
            
            boolean hasUsers = false;
            while (rs.next()) {
                hasUsers = true;
                System.out.printf("%-8d %-20s%n",
                    rs.getInt("userid"),
                    rs.getString("username")
                );
            }
            
            if (!hasUsers) {
                System.out.println("No users found. Use 'addUser' to add the first user.");
            }
            System.out.println("=".repeat(30));
        }
    }
}