package com.utils;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 反射工具
 *
 * @author jianshengd
 * @date 2018/5/2
 */
public class ReflectUtils {
    /**
     * 获取变量值
     *
     * @param obj       类对象
     * @param fieldName 变量名
     * @return 变量值
     * @throws NoSuchFieldException   变量名错误
     * @throws IllegalAccessException 不可访问
     */
    public static Object getFieldValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
    }

    /**
     * 获取int变量值
     *
     * @param obj       类对象
     * @param fieldName 变量名
     * @return 变量值
     * @throws NoSuchFieldException   变量名错误
     * @throws IllegalAccessException 不可访问
     */
    public static int getFieldIntValue(Object obj, String fieldName) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.getInt(obj);
    }

    /**
     * 设置变量值
     *
     * @param obj       类对象
     * @param fieldName 变量名
     * @throws NoSuchFieldException   变量名错误
     * @throws IllegalAccessException 不可访问
     */
    public static void setFieldValue(Object obj, String fieldName, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }

    /***
     * 设置字段值
     * @param obj  实例对象
     * @param methodName       调用方法名
     * @param parameterTypes   参数类型
     * @param values           参数值
     * @return 方法返回值
     */

    public static Object methodInvoke(Object obj, String methodName, Class[] parameterTypes, Object[] values) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method;
        if (parameterTypes == null) {
            method = obj.getClass().getDeclaredMethod(methodName);
        } else {
            method = obj.getClass().getDeclaredMethod(methodName, parameterTypes);
        }
        method.setAccessible(true);
        if (values == null) {
            return method.invoke(obj);
        } else {
            return method.invoke(obj, values);
        }
    }
}
