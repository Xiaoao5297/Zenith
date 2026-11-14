package gnu.trove;

public class Version {

    public Version() {}

    public static void main(String[] astring) {
        System.out.println(getVersion());
    }

    public static String getVersion() {
        String s = Version.class.getPackage().getImplementationVersion();

        return s != null ? "trove4j version " + s : "Sorry no Implementation-Version manifest attribute available";
    }
}
