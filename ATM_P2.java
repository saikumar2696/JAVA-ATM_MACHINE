import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;

public class ATM_P2{

    Map<Integer, String> map = new HashMap<Integer, String>();

    DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
    Scanner sc = new Scanner(System.in);
    int counter = 1;
    boolean istrue = true;

        String sql1 = "select * from atm2";
        String sql2 = "select * from ministatement2";
        Connection cc = DriverManager.getConnection("jdbc:mysql://localhost:3306/atm", "root", "Login@243");
        PreparedStatement stmt = cc.prepareStatement(sql1,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery();
        PreparedStatement stmt2 = cc.prepareStatement(sql2,ResultSet.TYPE_SCROLL_SENSITIVE,ResultSet.CONCUR_UPDATABLE);
        ResultSet rs2 = stmt2.executeQuery();
        ResultSetMetaData rsmd = rs2.getMetaData();

    public boolean Verify_Id(long gno) throws SQLException {

        rs.next();
        for (int i = 1; i <= 5; i++)
        {
            if (gno == rs.getLong(1)) {
                System.out.println("Enter the 4 Digit Pin : ");
                int gpin = sc.nextInt();
                if (gpin == rs.getInt(2)) {
                    System.out.println(" VERIFICATION SUCESSFULL ");
                    istrue=true;
                    break;
                }
                else
                {
                    System.out.println("INVALID PIN");
                    System.exit(0);
                }
            }
            else{
                istrue=false;
                rs.next();
            }
            counter++;
        }
        if(istrue==false) {
            System.out.println("INVALID CARD");
        }
        return istrue;
    }
    public void balanceEnquiry() throws SQLException {
       rs.absolute(counter);
       rs2.absolute(counter);
        System.out.println("Account Balance is : " + rs.getDouble(3));
        rs2.updateDouble(2,rs.getDouble(3));
        rs2.updateRow();
        LocalDateTime dt = LocalDateTime.now();
        map.put(2,dt.format(df));
    }

    public void withdraw() throws SQLException {
        rs.absolute(counter);
        rs2.absolute(counter);
        System.out.println("Enter the amount to withdraw");
        double gwamount = sc.nextDouble();
        if (gwamount < 30000) {
            if (gwamount<rs.getDouble(3)) {
                rs.updateDouble(5, gwamount);
                rs.updateDouble(3, (rs.getDouble(3) - gwamount));
                rs.updateRow();
                System.out.println("Balance After Withdraw is : " + rs.getDouble(3));
                rs2.updateDouble(4, gwamount);
                rs2.updateRow();
                LocalDateTime dt = LocalDateTime.now();
                map.put(3,dt.format(df));

            } else
                System.out.println("Withdraw amount should not be more than current Balance");
        }
        else
            System.out.println("Daily withdraw limit is 30000");
    }

    public void deposit() throws SQLException {
        rs.absolute(counter);
        rs2.absolute(counter);
        System.out.println("Enter the amount to Deposit");
        double gdamount = sc.nextDouble();
        rs.updateObject(4, gdamount);
        rs.updateObject(3, (rs.getDouble(3) + gdamount));
        System.out.println("Amount has been deposited Sucessfully");
        rs.updateRow();
        System.out.println("Balance Amount is : " + rs.getDouble(3));
        rs2.updateDouble(3, gdamount);
        LocalDateTime dt = LocalDateTime.now();
        rs2.updateRow();
        map.put(4,(dt.format(df)));
    }
    public void pinchange() throws SQLException {
       rs.absolute(counter);
       rs2.absolute(counter);
        System.out.println("Enter the new 4 Digit PIN");
        int newpin = sc.nextInt();
        rs.updateInt(2, newpin);
        rs.updateRow();
        System.out.println("PIN had been Changed Sucessfully");
        rs2.updateString(5, "****  PIN CHANGED");
        LocalDateTime dt = LocalDateTime.now();
        rs2.updateRow();
        map.put(5,dt.format(df));
    }

    public void ministatement() throws SQLException {
        rs.absolute(counter);
        rs2.absolute(counter);
        System.out.println(counter);
        System.out.println("========================================");
        System.out.println("============== C360 BANK ===============");
        System.out.println("========================================");
        LocalDateTime dt = LocalDateTime.now();
        System.out.println("Date & Time : " + (dt.format(df)) + "\t" + "\t" + "Record No: " + rs.getString(1));
        System.out.println("Card NO : xxxxxx" + (rs.getLong(1) % 10000));
        Iterator<Map.Entry<Integer, String>> mapitr = map.entrySet().iterator();
        while (mapitr.hasNext()) {
            Map.Entry<Integer, String> n = mapitr.next();
            if(n.getKey()==null) {}
            else
                System.out.println(n.getValue() + "\t" + "\t" + rs2.getObject(n.getKey()) + " " + rsmd.getColumnName((n.getKey())));
        }
        }

    public void exitmessage()
    {
        System.out.println("------THANKYOU------");
        System.out.println("Please Collect your Card Before you leave");
    }

    public static void main(String[] args) throws SQLException {

        Scanner sc = new Scanner(System.in);
        ATM_P2 atm = new ATM_P2();
        System.out.println("---------Welcome to C360 Bank----------");
        System.out.println(" Verification Process");
        System.out.println("Please enter your card number : ");
        long gno = sc.nextLong();
        atm.Verify_Id(gno);
        if (atm.istrue==true) {
            while (true) {
                System.out.println(" 1. Balance Inquiry\n" +
                        " 2. Withdraw Amount\n" +
                        " 3. DepositAmount \n" +
                        " 4.PIN Change\n" +
                        " 5. View Mini Statement\n" +
                        " 6. Exit");
                System.out.println("choose the Operation you want to perform");
                System.out.println("Enter the Index of Operation: ");

                int value = sc.nextInt();
                switch (value) {
                    case 1: {
                        atm.balanceEnquiry();
                        break;
                    }

                    case 2: {
                        atm.withdraw();
                        break;
                    }
                    case 3: {
                        atm.deposit();
                        break;
                    }
                    case 4: {
                        atm.pinchange();
                        break;
                    }
                    case 5: {
                        atm.ministatement();
                        break;
                    }
                    case 6: {
                        atm.exitmessage();
                        System.exit(0);
                    }
                }
            }
        }
        else System.exit(0);
    }
    public ATM_P2() throws SQLException {}
}

