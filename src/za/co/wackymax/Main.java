package za.co.wackymax;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class Main {

    private int availableLiquidTypes;
    private final Map<Integer, Integer> liquidQuantities = new HashMap<>();
    private List<Jar> jars = new ArrayList<>();

    public static void main(String[] args) {

        Main main = new Main();
        main.runSolution();
    }

    private void runSolution(){

        try {

            Path inputPath = Paths.get("W:\\Projects\\Personal\\CodeOff\\26Feb2016\\code_off-2.in");
            List<String> fileLines = Files.readAllLines(inputPath, Charset.defaultCharset());

            availableLiquidTypes = Integer.parseInt(fileLines.get(0));

            for (int i = 1; i <= availableLiquidTypes; i++) {

                liquidQuantities.put(i - 1, Integer.parseInt(fileLines.get(i)));
            }

            for (int i = 1 + availableLiquidTypes; i < fileLines.size(); i++) {

                jars.add(createJar(fileLines.get(i)));
            }

            System.out.println("Available liquids " + availableLiquidTypes);
            System.out.println("Jars liquids " + jars.size());

            fillJarsWithLiquid();

            System.out.println("All Jars filled");

            StringBuilder sb = new StringBuilder();

            int totalRemainderLiquid = 0;
            for (Integer integer : liquidQuantities.values()) {
                totalRemainderLiquid += integer;
            }

            sb.append(totalRemainderLiquid).append(" //The total remainder liquid\n");

            for (int i = 0; i < jars.size(); i++) {

                Jar jar = jars.get(i);
                sb.append(jar.filledLiquid == null ? "" : jar.filledLiquid)
                        .append(",")
                        .append(jar.filledQuantity)
                        .append(" //Jar " + i + " contains " + jar.filledQuantity + " litres of liquid " + (jar.filledLiquid == null ? "Magic" : jar.filledLiquid) + "\n");
            }

            Files.write(Paths.get("W:\\Projects\\Personal\\CodeOff\\26Feb2016\\code_off-2.out"), sb.toString().getBytes());

        }
        catch (Exception ex){
            ex.printStackTrace();
            System.out.println("Program failed");
        }
    }

    private Jar createJar(String jarFileLine){

        String[] jarDetails = jarFileLine.split(",");

        int jarSize = Integer.parseInt(jarDetails[0]);
        List<Integer> jarLiquids = new ArrayList<>();

        for (int i = 1; i < jarDetails.length; i++) {

            jarLiquids.add(Integer.parseInt(jarDetails[i]));
        }

        return new Jar(jarSize, jarLiquids.toArray(new Integer[]{}));
    }

    private void fillJarsWithLiquid(){

        for (Integer liquidTypeId : liquidQuantities.keySet()) {

            Integer liquidQuantity = liquidQuantities.get(liquidTypeId);
            List<Jar> availableJars = findAllJarsForLiquidType(liquidTypeId);

            while(liquidQuantity > 0){

                Jar nextCandidateJar = findNextCandidateJar(availableJars, liquidQuantity);

                if(nextCandidateJar == null)
                    break;

                nextCandidateJar.filledLiquid = liquidTypeId;

                if(liquidQuantity - nextCandidateJar.jarSize > 0){
                    nextCandidateJar.filledQuantity = nextCandidateJar.jarSize;
                    liquidQuantity -= nextCandidateJar.filledQuantity;
                }
                else{
                    nextCandidateJar.filledQuantity = liquidQuantity;
                    liquidQuantity = 0;
                }

            }
        }
    }

    private List<Jar> findAllJarsForLiquidType(Integer liquidType){

        List<Jar> matchingJars = new ArrayList<>();
        for (Jar jar : jars) {

            int jarIndex = Arrays.binarySearch(jar.liquidTypes, liquidType);
            if(jarIndex >= 0 && jars.get(jarIndex).filledLiquid == null)
                matchingJars.add(jar);
        }

        return matchingJars;
    }

    private Jar findNextCandidateJar(List<Jar> compatibleJars, int requiredLiquid){

        if(compatibleJars.size() == 0)
            return null;

        for (Jar compatibleJar : compatibleJars) {

            if(compatibleJar.liquidTypes.length == 0 && compatibleJar.jarSize > 0)
                return compatibleJar;
        }

        List<Jar> sortedJars = new ArrayList<>(compatibleJars);
        Collections.copy(sortedJars, compatibleJars);

        Jar closestMatch = null;
        int closestMatchValue = Integer.MAX_VALUE;
        for (Jar sortedJar : sortedJars) {

            if(sortedJar.jarSize < 1)
                continue;

            if(Math.abs(sortedJar.jarSize - requiredLiquid) < closestMatchValue){

                closestMatchValue = Math.abs(sortedJar.jarSize - requiredLiquid);
                closestMatch = sortedJar;
            }
        }

        return closestMatch;
    }

    private static class Jar implements Comparable<Jar> {

        private final int jarSize;
        private Integer[] liquidTypes;

        private Integer filledLiquid;
        private int filledQuantity;

        private Jar(int jarSize, Integer[] liquidTypes) {
            this.jarSize = jarSize;
            this.liquidTypes = liquidTypes;

            Arrays.sort(liquidTypes);
        }

        @Override
        public int compareTo(Jar o) {
            return Integer.compare(jarSize, o.jarSize);
        }
    }
}
