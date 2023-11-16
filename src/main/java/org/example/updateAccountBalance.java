
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;

public class updateAccountBalance{
    private static void updateAccountBalance(Connection connection, TransactionReader transaction){
        try{
            String transactionSql = "insert into transaction (id, account_id, amount, transaction_date) " +
                    "values ( ?, ? ,? ,?)";
            String bankAccountSql = "update bank_account set balance = balance + ? where id = ?";

            connection.setAutoCommit(false);

            PreparedStatement transactionStatement = connection.prepareStatement(transactionSql);
            transactionStatement.setInt(1, transaction.getId());
            transactionStatement.setInt(2,transaction.getAccountId());
            transactionStatement.setDouble(3,transaction.getAmount());
            transactionStatement.setTimestamp(4,new Timestamp(transaction.getTransactionDate().getTime()));
            transactionStatement.executeUpdate();
            transactionStatement.close();

            PreparedStatement bankAccountStatement = connection.prepareStatement(bankAccountSql);
            bankAccountStatement.setDouble(1, transaction.getAmount);
            bankAccountStatement.setInt(2,transaction.getAccountId());
            bankAccountStatement.executeUpdate();
            bankAccountStatement.close();

            connection.commit();
            System.out.println("acount balance update for transaction: " + transaction);
        } catch (SQLException e){
            try {
                connection.rollback();
            }catch (SQLException ex){
                ex.printStackTrace();
            }
            System.out.println("error updating account balance for transaction" + transaction);
            e.printStackTrace();
        }finally {
            try {
                connection.setAutoCommit(true);
                connection.close();
            }catch (SQLException ex){
                ex.printStackTrace();
            }
        }
    }
}
