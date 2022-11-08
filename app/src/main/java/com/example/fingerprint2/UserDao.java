package com.example.fingerprint2;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDao {
    public boolean login(String username, String password) {

//        String sql = "select * from ra.member where account = ? and password = ?";
        String sql = "select * from app.account_info where account = ? and password = ?";

        Connection con = JDBCUtils.getConn();

        try {
            PreparedStatement pst = con.prepareStatement(sql);

            pst.setString(1, username);
            pst.setString(2, password);

            if (pst.executeQuery().next()) {
                GlobalInformation.account = username;
                return true;

            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } finally {
            JDBCUtils.close(con);
        }

        return false;
    }
    public boolean register(User user){

//        String sqlInjectRa = "insert into ra.member (account,Password,identity,level,UID) VALUES (?,?,?,?,?)";
        String sqlInjectAccountInfo = "insert into app.account_info (account,password,hospital,doctor,publicKey) VALUES (?,?,?,?,?)";



        Connection  con = JDBCUtils.getConn();
//        int UID =100000001;
        String hospital = "fju";
        String doctor = "a";
        String publicKey = "C:\\Program Files\\Ampps\\www\\publicKey\\public.key";

        try {
            PreparedStatement pst= con.prepareStatement(sqlInjectAccountInfo);

            pst.setString(1,user.getUsername());
            pst.setString(2,user.getPassword());
            pst.setString(3,hospital);
            pst.setString(4,doctor);
            pst.setString(5,publicKey);
//            pst.setString(3,"tester");
//            pst.setInt(4,2);
//            pst.setInt(5,UID);
//            pst.setString(3,"tester");
//            pst.setInt(4,2);
//            pst.setInt(5,UID);


            int value = pst.executeUpdate();

            if(value>0){
                return true;
            }


        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }
        return false;
    }

    public User findUser(String username){

        String sql = "select * from ra.member where account = ?";

        Connection  con = JDBCUtils.getConn();
        User user = null;
        try {
            PreparedStatement pst=con.prepareStatement(sql);

            pst.setString(1,username);

            ResultSet rs = pst.executeQuery();

            while (rs.next()){

                String usernamedb = rs.getString(1);
                String password  = rs.getString(2);
                user = new User(usernamedb,password);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            JDBCUtils.close(con);
        }

        return user;
    }
}
