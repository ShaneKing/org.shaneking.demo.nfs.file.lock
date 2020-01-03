package org.shaneking.demo.nfs.file.lock;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.text.MessageFormat;

//java -cp org.shaneking.demo.nfs.file.lock-0.10.0.jar org.shaneking.demo.nfs.file.lock.NFSFileLock /dev-nas-cluster/workspace/github/shaneking/org.shaneking.demo.nfs.file.lock/test.lock
public class NFSFileLock {
  public static final int MAX_TRY_TIMES = 10;
  public static final String FMT_FAILED = "Lock file {0} failed, th{1}.";
  public static final String FMT_SUCCESSFULLY = "Lock file {0} successfully, th{1}.";

  public static void main(String[] args) {
    RandomAccessFile randomAccessFile = null;
    FileChannel fileChannel = null;
    FileLock fileLock = null;

    try {
      File lockFile = new File(args[0]);
      if (!lockFile.exists()) {
        lockFile.getParentFile().mkdirs();
        lockFile.createNewFile();
      }
      randomAccessFile = new RandomAccessFile(lockFile, "rw");
      fileChannel = randomAccessFile.getChannel();
      int tryTimes = 0;
      while (fileLock == null && tryTimes < MAX_TRY_TIMES) {
        try {
          fileLock = fileChannel.tryLock();
        } catch (IOException e) {
          e.printStackTrace();
        }
        if (fileLock == null) {
          System.out.println(MessageFormat.format(FMT_FAILED, lockFile.getAbsolutePath(), tryTimes));
          if (!lockFile.exists()) {
            lockFile.getParentFile().mkdirs();
            lockFile.createNewFile();
          }
          tryTimes++;
          Thread.sleep(10000);//10
        } else {
          System.out.println(MessageFormat.format(FMT_SUCCESSFULLY, lockFile.getAbsolutePath(), tryTimes));
        }
      }
      if (fileLock != null) {
        //TODO some logic here
        Thread.sleep(100000);//100, Just to make the test more obvious
      }
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      if (fileLock != null) {
        try {
          fileLock.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (fileChannel != null) {
        try {
          fileChannel.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
      if (randomAccessFile != null) {
        try {
          randomAccessFile.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }
    }
  }
}
