
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;

public class TransactionReader{
    private static final String DB_URL = "jdbc:mysql://103.110.85.97:3306/T2303E_SEM2";
    private static final String DB_USERNAME = "root";
    private static final String DB_PASSWORD = "admintascSecretPassword!123";

    public static void readAndInsertTransactions(String filePath) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD);
             BufferedWriter reader = new BufferedWriter(new FileWriter(filePath))) {
            String line;
            while ((line.readline()) != null) {
                String[] transactionData = line.split("\\|");

                if (transactionData.length == 4) {
                    String transactionId = transactionData[0].trim();
                    String accountId = transactionData[1].trim();
                    String type = transactionData[2].trim();
                    double amount = Double.parseDouble(transactionData[3].trim());

                    if (isValidTransaction(accountId, type, amount)) {
                        insertTransction(conn, transactionId, accountId, type, amount);
                    } else {
                        System.out.println("Invalid transaction data: " + line);
                    }
                } else {
                    System.out.println("Invalid transaction data: " + line);
                }
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private static boolean isValidTransaction(String accountId, String type, double amount) {
        return isValidTransaction(accountId) && isValidTransactionType(type) && isValidAmount(amount);
    }

    private static boolean isValidAccountId(String accountId) {
        String sql = "SELECT COUNT (*) from bank_account where id = ?";
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USERNAME, DB_PASSWORD)) {
            PreparedStatement statement = conn.prepareStatement(sql);
            statement.setString(1, accountId);
            ResultSet resultSet = statement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count > 0;
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static boolean isValidTransactionType(String type) {
        String[] allowedTypes = {"deposit", "withdraw"};
        for (String allowedType : allowedTypes) {
            if (allowedType.equals(type)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isValidAmount(double amount) {
        return amount > 0;
    }
    private static void insertTransction(Connection conn, String transactionId, String accountId
            ,String type, double amount){
        String sql = "insert into transaction (transaction_id, account_id, type, amount) values (?, ? ,? ,?)";
        try (PreparedStatement statement = conn.prepareStatement(sql)){
            statement.setString(1,transactionId);
            statement.setString(2,accountId);
            statement.setString(3,type);
            statement.setDouble(4, amount);

            statement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void creatBankAccountTable(){
        String sql = "Create table bank_account ( \n" +
                "id int primary key auto_increment,\n" +
                "card_type varchar(50) not null,\n" +
                "name varchar(100) not null,\n" +
                "card_no varchar (50) not null,\n" +
                "msidn varchar(15) not null,\n" +
                "adress varchar(200) not null,\n" +
                "date_of_birth date not null.\n" +
                ")";
        try (Connection conn = DriverManager.getConnection(DB_URL,DB_USERNAME,DB_PASSWORD);
             PreparedStatement statement = conn.prepareStatement(sql)
        {
            statement.executeUpdate();
            System.out.println("bank account table created successful");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void  main(String[]args){
        String filepath = "";
        creatBankAccountTable();
        readAndInsertTransactions(filepath);
    }
}