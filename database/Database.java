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
import com.etiennelndr.projetias.bot_pogamut.BotProjetIAS.BotDatas;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * @author Etienne
 */
public class Database {
    
    //private final static String URL_MYSQL       = "jdbc:mysql://localhost:3306/projetias?serverTimezone=UTC";
    //private final static String URL_SQLITE      = "jdbc:sqlite:D:/Documents/NetBeansProjects/projetias/src/main/java/com/etiennelndr/projetias/bot_pogamut/projetias.db";
    private final static String URL_SQLITE      = "jdbc:sqlite:C:/Users/matth/Documents/NetBeansProjects/projetias/src/main/java/com/etiennelndr/projetias/bot_pogamut/projetias.db";
    //private final static String LOGIN           = "user_projetias";
    //private final static String PASSWORD        = "1234abcd";
    private final static String TABLE           = "bot_states";
    
    private Connection con;
    
    private final static String QUERY_RESET                = "DELETE FROM " + TABLE;
    private final static String QUERY_RESET_AUTO_INCREMENT = "UPDATE sqlite_sequence SET seq = 0 WHERE name = '" + TABLE + "'";
    
    public Database() {
        try {
            //this.con = DriverManager.getConnection(URL_MYSQL, LOGIN, PASSWORD);
            this.con = DriverManager.getConnection(URL_SQLITE);
        } catch(SQLException e) {
            System.out.println("ERROR : " + e.getMessage());
            System.exit(-1);
        }
    }
    
    @SuppressWarnings("LockAcquiredButNotSafelyReleased")
    public void resetDatabase() {
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
    }
    
    
    @SuppressWarnings("LockAcquiredButNotSafelyReleased")
    public void insertInDatabase(BotProjetIAS bot) {      
        // Create a query to insert datas in a new row of the bot_state table
        String query;
        if (bot.getEnemy() == null)
            query = "INSERT INTO " + TABLE + " (state, life, id_bot, enemy_killed, date) VALUES (?, ?, ?, ?, date('now'))";
        else 
            query = "INSERT INTO " + TABLE + " (state, life, id_bot, enemy_killed, date, id_enemy, enemy_life) VALUES (?, ?, ?, ?, date('now'), ?, ?)";
        
        try {
            // Create a new statement
            PreparedStatement preparedStmt = this.con.prepareStatement(query);
            
            int stmtIndex = 0;
            
            // Insert data values
            // State
            preparedStmt.setString(++stmtIndex, bot.getCurrentState().STATE);
            // Life
            preparedStmt.setInt(++stmtIndex, bot.getInfo().getHealth());
            // Id of the bot
            preparedStmt.setInt(++stmtIndex, bot.getIdBot());
            // Number of enemy our bot has killed
            preparedStmt.setInt(++stmtIndex, bot.getFrags());
            
            if (bot.getEnemy() != null) {
                BotProjetIAS enemyBot = BotDatas.bots.get(bot.getEnemy().getName().split(" ")[0]);
                
                // Id enemy
                preparedStmt.setInt(++stmtIndex, enemyBot.getIdBot());

                // Enemy life
                preparedStmt.setInt(++stmtIndex, enemyBot.getInfo().getHealth());
            }
            
            // Execute the statement
            preparedStmt.execute();
            
            // Close the statement
            preparedStmt.close();
        } catch(SQLException e) {
            System.out.println("ERROR : " + e.getMessage());
            System.exit(-1);
        }
    }
}
