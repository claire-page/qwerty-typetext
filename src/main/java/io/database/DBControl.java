package io.database;

import core.RunData;

import java.util.List;
public class DBControl {

    public static DatabaseInteractor interactor;
    public DBControl (DatabaseInteractor iinteractor){
        interactor = iinteractor;
    }

    public static List<RunData> getEntries(){

        return (interactor.retrieveEntries());
    }

    /**
     * builds SQL query.
     * @param name name selected
     * @param filter selected
     */
    public static String buildQuery(String name, QueryFilter filter, int num) {

        StringBuilder b = new StringBuilder();
        b.append("SELECT * from loggedruns");

        if (name.isEmpty()) {
            b.append("WHERE Name = ");
            String nameStr = ("'" + name + "'");
            b.append(nameStr);
        }

        String orderByClause = filter.getOrderBy();
        b.append(orderByClause);
        String limitStr = ("LIMIT " + num + ";");
        b.append(limitStr);
        return(b.toString());
    }

    public static void setInteractorSQLString(QueryFilter f, int num){
        interactor.setExecutableSQL(buildQuery("", f, num));
    }

    public static void setInteractorSQLString(String name, QueryFilter f, int num){
        interactor.setExecutableSQL(buildQuery(name, f, num));
    }

}



