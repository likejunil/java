package june1.vgen.open.common.util;

import june1.vgen.open.domain.AttachmentFile;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FileDto {

    private String path;
    private String name;
    private String ext;
    private String stored;
    private long size;

    public AttachmentFile toAttachmentFile() {
        return AttachmentFile.builder()
                .path(this.getPath())
                .name(this.getName())
                .ext(this.getExt())
                .size(this.getSize())
                .stored(this.getStored())
                .inUse(true)
                .useCount(0)
                .build();
    }
}
