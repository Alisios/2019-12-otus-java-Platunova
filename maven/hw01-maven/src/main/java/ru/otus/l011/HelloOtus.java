package ru.otus.l011;

import com.google.common.collect.Lists;
import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import com.google.common.collect.Multisets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * To start the application:
 * mvn package
 * java -jar ./L01-maven/target/L01-maven-jar-with-dependencies.jar
 * java -cp "./L01-maven/target/L01-maven.jar:${HOME}/.m2/repository/com/google/guava/guava/27.1-jre/guava-27.1-jre.jar" ru.otus.l011.Main
 * <p>
 * To unzip the jar:
 * unzip -l L01-maven.jar
 * unzip -l L01-maven-jar-with-dependencies.jar
 * <p>
 * To build:
 * mvn package
 * mvn clean compile
 * mvn assembly:single
 * mvn clean compile assembly:single
 */
public class HelloOtus {
    private static final int MEASURE_COUNT = 1;

    public static void main(String... args) {

        Multiset<String> wordsMultiset = HashMultiset.create();

        wordsMultiset.add("Java");
        wordsMultiset.add("is");
        wordsMultiset.add("really,");
        wordsMultiset.add("really,");
        wordsMultiset.add("really,");
        wordsMultiset.add("cool");
        System.out.println(wordsMultiset);

        for (String word : Multisets.copyHighestCountFirst(wordsMultiset).elementSet())
            System.out.println("Occurrences of " + word + " : " + wordsMultiset.count(word));

        List<Integer> example = new ArrayList<>();
        int min = 0;
        int max = 999;

        for (int i = min; i < max + 1; i++) {
            example.add(i);
        }

        List<Integer> result = new ArrayList<>();
        Collections.shuffle(example);
        calcTime(() -> result.addAll(Lists.reverse(example)));
    }

    private static void calcTime(Runnable runnable) {
        long startTime = System.nanoTime();
        for (int i = 0; i < MEASURE_COUNT; i++) {
            runnable.run();
        }
        long finishTime = System.nanoTime();
        long timeNs = (finishTime - startTime) / MEASURE_COUNT;
        System.out.println("\nTime spent: " + timeNs + "ns (" + timeNs / 1_000_000 + "ms)");
    }
}
