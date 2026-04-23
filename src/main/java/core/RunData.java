package core;


//going to send this to the database...
//controller will make a record from values it can access, and sends it to the db.
//maybe unnecessary?
public record RunData(Double time, String name, int keyStrokes, int backTracked, int wordCount, int characterCount ){

    public double getWordsPerMinute(){
        return(Math.floor(this.wordCount()/this.time()*100)/100)*60;
    }

    public double getCharsPerSecond(){
        return(Math.floor(this.keyStrokes()/this.time()*100)/100);
    }

    public int getFaults(){
        return(this.keyStrokes - this.characterCount - this.backTracked );
    }

};

