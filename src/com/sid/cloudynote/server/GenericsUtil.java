package com.sid.cloudynote.server;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class GenericsUtil {

	@SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(Class clazz, int index) {

		Type genType = clazz.getGenericSuperclass();// 得到泛型父类

		// 如果没有实现ParameterizedType接口，即不支持泛型，直接返回Object.class

		if (!(genType instanceof ParameterizedType)) {

			return Object.class;

		}

		// 返回表示此类型实际类型参数的Type对象的数组,数组里放的都是对应类型的Class, 如BuyerServiceBean
		// extendsDaoSupport<Buyer,Contact>就返回Buyer和Contact类型

		Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

		if (index >= params.length || index < 0) {

			throw new RuntimeException("你输入的索引"
					+ (index < 0 ? "不能小于0" : "超出了参数的总数"));

		}

		if (!(params[index] instanceof Class)) {

			return Object.class;

		}

		return (Class) params[index];

	}

	@SuppressWarnings("unchecked")
	public static Class getSuperClassGenricType(Class clazz) {

		return getSuperClassGenricType(clazz, 0);

	}

	@SuppressWarnings("unchecked")
	public static Class getMethodGenericReturnType(Method method, int index) {

		Type returnType = method.getGenericReturnType();

		if (returnType instanceof ParameterizedType) {

			ParameterizedType type = (ParameterizedType) returnType;

			Type[] typeArguments = type.getActualTypeArguments();

			if (index >= typeArguments.length || index < 0) {

				throw new RuntimeException("你输入的索引"
						+ (index < 0 ? "不能小于0" : "超出了参数的总数"));

			}

			return (Class) typeArguments[index];

		}

		return Object.class;

	}

	// 其他常用方法略

}
