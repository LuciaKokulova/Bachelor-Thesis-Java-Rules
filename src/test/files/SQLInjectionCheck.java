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

            // Noncompliant@+3
            // Noncompliant@+2
            // Noncompliant@+1
            sql = 'SELECT * FROM Users WHERE Name ="' + uName + '" AND Pass ="' + uPass + '"'; // Noncompliant
        } catch (Exception exception) {
            exception.printStackTrace();
        }

        if (i == 0) {
            uName = getRequestString("username");
            uPass = getRequestString("userpassword");

            // Noncompliant@+3
            // Noncompliant@+2
            // Noncompliant@+1
            sql = 'SELECT * FROM Users WHERE Name ="' + uName + '" AND Pass ="' + uPass + '"'; // Noncompliant
        } else {
            txtUserId = getRequestString("UserId");
            // Noncompliant@+1
            txtSQL = "SELECT * FROM Users WHERE UserId = " + txtUserId; // Noncompliant
        }
    }


}