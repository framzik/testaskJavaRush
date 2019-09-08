package com.space.model;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Date;

public class ConsoleTest {

    private static ResultSet rs;

    public  static void main(String args[]) throws Exception {
    String user = "root";
    String pass = "root";
    String url = "jdbc:mysql://localhost:3306/cosmoport?serverTimezone=UTC";
    Class.forName("com.mysql.cj.jdbc.Driver");

    String query = "seleqt count(*) from ship";
    try(Connection con = DriverManager.getConnection(url,user,pass);
        Statement stm  = con.createStatement()){
        System.out.println("We are connected!");
        ResultSet resultSet = stm.executeQuery("select * from ship");
        while  (resultSet.next()){
            System.out.println(resultSet.getInt(1)+" "+resultSet.getString(2)+" " +resultSet.getString(3)+
                    " " + resultSet.getString(4) +" " +resultSet.getDate(5)
            );

        }
    }
    }
}