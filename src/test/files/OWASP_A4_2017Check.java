import javax.xml.parsers.SAXParser;

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
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        String FEATURE = null;
        try {
            FEATURE = "http://xml.org/sax/features/external-general-entities";
            dbf.setFeature(FEATURE, true);

            dbf.setFeature("http://xml.org/sax/features/external-parameter-entities", true); // Noncompliant
        } catch (CloneNotSupportedException cnse) {
            throw new DigestException("couldn't make digest of partial content");
        }

        MessageDigest messageDigest;
    }


}