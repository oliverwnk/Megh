/*
 *  Copyright (c) 2012-2013 Malhar, Inc.
 *  All Rights Reserved.
 */
package com.datatorrent.flume.storage;

/**
 * 
 * @author Gaurav Gupta  <gaurav@datatorrent.com>
 */
public interface Storage
{
  /**
   * This stores the bytes and returns the unique identifier to retrieve these bytes
   * 
   * @param bytes
   * @return
   */
  byte[] store(byte[] bytes);

  /**
   * This returns the data bytes for the current identifier and the identifier for next data bytes. <br/>
   * The first eight bytes contain the identifier and the remaining bytes contain the data 
   * 
   * @param identifier
   * @return
   */
  byte[] retrieve(byte[] identifier);

  /**
   * This returns data bytes and the identifier for the next data bytes. The identifier for current data bytes is based
   * on the retrieve method call and number of retrieveNext method calls after retrieve method call. <br/>
   * The first eight bytes contain the identifier and the remaining bytes contain the data
   * 
   * @return
   */
  byte[] retrieveNext();

  /**
   * This is used to clean up the files identified by identifier
   * 
   * @param identifier
   */
  void clean(byte[] identifier);

  /**
   * This flushes the data from stream
   * 
   */
  void flush();

  /**
   * This flushes the data and closes stream
   * 
   */
  void close();

}
