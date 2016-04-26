package all2all

class Run extends Script {
    @Override
    Object run() {
        new Executor().execute(args)
    }

    public static void main(String[] args) {
        new Executor().execute(args)
    }
}