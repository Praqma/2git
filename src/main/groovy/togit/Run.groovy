package togit

class Run extends Script {
    @Override
    Object run() {
        new Executor().execute(args)
    }

    static void main(String[] args) {
        new Executor().execute(args)
    }
}
