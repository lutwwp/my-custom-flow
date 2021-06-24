

package com.yusei.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.beans.BeanUtils;

public class BeanCastUtil {
  private BeanCastUtil() {
    throw new IllegalStateException("Utility class");
  }

  public static <O, N> N castBean(O originBean, Class<N> nowBeanClazz) {
    if (originBean == null) {
      return null;
    } else {
      N nowBean = null;

      try {
        nowBean = nowBeanClazz.newInstance();
        BeanUtils.copyProperties(originBean, nowBean);
    } catch (InstantiationException var4) {
      var4.printStackTrace();
    } catch (IllegalAccessException var5) {
      var5.printStackTrace();
    }

    return nowBean;
  }
}


  public static <O, N> List<N> castList(List<O> originList, Class<N> nowBeanClazz) {
    if (originList == null) {
      return null;
    } else {
      List<N> nowList = new ArrayList();
      Iterator<O> var3 = originList.iterator();

      while(var3.hasNext()) {
        O originBean = var3.next();
        nowList.add(castBean(originBean, nowBeanClazz));
      }

      return nowList;
    }
  }
}
