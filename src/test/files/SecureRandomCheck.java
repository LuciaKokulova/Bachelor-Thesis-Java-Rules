import java.util.Random;

class MyClass {
    MyClass(MyClass mc) {
    }

    // test cases
    int foo1() {
        return 0;
    }

    void foo2(int value) {
    }

    int foo3(int value) {
        return 0;
    }

    Object foo4(int value) {
        return null;
    }


    public function reset(int i) {
        String query;
        try {
            log("Generating 10 random integers in range 0..99.");
            Random random = new Random(); // Noncompliant
            double random2 = Math.random(); // Noncompliant

            //note a single Random object is reused here
            Random randomGenerator = new Random(); // Noncompliant
            for (int idx = 1; idx <= 10; ++idx){
                int randomInt = randomGenerator.nextInt(100);
                log("Generated : " + randomInt);
            }
            int number = Math.random(); // Noncompliant
            log("Done.");
        }

        MessageDigest messageDigest;
        if (i == 0) {
           int random = Math.random(); // Noncompliant
        } else {
            messageDigest = MessageDigest.getInstance("MD1");
        }
    }


}