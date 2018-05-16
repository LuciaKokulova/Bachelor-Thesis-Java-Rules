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
        MessageDigest md = MessageDigest.getInstance("SHA-1"); // Noncompliant
        return 0;
    }

    Object foo4(int value) {
        return null;
    }


    public function reset(int i) {
        MessageDigest md = MessageDigest.getInstance("SHA-256");

        try {
            md.update(toChapter1);
            MessageDigest tc1 = md.clone();
            byte[] toChapter1Digest = tc1.digest();
            md.update(toChapter2);
        } catch (CloneNotSupportedException cnse) {
            throw new DigestException("couldn't make digest of partial content");
        }

        MessageDigest messageDigest;
        if (i == 0) {
            messageDigest = MessageDigest.getInstance("MD2"); // Noncompliant
        } else {
            messageDigest = MessageDigest.getInstance("MD1");
        }

        if (i == 9) {
            messageDigest = MessageDigest.getInstance("MD5"); // Noncompliant
        }
    }


}