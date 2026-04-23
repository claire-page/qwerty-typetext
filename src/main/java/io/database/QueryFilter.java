package io.database;

public enum QueryFilter {
    //used to order results.
    //these are what we can order results by.

    runTime(" ORDER BY time ASC;"),
    runWPM(" ORDER BY wordcount / time * 60 ASC"),
    runCPM(" ORDER BY wordcount / time * 60 ASC"),
    noFilters(" ORDER BY RUNID DESC");

    private final String orderBy;


     QueryFilter (String orderBy){

         this.orderBy = orderBy;
     }

     public String getOrderBy(){
         return(this.orderBy);
     }
};

