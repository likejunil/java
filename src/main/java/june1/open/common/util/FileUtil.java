package june1.open.common.util;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import static june1.open.common.ConstantInfo.FILE_ROOT_PATH;

@Slf4j
@Component
@RequiredArgsConstructor
public class FileUtil {

    public FileDto saveFile(MultipartFile file, String path) throws IOException {
        //전송하려는 파일이 존재하는지 확인..
        if (file == null || file.isEmpty()) {
            log.error("전송하려는 파일이 존재하지 않습니다.");
            return null;
        }

        //파일을 저장하려는 경로 정보를 확인한다.
        if (!StringUtils.hasText(path)) {
            log.error("파일을 저장하려는 경로 정보가 정확하지 않습니다.");
            return null;
        }

        //파일 이름 정보가 전달되었는지 확인..
        String upload = file.getOriginalFilename();
        if (upload == null) {
            log.error("파일 이름 정보가 누락되었습니다.");
            return null;
        }

        int index = upload.lastIndexOf(".");
        String ext = index == -1 ? "" : upload.substring(index + 1);
        String stored = UUID.randomUUID() + (index == -1 ? "" : "." + ext);
        log.info("업로드이름=[{}], 저장이름=[{}], 경로=[{}], 확장자=[{}], 크기={}",
                upload, stored, path, ext, file.getSize());

        //실제 파일 저장
        saveFileProc(file, path, stored);

        return FileDto.builder()
                .path(path)
                .name(upload)
                .stored(stored)
                .size(file.getSize())
                .ext(ext)
                .build();
    }

    private void saveFileProc(MultipartFile file, String path, String name) throws IOException {
        //파일이 저장될 경로 생성
        String pathName = FILE_ROOT_PATH + "/" + path;
        File filePath = new File(pathName);
        if (!filePath.exists()) {
            if (!filePath.mkdirs()) {
                log.error("[{}] 경로를 생성하지 못했습니다.", pathName);
                return;
            }
        }

        //실제 파일을 저장
        String fulPathFile = pathName + "/" + name;
        file.transferTo(new File(fulPathFile));
    }
}
