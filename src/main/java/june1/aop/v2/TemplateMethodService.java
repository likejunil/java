package june1.aop.v2;

import june1.aop.trace.LogTrace;
import june1.aop.trace.TemplateMethod;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class TemplateMethodService {

    private final static String title = "TemplateMethodService.save()";
    private final TemplateMethodRepository templateMethodRepository;
    private final LogTrace trace;

    public void save(String hello) {
        TemplateMethod<Void> template = new TemplateMethod<>(trace) {
            @Override
            public Void call() {
                templateMethodRepository.save(hello);
                return null;
            }
        };

        template.execute(title);
    }
}
