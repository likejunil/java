package june1.aop.proxy.basic;

public class ProxyPatternClient {

    private Subject subject;

    public ProxyPatternClient(Subject subject) {
        this.subject = subject;
    }

    public String execute() {
        return subject.operation();
    }
}
