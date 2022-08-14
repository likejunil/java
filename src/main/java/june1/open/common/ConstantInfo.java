package june1.open.common;

public class ConstantInfo {

    //-----------------------
    // token
    //-----------------------
    public static final String SEQ = "seq";
    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer";

    //-----------------------
    // role
    //-----------------------
    public static final String ROLE_ADMIN = "ROLE_ADMIN";
    public static final String ROLE_MANAGER = "ROLE_MANAGER";
    public static final String ROLE_USER = "ROLE_USER";

    //-----------------------
    // backup
    //-----------------------
    public static final String SUFFIX = "History";
    public static final String REPOSITORY = "Repository";
    public static final String BASE_PACKAGE = "june1.open";
    public static final int CREATE = 100;
    public static final int UPDATE = 200;

    //-----------------------
    // uri
    //-----------------------
    public static final String URI_AUTH = "/auth";
    public static final String URI_LOGIN = "/login";
    public static final String URI_JOIN = "/join";
    public static final String URI_REISSUE = "/reissue";
    public static final String URI_LOGOUT = "/logout";

    //-----------------------
    // code
    //-----------------------
    public static final String CODE_AUTH = "Auth";
    public static final String CODE_MEMBER = "Member";
    public static final String CODE_COMPANY = "Company";
    public static final String CODE_SERVER = "Server";
    public static final String CODE_CLIENT = "Client";
    public static final String CODE_REDIS = "Redis";
    public static final String CODE_DTO = "Dto";
    public static final String CODE_FILE = "File";

    //-----------------------
    // query
    //-----------------------
    public static final int QUERY_SIZE_LIMIT = 100000;

    //-----------------------
    // password
    //-----------------------
    public static final int PASSWORD_MAX_LENGTH = 12;
    public static final int PASSWORD_MIN_LENGTH = 8;

    //-----------------------
    // file
    //-----------------------
    public static final String FILE_ROOT_PATH = "/Users/june1/files";
    public static final String MEMBER_IMAGE_FILE_PATH = "/member/image";

    //-----------------------
    // log
    //-----------------------
    public static final String LOG_KEY = "log";

    //-----------------------
    // http response code
    //-----------------------
    public static final int NEED_REISSUE_TOKEN = 418;
}
