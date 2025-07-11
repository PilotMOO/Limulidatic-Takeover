package mod.pilot.horseshoe_crab_takeover.data;

public class SimpleProfiler {
    public static SimpleProfiler build(){
        return new SimpleProfiler();
    }
    private SimpleProfiler(){}

    public void start(String profile){
        if (lock) err("Cannot begin an already active profile! Close current profile or create a different profiler to begin a new one! Aborting start...");
        else {
            this.profile = profile;
            lock = true;
            init = System.nanoTime();
        }
    }
    public void close(){
        if (close != -1) err("Cannot close an already closed profile! Reset the current profile and start a new profile before attempting to close");
        close = System.nanoTime();
    }

    public void reset(){
        profile = null;
        init = -1;
        close = -1;
        lock = false;
    }

    public void printAndPop(){
        if (close == -1) close();
        readDetails();
        reset();
    }

    public void readDetails(){
        final String id = "[SIMPLE PROFILER/'" + profile + "'] ";
        final int length = id.length();

        builder.append(id);
        out(builder.append("Printing details for profiler...").toString()); builder.setLength(length);
        out(builder.append("INIT at nano [").append(init).append("], CLOSE at [").append(close).append("]").toString()); builder.setLength(length);
        out(builder.append("Elasped: ").append(close - init).append("NS").toString()); cleanBuilder();
    }
    private final StringBuilder builder = new StringBuilder();
    private void cleanBuilder(){
        builder.setLength(0);
    }

    private String profile;
    private long init = -1;
    private long close = -1;
    private boolean lock;

    private static void out(String s){
        System.out.println(s);
    }
    private static void err(String s){
        System.err.println(s);
    }
}
