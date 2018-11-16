/*
 * Copyright (C) 2018 AMIS research group, Faculty of Mathematics and Physics, Charles University in Prague, Czech Republic
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.etiennelndr.projetias.bot_pogamut.database;

import com.etiennelndr.projetias.bot_pogamut.BotProjetIAS;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Calendar;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Etienne
 */
public class Database {
    
    private final static String URL      = "jdbc:mysql://localhost:3306/projetias?serverTimezone=UTC";
    private final static String LOGIN    = "user_projetias";
    private final static String PASSWORD = "1234abcd";
    private final static String TABLE    = "bot_states";
    
    public static Lock locker;
    
    private Connection con;
    
    private final static String QUERY_RESET = "DELETE FROM " + TABLE;
    
    public Database() {
        try {
            this.con = DriverManager.getConnection(URL, LOGIN, PASSWORD);
        } catch(SQLException e) {
            System.out.println("ERROR : " + e.getMessage());
            System.exit(-1);
        }
        
        locker = new ReentrantLock();
    }
    
    public void resetDatabase() {
        // Lock the code
        locker.lock();
        
        try {
             Statement stmt = this.con.createStatement();
             
             boolean rset = stmt.execute(QUERY_RESET);
        } catch(SQLException e) {
            System.out.println("ERROR : " + e.getMessage());
            System.exit(-1);
        }
        
        // Unlock the code
        locker.unlock();
    }
    
    
    public void insertInDatabase(BotProjetIAS bot) {
        // Lock the code
        locker.lock();
        
        String query = "INSERT INTO " + TABLE + " (state, life, date) VALUES (?, ?, ?)";
        
        try {
            PreparedStatement preparedStmt = this.con.prepareStatement(query);
            
            preparedStmt.setString(1, bot.getCurrentState().TITLE);
            preparedStmt.setInt(2, bot.getInfo().getHealth());
            
            Calendar calendar = Calendar.getInstance();
            java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
            preparedStmt.setDate(3, date);
            
            // Execute the statement
            preparedStmt.execute();
        } catch(SQLException e) {
            System.out.println("ERROR : " + e.getMessage());
        }
        
        // Unlock the code
        locker.unlock();
    }
}
