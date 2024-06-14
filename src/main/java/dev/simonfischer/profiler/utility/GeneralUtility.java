package dev.simonfischer.profiler.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GeneralUtility {
    public static <T> List<Long> getDifferences(List<T> list1, List<T> list2, Function<T, Long> entityIdentifier) {
        List<Long> entityIdsNew = list1.stream()
                .map(entityIdentifier).toList();

        List<Long> existingEntityIds = list2
                .stream()
                .map(entityIdentifier).toList();

        List<Long> entityToRemove = new ArrayList<>(existingEntityIds);
        entityToRemove.removeAll(entityIdsNew);

        return entityToRemove;
    }
}
