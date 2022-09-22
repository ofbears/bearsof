package top.bearsof.reggie.common;

/**
 * 基于ThreadLocal封装工具类，用户保存和获取当前登录用户的id
 */
public class BaseContext {
    private static final ThreadLocal<Long> threadLocal = new ThreadLocal<Long>();

    /**
     * 设置线程，传递ID
     * @param id
     */
    public static void setCurrentId(Long id){
        threadLocal.set(id);
    }

    /**
     * 获取线程中，存取的值
     * @return
     */
    public static Long getCurrentId(){
        return threadLocal.get();
    }
}
