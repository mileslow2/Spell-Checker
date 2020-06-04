public class TimeIt {
    public static void main(String[] args)
    {
        long sum = 0;
        int i;
        for(i = 0; i < 500; i ++) {
            long start = System.currentTimeMillis();

            // call function here
            new SpellChecker("dict.txt", "miss.txt", 25196*5).spellCheck("essay.txt");
            
            //when unknown words were reached it printed uknown word
            // ending time
            long end = System.currentTimeMillis();
            sum += end - start;
            
        }
        // starting time
        System.out.println("Average Execution time: " +
                    (sum/i) + "ms");
    }
}
