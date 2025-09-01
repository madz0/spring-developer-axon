package com.github.madz0.springdeveloperaxon.domain.util;

public class StringUtils {
  public static boolean isEmpty(String str) {
    return str == null || str.replaceAll("\\s+", "").isEmpty();
  }
}
