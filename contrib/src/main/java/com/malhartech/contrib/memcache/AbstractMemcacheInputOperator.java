/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.malhartech.contrib.memcache;

import com.malhartech.api.ActivationListener;
import com.malhartech.api.Context.OperatorContext;
import com.malhartech.api.InputOperator;
import com.malhartech.util.CircularBuffer;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Map.Entry;
import net.spy.memcached.AddrUtil;
import net.spy.memcached.MemcachedClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Memcache input adapter operator, which get data from Memcached using spymemcached library<p><br>
 *
 * <br>
 * Ports:<br>
 * <b>Input</b>: No input port<br>
 * <b>Output</b>: Can have any number of output ports<br>
 * <br>
 * Properties:<br>
 * <b>tuple_blast</b>: Number of tuples emitted in each burst<br>
 * <b>bufferSize</b>: Size of holding buffer<br>
 * <b>servers</b>: the kestrel server url list<br>
 * <b>keys</b>: the queueName to interact with kestrel server<br>
 * <br>
 * Compile time checks:<br>
 * Class derived from this has to implement the abstract method emitTuple() <br>
 * <br>
 * Run time checks:<br>
 * None<br>
 * <br>
 * <b>Benchmarks</b>: TBD
 * <br>
 *
 * @author Zhongjian Wang <zhongjian@malhar-inc.com>
 */
public abstract class AbstractMemcacheInputOperator implements InputOperator, ActivationListener<OperatorContext>
{
  private static final Logger logger = LoggerFactory.getLogger(AbstractMemcacheInputOperator.class);
  private static final int DEFAULT_BLAST_SIZE = 1000;
  private static final int DEFAULT_BUFFER_SIZE = 1024 * 1024;
  private int tuple_blast = DEFAULT_BLAST_SIZE;
  private int bufferSize = DEFAULT_BUFFER_SIZE;
  transient CircularBuffer<Entry<String,Object>> holdingBuffer;
  protected transient MemcachedClient client;
  ArrayList<String> servers = new ArrayList<String>();
  ArrayList<String> keys = new ArrayList<String>();
  protected transient GetDataThread gdt;

  public abstract void emitTuple(Entry<String,Object> e);

  public void addServer(String server) {
    servers.add(server);
  }

  public void addKey(String key) {
    keys.add(key);
  }

/**
 * a thread actively pulling data from Memcached
 * and added to the holdingBuffer
 */
  private class GetDataThread extends Thread
  {
    @Override
    public void run()
    {
      Map<String, Object> map = client.getBulk(keys);
      for( Entry<String, Object> e : map.entrySet() ) {
        holdingBuffer.add(e);
      }
    }
  }

  @Override
  public void emitTuples()
  {
    int ntuples = tuple_blast;
    if (ntuples > holdingBuffer.size()) {
      ntuples = holdingBuffer.size();
    }
    for (int i = ntuples; i-- > 0;) {
      emitTuple(holdingBuffer.pollUnsafe());
    }
  }

  @Override
  public void beginWindow(long windowId)
  {
  }

  @Override
  public void endWindow()
  {
  }

  @Override
  public void setup(OperatorContext context)
  {
    holdingBuffer = new CircularBuffer<Entry<String,Object>>(bufferSize);
    try {
      client = new MemcachedClient(AddrUtil.getAddresses(servers));
    }
    catch (IOException ex) {
      logger.info(ex.toString());
    }
  }

  @Override
  public void teardown()
  {
  }

  @Override
  public void activate(OperatorContext ctx)
  {
    gdt = new GetDataThread();
    gdt.start();
  }

  @Override
  public void deactivate()
  {
  }

  public void setTupleBlast(int i)
  {
    this.tuple_blast = i;
  }
}
