package june1.open.common.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class It {

    private static ServletRequestAttributes get() {
        return (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
    }

    public static HttpServletRequest getRequest() {
        return get().getRequest();
    }

    public static HttpServletResponse getResponse() {
        return get().getResponse();
    }

    public static HttpSession getSession() {
        return getRequest().getSession(true);
    }

    public static void clearSession() {
        getRequest().getSession().invalidate();
    }

    public static void setIt(String key, Object value) {
        getRequest().setAttribute(key, value);
    }

    public static Object getIt(String key) {
        return getRequest().getAttribute(key);
    }

    public static void setItInSession(String key, Object value) {
        getSession().setAttribute(key, value);
    }

    public static Object getItInSession(String key) {
        return getSession().getAttribute(key);
    }
}
