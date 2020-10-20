package ru.otus.JSON;

import java.io.Serializable;
import java.util.*;

/*Класс для демонстрации работы самодельного DIYgson()*/
public class HWObject implements Serializable {

    private int valueInt = 145678;
    private Integer valueInteger = 190000;
    private String valueString = "строчечка";
    private ArrayList<String> arrayListStr = new ArrayList<>(Arrays.asList("100", "70", "10", "5", "0", "20"));
    private Set<Integer> hashSet = new HashSet<>(Arrays.asList(123, 45, 3, 4, 5, 6, 678));
    private TreeMap<String, ArrayList<Integer>> treeMap = new TreeMap<>();
    private Double[] arrayDouble = {123.5, 456.6, 789.6};
    private Integer[] arrayInteger = {1, 2, 3};
    private int[] arrayInt = {4, 5, 6};

    @Override
    public String toString() {
        return "HWObject{" +
                "valueInt=" + valueInt +
                ", valueInteger=" + valueInteger +
                ", valueString='" + valueString + '\'' +
                ", arrayListInt=" + arrayListStr +
                ", hashSet=" + hashSet +
                ", treeMap=" + treeMap +
                ", arrayDouble=" + Arrays.toString(arrayDouble) +
                ", arrayInteger=" + Arrays.toString(arrayInteger) +
                ", arrayInt=" + Arrays.toString(arrayInt) +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HWObject hwObject = (HWObject) o;
        return valueInt == hwObject.valueInt &&
                Objects.equals(valueInteger, hwObject.valueInteger) &&
                Objects.equals(valueString, hwObject.valueString) &&
                Objects.equals(arrayListStr, hwObject.arrayListStr) &&
                Objects.deepEquals(hashSet, hwObject.hashSet) &&
                Objects.deepEquals(treeMap, hwObject.treeMap) &&
                Arrays.deepEquals(arrayDouble, hwObject.arrayDouble) &&
                Arrays.deepEquals(arrayInteger, hwObject.arrayInteger) &&
                Arrays.equals(arrayInt, hwObject.arrayInt);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(valueInt, valueInteger, valueString, arrayListStr, hashSet, treeMap);
        result = 31 * result + Arrays.hashCode(arrayDouble);
        result = 31 * result + Arrays.hashCode(arrayInteger);
        result = 31 * result + Arrays.hashCode(arrayInt);
        return result;
    }

    HWObject() {
        treeMap.put("key1", new ArrayList<>(Arrays.asList(1, 2, 3, 4, 5, 6)));
        treeMap.put("key2", new ArrayList<>(Arrays.asList(7, 8, 9, 10, 11, 12)));
        treeMap.put("key3", new ArrayList<>(Arrays.asList(17, 18, 19, 110, 111, 112)));
    }

    ;

    public int getValueInt() {
        return valueInt;
    }

    public void setValueInt(int valueInt) {
        this.valueInt = valueInt;
    }

    public String getValueString() {
        return valueString;
    }

    public void setValueString(String valueString) {
        this.valueString = valueString;
    }

    public Set<Integer> getHashSet() {
        return hashSet;
    }

    public void setHashSet(Set<Integer> hashSet) {
        this.hashSet = hashSet;
    }

    public Integer getValueInteger() {
        return valueInteger;
    }

    public void setValueInteger(Integer valueInteger) {
        this.valueInteger = valueInteger;
    }

    public ArrayList<String> getArrayListInt() {
        return arrayListStr;
    }

    public void setArrayListInt(ArrayList<String> arrayListInt) {
        this.arrayListStr = arrayListInt;
    }

    public int[] getArrayInt() {
        return arrayInt;
    }

    public void setArrayInt(int[] arrayInt) {
        this.arrayInt = arrayInt;
    }

    public Integer[] getArrayInteger() {
        return arrayInteger;
    }

    public void setArrayInteger(Integer[] arrayInteger) {
        this.arrayInteger = arrayInteger;
    }

    public Double[] getArrayDouble() {
        return arrayDouble;
    }

    public void setArrayDouble(Double[] arrayDouble) {
        this.arrayDouble = arrayDouble;
    }

    public TreeMap<String, ArrayList<Integer>> getTreeMap() {
        return treeMap;
    }

    public void setTreeMap(TreeMap<String, ArrayList<Integer>> treeMap) {
        this.treeMap = treeMap;
    }
}
