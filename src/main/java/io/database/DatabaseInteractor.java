package io.database;

import core.RunData;

import java.sql.*;

import java.util.ArrayList;

import java.util.List;

public class DatabaseInteractor {
   Connection connection;
   Runnable onError;

   private String executableSQL;
    public DatabaseInteractor(Runnable errorDisplay){
        this.onError = errorDisplay;
        this.executableSQL = DBControl.buildQuery("", QueryFilter.noFilters);

        try { this.connection = DriverManager.getConnection(System.getenv("url"),  "qwertyUser", System.getenv("password") );

            } catch (SQLException e) {

                this.connection = null; //setting connection to null, so we can handle it.
        }
    }

    public void sendData(RunData runData){

        try {

            PreparedStatement s = this.connection.prepareStatement(

                    "INSERT INTO loggedruns (time, date, name, BACKTRACKS, KeyStrokes, charcount, wordcount)  VALUES " +
                            "(?, ?, ?, ?, ?, ?, ?);"
            );
            s.setDouble(1, runData.time());
            s.setDate(2, new Date(System.currentTimeMillis()));
            s.setString(3, runData.name());
            s.setInt(4, runData.backTracked());
            s.setInt(5, runData.keyStrokes());
            s.setInt(6, runData.characterCount());
            s.setInt(7, runData.wordCount());

            s.execute();

        } catch (SQLException e){
            throw new RuntimeException(e);
        }
    }

    /**
     * retrieves data from last N entries in the form of an array of arrays containing strings.
     */
    public List<RunData> retrieveEntries(){

        ArrayList<RunData> arraylist = new ArrayList<>();

            try {
                PreparedStatement s = this.connection.prepareStatement(this.executableSQL);
                ResultSet results = s.executeQuery();

                while (results.next()){
                    RunData entry = new RunData(results.getDouble("time"),
                            results.getString("name"),
                            results.getInt("KeyStrokes"),
                            results.getInt("BACKTRACKS"),
                            results.getInt("wordcount"),
                            results.getInt("charcount"));
                    arraylist.add(entry);
                }
                return(arraylist.stream().toList());

            } catch (SQLException e) {
                onError.run();
            }

         return(arraylist.stream().toList());
    }


//to stop from asking to save to the db if connection isn't working.
    public boolean isConnectionValid(){
        try {
            if (!(this.connection ==null) && !this.connection.isClosed()){
                return(true);
            }
        } catch (SQLException e) {
            return(false);
        }
        return(false); //we'd never get here but whatever
    }

    public void setExecutableSQL(String s){
        this.executableSQL = s;
    }
}
