package ru.otus.generics;

import java.util.*;

public class Generics {

    public static void main(String... args) {

        DIYarrayList<Integer> example_DIY_array = new DIYarrayList<>();

        example_DIY_array.add(444);
        example_DIY_array.add(555);
        example_DIY_array.add(777);

        DIYarrayList<Integer> example_DIY_array2 = new DIYarrayList<>();
        example_DIY_array2.add(444);
        example_DIY_array2.add(55566);
        example_DIY_array2.add(99);
        Collections.copy(example_DIY_array2, example_DIY_array);
        example_DIY_array.add(1, 100000);
        Collections.addAll(example_DIY_array, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21);
        Collections.sort(example_DIY_array);

        for (Integer element : example_DIY_array) {
            System.out.print(element + " ");
        }
        System.out.println();
        for (Integer element : example_DIY_array2) {
            System.out.print(element + " ");
        }
    }
}
