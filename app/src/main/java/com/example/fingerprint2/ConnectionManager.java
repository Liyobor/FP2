package com.example.fingerprint2;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConnectionManager extends Thread{



    public static void pullRecord(DataHandler dataHandler)
    {
        String driver = "com.mysql.jdbc.Driver";
//localhost指本機，也可以用本地ip地址代替，3306為MySQL資料庫的預設埠號，“user”為要連線的資料庫名
        String url = "jdbc:mysql://140.136.151.70:3306/app";
//填入資料庫的使用者名稱跟密碼
        String username = "root";
        String password = "xcBzOSB1cA";
        String sql = "select * from file_info";//編寫要執行的sql語句，此處為從user表中查詢所有使用者的資訊
        try
        {
            Class.forName(driver);//載入驅動程式，此處運用隱式註冊驅動程式的方法
        }
        catch(ClassNotFoundException e)
        {
            System.out.println("載入驅動程式錯誤");
            e.printStackTrace();
        }
        try
        {
            Connection con = DriverManager.getConnection(url,username,password);//建立連線物件
            Statement st = con.createStatement();//建立sql執行物件
            ResultSet rs = st.executeQuery(sql);//執行sql語句並返回結果集
            while(rs.next())//對結果集進行遍歷輸出
            {
                String caseOID;
                String filePath;
                String account;
                int index = 0;

                caseOID = String.format("%s",rs.getString("caseOID"));
                account = String.format("%s",rs.getString("account"));
                filePath = String.format("%s",rs.getString("filePath"));
                String fileName = filePath.substring(filePath.indexOf("file")+5,filePath.length()-4);


                dataHandler.info.add(index,(fileName));
                dataHandler.caseOID = Integer.parseInt(caseOID);
                dataHandler.account = Integer.parseInt(account);



            }
//                關閉物件
            rs.close();
            st.close();
            con.close();
        }
        catch(SQLException e)
        {
            System.out.println("連線錯誤");
            e.printStackTrace();
        }
    }





    private static HttpURLConnection get_connection;

    public static InputStream Get_HttpURLConnection() throws IOException {

        String url = "http://140.136.151.70/Project/Download/download.php";

        try {
            // We retrieve the contents of our webpage.
            URL myurl = new URL(url);
            get_connection = (HttpURLConnection) myurl.openConnection();
            // Here we specify the connection type
            get_connection.setRequestMethod("GET");
            StringBuilder webpage_content;

            try (BufferedReader webpage = new BufferedReader(
                    new InputStreamReader(get_connection.getInputStream()))) {

                String webpage_line;
                webpage_content = new StringBuilder();

                while ((webpage_line = webpage.readLine()) != null) {

                    webpage_content.append(webpage_line);
                    webpage_content.append(System.lineSeparator());
                }
            }



            return new ByteArrayInputStream(webpage_content.toString().getBytes(StandardCharsets.UTF_8));



        } finally {
            //Disconnect
            get_connection.disconnect();

        }
    }



}
