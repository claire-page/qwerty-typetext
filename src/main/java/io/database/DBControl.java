package io.database;

import controllers.Control;
import core.RunData;
import core.RunTracker;

import java.util.List;
public class DBControl {

    public static final int defaultNumberofEntries = 15;

    public static DatabaseInteractor interactor = new DatabaseInteractor(Control::displayDBAlert);;

    public static List<RunData> getEntries(){
        return (interactor.retrieveEntries());
    }

    /**
     * builds SQL query.
     * @param name name selected
     * @param filter selected
     */
    public static String buildQuery(String name, QueryFilter filter) {

        StringBuilder b = new StringBuilder();
        b.append("SELECT * from loggedruns ");

        if (!name.isEmpty()) {
            b.append("WHERE Name LIKE ");
            String nameStr = ("'" + name + "%'");
            b.append(nameStr);
        }

        String orderByClause = filter.getOrderBy();
        b.append(orderByClause);

        String limitStr = ("LIMIT " + defaultNumberofEntries + ";");
        b.append(limitStr);
        return(b.toString());
    }

//    public static void setInteractorSQLString(QueryFilter f, int num){
//        interactor.setExecutableSQL(buildQuery("", f, num));
//    }

    public static void setInteractorSQLString(String name, QueryFilter f){
        interactor.setExecutableSQL(buildQuery(name, f));
    }

    public static boolean isConnectionValid(){
        return(interactor.isConnectionValid());
    }

    public static void passDataToInteractor(RunData r){
        interactor.sendData(r);
    }

}



