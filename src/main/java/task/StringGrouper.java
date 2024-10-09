package task;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class StringGrouper {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("Usage: java -jar StringGrouper.jar lng.txt");
            return;
        }

        String inputFile = args[0];

        try {
            long startTime = System.currentTimeMillis();
            Set<String> uniqueLines = readFile(inputFile);
            List<Set<String>> groups = groupLines(uniqueLines);
            writeGroupsToFile(groups);
            long endTime = System.currentTimeMillis();

            System.out.println("Количество групп с более чем одним элементом: " + countGroupsWithMoreThanOneElement(groups));
            System.out.println("Время выполнения: " + (endTime - startTime) + " мс");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Set<String> readFile(String inputFile) throws IOException {
        Set<String> uniqueLines = new HashSet<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new URL(inputFile).openStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isValidLine(line)) {
                    uniqueLines.add(line);
                }
            }
        }
        return uniqueLines;
    }

    private static boolean isValidLine(String line) {
        return line.matches("^(?!.*\".*\".*)(.*;)*.*$"); // Условие для фильтрации некорректных строк
    }

    private static List<Set<String>> groupLines(Set<String> lines) {
        Map<Integer, Set<String>> columnValuesMap = new HashMap<>();
        Map<String, Set<String>> groupsMap = new HashMap<>();

        for (String line : lines) {
            String[] values = line.split(";");

            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                if (!value.isEmpty()) {
                    columnValuesMap.putIfAbsent(i, new HashSet<>());
                    columnValuesMap.get(i).add(value);
                }
            }

            for (int i = 0; i < values.length; i++) {
                String value = values[i].trim();
                if (!value.isEmpty()) {
                    groupsMap.putIfAbsent(value, new HashSet<>());
                    groupsMap.get(value).add(line);
                }
            }
        }

        List<Set<String>> groups = new ArrayList<>(groupsMap.values());
        return groups;
    }

    private static void writeGroupsToFile(List<Set<String>> groups) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            writer.write("Количество групп: " + countGroupsWithMoreThanOneElement(groups) + "\n");
            int groupNumber = 1;

            for (Set<String> group : groups) {
                if (group.size() > 1) {
                    writer.write("Группа " + groupNumber++ + "\n");
                    for (String line : group) {
                        writer.write(line + "\n");
                    }
                }
            }
        }
    }

    private static int countGroupsWithMoreThanOneElement(List<Set<String>> groups) {
        int count = 0;
        for (Set<String> group : groups) {
            if (group.size() > 1) {
                count++;
            }
        }
        return count;
    }
}
