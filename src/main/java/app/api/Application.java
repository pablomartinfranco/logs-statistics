package app.api;

public interface Application {
    static void run(Context ctx) {
        long startTime = System.currentTimeMillis();
        ctx.runner().analyze(ctx.folder());
        System.out.printf("Total elapsed time %s ms%n",
                System.currentTimeMillis() - startTime);
    }
}
