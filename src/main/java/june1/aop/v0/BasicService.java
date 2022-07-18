package june1.aop.v0;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class BasicService {

    protected final static String title = "BasicService.save()";
    private final BasicRepository basicRepository;

    public void save(String hello) {
        basicRepository.save(hello);
    }
}
