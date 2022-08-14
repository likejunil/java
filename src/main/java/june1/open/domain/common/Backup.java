package june1.open.domain.common;

import june1.open.common.annotation.BackupEntity;
import june1.open.common.annotation.BackupRepository;
import june1.open.common.jwt.JwtUserInfo;
import june1.open.common.util.ApplicationContextProvider;
import june1.open.common.util.ReflectionsUtil;
import june1.open.domain.history.History;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.PatternMatchUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static june1.open.common.ConstantInfo.REPOSITORY;
import static june1.open.common.ConstantInfo.SUFFIX;

@Slf4j
@AllArgsConstructor
@SuperBuilder
public abstract class Backup {

    //대상 엔티티가 백업 대상 엔티티인지 판단한다.
    @Transactional
    protected boolean support() {
        String name = this.getClass().getSimpleName();

        //log.info("[{}]은 백업 대상 엔티티가 아닙니다.", name);
        return ReflectionsUtil.getClass(name + SUFFIX, BackupEntity.class) != null &&
                ReflectionsUtil.getClass(name + SUFFIX + REPOSITORY, BackupRepository.class) != null;
    }

    //현재 저장하려는 엔티티를 백업하는 것이 목표다.
    protected void backup(int act) {

        if (!support()) {
            //log.info("백업을 진행하지 않습니다.");
            return;
        }

        Class<?> source = null;
        Class<?> target = null;
        Object instance = null;
        String name = null;

        try {
            //------------------------------------------------------------
            //1.백업하려는 엔티티 클래스를 구한다.(무엇을)
            //------------------------------------------------------------
            source = this.getClass();

            //------------------------------------------------------------
            //2.백업 엔티티 객체를 생성한다.(어디에)
            // - @BackupEntity 애노테이션이 적용된 클래스를 대상으로 찾는다.
            // - 백업대상 엔티티에 History 접미사가 붙은 '클래스 이름'으로 백업 클래스를 찾는다.
            // - 기본 생성자로 백업 엔티티 객체를 생성한다.
            //------------------------------------------------------------
            name = this.getClass().getSimpleName();
            target = ReflectionsUtil.getClass(name + SUFFIX, BackupEntity.class);
            if (target == null) {
                log.error("[{}] 백업을 실패했습니다.(1)", name);
                return;
            }
            instance = target
                    .getConstructor(null)
                    .newInstance();

        } catch (InstantiationException
                | IllegalAccessException
                | InvocationTargetException
                | NoSuchMethodException e) {
            log.error("[{}] 백업을 실패했습니다.(2) [{}]", name, e.getMessage());
            return;
        }

        //------------------------------------------------------------
        //3.백업하려는 필드들을 파악한다.
        //------------------------------------------------------------
        List<Field> fields = new ArrayList<>();
        Class<?> it = source;
        while (it != null) {
            fields.addAll(Arrays.asList(it.getDeclaredFields()));
            it = it.getSuperclass();
        }

        //------------------------------------------------------------
        //4.필드들을 순회하며 백업 객체를 채운다.
        //------------------------------------------------------------
        History history = null;
        String[] except = {"log", "createdTime", "modifiedTime"};
        Long memberSeq = null;
        Long originalSeq = null;
        try {
            for (Field f : fields) {
                if (PatternMatchUtils.simpleMatch(except, f.getName()))
                    continue;

                f.setAccessible(true);
                if (f.getName().equals("id")) {
                    originalSeq = (Long) f.get(this);
                    //회원 생성의 경우 토큰이 없으므로 Authentication 에서 '누가'에 해당하는 정보를 얻을 수 없다.
                    //따라서 회원이 저장된 후 seq 를 얻을 수 있으므로 그 값을 사용한다.
                    //회원 생성이 아닌 경우 다음 과정에서 memberSeq 를 다시 지정한다.
                    memberSeq = (Long) f.get(this);
                } else {
                    f.set(instance, f.get(this));
                }
            }

            //------------------------------------------------------------
            //5.누가 무엇을 어떻게.. 정보를 등록한다.
            //------------------------------------------------------------
            Object principal = SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getPrincipal();
            if (principal instanceof JwtUserInfo) {
                memberSeq = ((JwtUserInfo) principal).getSeq();
            }

            history = History.builder()
                    .action(act)
                    .memberSeq(memberSeq)
                    .originalSeq(originalSeq)
                    .build();

            Field field = target.getDeclaredField("history");
            field.setAccessible(true);
            field.set(instance, history);

            //------------------------------------------------------------
            //6.백업 엔티티를 다루는 리포지터리 객체를 얻는다.
            // - @BackupRepository 애노테이션이 적용된 클래스를 대상으로 찾는다.
            // - 백업대상 엔티티에 HistoryRepository 접미사가 붙은 '클래스 이름'으로 리포지터리 클래스를 찾는다.
            // - 해당 리포지토리에 save() 메서드를 찾는다.
            //------------------------------------------------------------
            Class<?> repository = ReflectionsUtil.getClass(name + SUFFIX + REPOSITORY, BackupRepository.class);
            if (repository == null) {
                log.error("[{}] 백업을 실패했습니다.(3)", name);
                return;
            }

            Method save = Arrays.stream(repository.getMethods())
                    .filter(m -> m.getName().equals("save"))
                    .findAny()
                    .orElseThrow(() -> {
                        log.error("[{}]에서 save() 메소드를 찾을 수 없습니다.",
                                repository.getSimpleName());
                        return new RuntimeException("save() 메소드를 찾을 수 없음..");
                    });

            //------------------------------------------------------------
            //7.백업대상을 백업 엔티티에 저장한다.
            // - 리포지터리 객체를 스프링 컨테이너에서 가져온다.
            // - 불러온 객체로부터 save() 메소드를 호출한다.
            //------------------------------------------------------------
            ApplicationContext ac = ApplicationContextProvider.getApplicationContext();
            save.invoke(ac.getBean(repository), instance);

        } catch (IllegalAccessException e) {
            log.error("잘못된 접근을 시도했습니다.[{}]", e.getMessage());
        } catch (NoSuchFieldException e) {
            log.error("그런 필드는 존재하지 않습니다.[{}]", e.getMessage());
        } catch (InvocationTargetException e) {
            log.error("대상 메소드를 실행하는데 실패했습니다.[{}]", e.getMessage());
        } catch (RuntimeException e) {
            log.error("에러가 발생했습니다 .[{}]", e.getMessage());
        }
    }
}