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
import java.util.logging.Level;
import java.util.logging.Logger;

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
    
    private final static String QUERY_RESET                = "DELETE FROM " + TABLE;
    private final static String QUERY_RESET_AUTO_INCREMENT = "ALTER TABLE " + TABLE +" AUTO_INCREMENT=0";
    
    public Database() {
        try {
            this.con = DriverManager.getConnection(URL, LOGIN, PASSWORD);
        } catch(SQLException e) {
            System.out.println("ERROR : " + e.getMessage());
            System.exit(-1);
        }
        
        locker = new ReentrantLock();
    }
    
    @SuppressWarnings("LockAcquiredButNotSafelyReleased")
    public void resetDatabase() {
        // Lock the code
        locker.lock();
        
        try {
            // Create a new statement 
            Statement stmt = this.con.createStatement();
            
            // Execute a new query with the previous statement
            stmt.execute(QUERY_RESET);
            stmt.execute(QUERY_RESET_AUTO_INCREMENT);
             
            // Close the statement
            stmt.close();
        } catch(SQLException e) {
            System.out.println("ERROR : " + e.getMessage());
            System.exit(-1);
        }
        
        // Unlock the code
        locker.unlock();
    }
    
    
    @SuppressWarnings("LockAcquiredButNotSafelyReleased")
    public void insertInDatabase(BotProjetIAS bot) {
        // Lock the code
        locker.lock();
        
        // Create a query to insert datas in a new row of the bot_state table
        String query = "INSERT INTO " + TABLE + " (state, life, id_bot, date) VALUES (?, ?, ?, NOW())";
        
        try {
            // Create a new statement
            PreparedStatement preparedStmt = this.con.prepareStatement(query);
            
            // Insert data values
            // State
            preparedStmt.setString(1, bot.getCurrentState().STATE);
            // Life
            preparedStmt.setInt(2, bot.getInfo().getHealth());
            // Id of the bot
            preparedStmt.setInt(3, bot.getIdBot());
            // Current date
            Calendar calendar = Calendar.getInstance();
            java.sql.Date date = new java.sql.Date(calendar.getTime().getTime());
            preparedStmt.setDate(4, date);
            
            bot.getEnemy();
            
            // Execute the statement
            preparedStmt.execute();
            
            // Close the statement
            preparedStmt.close();
        } catch(SQLException e) {
            System.out.println("ERROR : " + e.getMessage());
            System.exit(-1);
        }
        
        // Unlock the code
        locker.unlock();
    }
}
