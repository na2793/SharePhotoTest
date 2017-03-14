package com.study.hancom.sharephototest.util;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MathUtil {
    public List<Integer> getRandomNumberList(List<Integer> usableNumberList, final int totalCount) {
        Collections.sort(usableNumberList);
        List<Integer> randomNumberList = new ArrayList<>();

        int maxIndex = usableNumberList.size() - 1;
        int minNumber = usableNumberList.get(0);
        int usedCount = 0;

        while (totalCount - usedCount > 0) {
            if (usableNumberList.get(maxIndex) <= totalCount) {
                int randomIndex = getRandomMath(maxIndex, 0);
                int randomUsableNum = usableNumberList.get(randomIndex);
                int tempUsedCount = usedCount + randomUsableNum;
                if (totalCount - tempUsedCount >= minNumber || totalCount - tempUsedCount == 0) {
                    randomNumberList.add(randomUsableNum);
                    usedCount += randomUsableNum;
                }
            } else {
                maxIndex--;
            }
        }

        return randomNumberList;
    }

    public int getRandomMath(int max, int min) {
        return (int) (Math.random() * (max - min + 1) + min);
    }
}
