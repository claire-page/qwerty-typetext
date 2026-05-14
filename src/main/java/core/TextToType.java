package core;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class TextToType {
    private static final String [] options = {("src/main/java/io/files/shelley.txt"),("src/main/java/io/files/jordan.txt"), ("src/main/java/io/files/text.txt"), ("src/main/java/io/files/bronte.txt"), ("src/main/java/io/files/NGGUU.txt"), ("src/main/java/io/files/thx.txt"), ("src/main/java/io/files/starwars.txt")};

    /**
     *
     * @return a String of text read from a random file.
     */
    //TODO:
    //Create function that generates a random string of text :
    // word count must be fixed no matter what. if I can get A-Z character count to be identical, that would be great too.
    //i want to do this up front instead of potentially making the input delay worse.
    // Maybe along the way I'll add a spinner, hm


    //generate plausible-sounding random text ->
    //this is a markov chain/list/whatever.
    // I want to end up with an array of key-pair value where the key is every distinct word
    //and the value is an array containing every word that was located at indexofkey+1.
    //how does this work for words with multiple occurences?
    //HASHMAP! how did i forget about hashmaps??
    public static String getRandomtxt(){
        Random rand = new Random();
        int random = rand.nextInt(options.length); //number of files we have.
        var chosen = options[random];

        try {
            var corpus = (Files.readString(Paths.get(chosen))).split(" ");
            var model = new HashMap<String, ArrayList<String>>();

            for(int i = 1 ; i < corpus.length; i++){ //will test multiple state lengths for generation but 1 should be fine for now.

                //populating the Markov Chain HashMap. This represents every word in the corpus
                // and the words which immediately follow them in the text.
                String currentword = corpus[i];
                String preceding = String.join(" ", Arrays.copyOfRange(corpus, i-1, i)); //wow i miss python.
                model.computeIfAbsent(preceding.toLowerCase(), k -> new ArrayList<>()).add(currentword);
                //if the preceding word is absent from the HashMap, make a new entry with that key make its value a new list!
                //either way, the current word gets added to the list belonging to that key.
                //super useful function
            }
            System.out.println(model);

            var markovText = new ArrayList<String>();
            int random2 = rand.nextInt(1, corpus.length);

            markovText.add(String.join(" ", Arrays.copyOfRange(corpus, random2-1, random2)));  //append a random starter word.

            for (int j = 0; j <= 30; j++){ //30 words is fine.

                String lastkey = markovText.getLast();

                int rand4 = rand.nextInt(model.get(lastkey.toLowerCase()).size());
                var randomNextWord = model.get(lastkey.toLowerCase()).get(rand4); //getting random following word for the key.
                markovText.add(randomNextWord);
            }

            return(String.join(" ",markovText));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }}
