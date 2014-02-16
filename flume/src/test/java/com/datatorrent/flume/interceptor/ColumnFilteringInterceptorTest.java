/*
 * Copyright (c) 2013 DataTorrent, Inc.
 * ALL Rights Reserved.
 */
package com.datatorrent.flume.interceptor;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;

import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Chetan Narsude <chetan@datatorrent.com>
 */
public class ColumnFilteringInterceptorTest
{
  private static InterceptorTestHelper helper;

  @BeforeClass
  public static void startUp()
  {
    HashMap<String, String> contextMap = new HashMap<String, String>();
    contextMap.put(ColumnFilteringInterceptor.Constants.DST_SEPARATOR, Byte.toString((byte) 1));
    contextMap.put(ColumnFilteringInterceptor.Constants.SRC_SEPARATOR, Byte.toString((byte) 2));
    contextMap.put(ColumnFilteringInterceptor.Constants.COLUMNS, "1 2 3");

    helper = new InterceptorTestHelper(new ColumnFilteringInterceptor.Builder(), contextMap);
  }

  @Test
  public void testInterceptEvent()
  {
    helper.testIntercept_Event();
  }

  @Test
  public void testFiles() throws IOException, URISyntaxException
  {
    helper.testFiles();
  }
}
