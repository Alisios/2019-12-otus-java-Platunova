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

public class GC {
    public static void main( String... args ) throws Exception {
        System.out.println( "Starting pid: " + ManagementFactory.getRuntimeMXBean().getName() );
        switchOnMonitoring();
        long beginTime = System.currentTimeMillis();

        int size = 25_000;
        int loopCounter = 500_000;
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
                    System.out.println( "start:" + startTime + " Name:" + gcName2  +", Count = "+ gcCount + ", GCTime=" + gc_time+", action:" + gcAction +  " (" + duration + " ms)"+ ", count in min = " + gcCount*60000./(startTime));
                }
            };
            emitter.addNotificationListener( listener, null, null );
        }
    }

}
