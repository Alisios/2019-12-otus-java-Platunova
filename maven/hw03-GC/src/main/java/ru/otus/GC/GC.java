package ru.otus.GC;

import com.sun.management.GarbageCollectionNotificationInfo;

import javax.management.MBeanServer;
import javax.management.NotificationEmitter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeData;
import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.ManagementFactory;
import java.util.List;


/*
О формате логов
http://openjdk.java.net/jeps/158

-Xms512m
-Xmx512m
-Xms256m
-Xmx256m
-Xms128m
-Xmx128m
-----------
-Xms512m
-Xmx512m
-Xlog:gc=debug:file=./hw03-GC/logs/GC-%p-%t.log:tags,uptime,time,level:filecount=5,filesize=10m
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./hw03-GC/logs/dump.log
-XX:+UseG1GC
-XX:MaxGCPauseMillis=10

-Xms128m
-Xmx128m
-Xlog:gc=debug:file=./hw03-GC/logs/GC-%p-%t.log:tags,uptime,time,level:filecount=5,filesize=10m
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./hw03-GC/logs/dump.log
-XX:+UseConcMarkSweepGC

-Xms512m
-Xmx512m
-Xlog:gc=debug:file=./hw03-GC/logs/GC-%p-%t.log:tags,uptime,time,level:filecount=5,filesize=10m
-XX:+HeapDumpOnOutOfMemoryError
-XX:HeapDumpPath=./hw03-GC/logs/dump.log
-XX:+UseParallelGC

*/

public class GC {
    public static void main( String... args ) throws Exception {
        System.out.println( "Starting pid: " + ManagementFactory.getRuntimeMXBean().getName() );
        switchOnMonitoring();
        long beginTime = System.currentTimeMillis();

        int size = 5_520_000;
        ///int size = 1_000_000;
        int loopCounter = 800;
        MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
        ObjectName name = new ObjectName( "ru.otus:type=Benchmark" );

        Benchmark mbean = new Benchmark( loopCounter );
        mbs.registerMBean( mbean, name );
        mbean.setSize( size );
        mbean.run();

        System.out.println( "time:" + ( System.currentTimeMillis() - beginTime ) / 1000 );
    }

    private static void switchOnMonitoring() {

        List<GarbageCollectorMXBean> gcbeans = java.lang.management.ManagementFactory.getGarbageCollectorMXBeans();
        for ( GarbageCollectorMXBean gcbean : gcbeans ) {
            System.out.println( "GC name:" + gcbean.getName() );
            NotificationEmitter emitter = (NotificationEmitter) gcbean;
            NotificationListener listener = ( notification, handback ) -> {
                if ( notification.getType().equals( GarbageCollectionNotificationInfo.GARBAGE_COLLECTION_NOTIFICATION ) ) {
                    GarbageCollectionNotificationInfo info = GarbageCollectionNotificationInfo.from( (CompositeData) notification.getUserData() );

                    String gcAction = info.getGcAction();
                    String gcCause = info.getGcCause();
                    long gcCount = gcbean.getCollectionCount();
                    String gcName2 = gcbean.getName();
                    long gc_time = gcbean.getCollectionTime();
                    long startTime = info.getGcInfo().getStartTime();
                    long duration = info.getGcInfo().getDuration();
                    //double count_in_minute = gcCount*60000./(startTime);
                    System.out.println( "start:" + startTime + " Name:" + gcName2  +", Count = "+ gcCount + ", GCTime=" + gc_time+", action:" + gcAction +  " (" + duration + " ms)"+ ", count in min = " + gcCount*60000./(startTime));

                   // System.out.println( "start:" + startTime + " Name:" + gcName2  +", Count = "+ gcCount + ", GCTime=" + gc_time+", action:" + gcAction + ", gcCause:" + gcCause+ "(" + duration + " ms)");
                }
            };
            emitter.addNotificationListener( listener, null, null );
        }
    }

}
