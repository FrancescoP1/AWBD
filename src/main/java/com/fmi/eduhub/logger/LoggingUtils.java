package com.fmi.eduhub.logger;

import java.util.Arrays;

public final class LoggingUtils {
  private static final String className = LoggingUtils.class.getCanonicalName();

  public static String getEnclosingMethodName() {
    StackTraceElement[] stackTraceElements = LoggingUtils.getFilteredStackTrace();
    try {
      return stackTraceElements[2].getMethodName();
    } catch (ArrayIndexOutOfBoundsException exception) {
      LoggerAspect.logException(
          new Exception("Error getting method name from stack trace!", exception),
          className,
          stackTraceElements[1].getMethodName()
      );
    }
    return null;
  }

  public static StackTraceElement getEnclosingMethodFromStack() {
    StackTraceElement[] stackTraceElements = LoggingUtils.getFilteredStackTrace();
    try {
      return stackTraceElements[2];
    } catch (ArrayIndexOutOfBoundsException exception) {
      LoggerAspect.logException(
          new Exception("Error getting method name from stack trace!", exception),
          className,
          stackTraceElements[1].getMethodName()
      );
    }
    return null;
  }

  public static StackTraceElement[] getFilteredStackTrace() {
    StackTraceElement[] stackTrace = new Throwable().getStackTrace();
    return Arrays.stream(stackTrace)
        .filter(x -> x.getClassName().contains("com.fmi"))
        .filter(x -> !x.getClassName().contains("$"))
        .toArray(StackTraceElement[]::new);
  }
}
