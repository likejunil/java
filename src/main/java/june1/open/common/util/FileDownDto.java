package june1.open.common.util;

import lombok.Builder;
import lombok.Getter;
import org.springframework.core.io.UrlResource;
import org.springframework.web.util.UriUtils;

import java.nio.charset.StandardCharsets;

@Getter
@Builder
public class FileDownDto {

    private String name;
    private UrlResource resource;

    public String getContentDisposition() {
        return "attachment; filename=\"" + UriUtils.encode(name, StandardCharsets.UTF_8) + "\"";
    }
}
