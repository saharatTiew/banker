package banker2;

import java.util.ArrayList;
import java.util.Random;

public class Banker {
    private int numProcess;
    private int numResource;
    private int[][] allocation;
    private int[][] max;
    private int[][] need;
    private int[] available;
    private int[] resource;
    private int[] maxResourceEachProcess;
    private boolean[] isVisited;
    private int currentIndex;
    private ArrayList<Integer> safeState;
    private int[] sum;

    public Banker(int numProcess, int numResource) {
        this.numProcess = numProcess;
        this.numResource = numResource;
        max = new int[numProcess][numResource];
        allocation = new int[numProcess][numResource];
        sum = new int[numResource];
        available = new int[numResource];
        need = new int[numProcess][numResource];
        resource = new int[numResource];
        maxResourceEachProcess = new int[numResource];
        safeState = new ArrayList<>();
        isVisited = new boolean[numProcess];
        randomMax(10);
        randomAllo();
        randomResource(5);
        calculateNeed();
        calculateAvail();
        currentIndex = 0;
    }

    public Banker(int numProcess, int numResource, int[][] max,
                  int[][] allocation, int[] resource) {
        this.numProcess = numProcess;
        this.numResource = numResource;
        this.max = max;
        this.allocation = allocation;
        sum = new int[numResource];
        available = new int[numResource];
        need = new int[numProcess][numResource];
        this.resource = resource;
        maxResourceEachProcess = new int[numResource];
        safeState = new ArrayList<>();
        isVisited = new boolean[numProcess];
        calculateSum();
        calculateNeed();
        calculateAvail();
        currentIndex = 0;
    }

    private boolean randomMax(int bound) {
        Random random = new Random();

        for (int i = 0; i < numProcess; ++i) {
            for (int j = 0; j < numResource; ++j) {
                max[i][j] = random.nextInt(bound) + 1;
            }
        }
        return true;
    }


    private boolean randomResource(int maxBound) {
        Random random = new Random();
        for (int i = 0; i < numResource; ++i) {
            resource[i] = random.nextInt(maxBound) + sum[i];
        }
        return true;
    }

    public boolean randomAllo() {
        Random random = new Random();
        int[] tempResource = resource.clone();
        for (int i = 0; i < numProcess; ++i) {
            for (int j = 0; j < numResource; ++j) {
                int tempValue;
                while (true) {
                    tempValue = random.nextInt(max[i][j]) + 1;
                    if (tempValue <= max[i][j]) {
                        break;
                    }
                    tempValue = random.nextInt(tempResource[i]);
                }
                allocation[i][j] = tempValue;
                tempResource[j] -= tempValue;
            }
        }
        calculateSum();
        return true;
    }

    public boolean calculateNeed() {
        for (int i = 0; i < numProcess; ++i) {
            for (int j = 0; j < numResource; ++j) {
                need[i][j] = max[i][j] - allocation[i][j];
            }
        }
        return true;
    }

    public int[] calculateSum() {
        sum = new int[numResource];
        for (int i = 0; i < numProcess; ++i) {
            for (int j = 0; j < numResource; ++j) {
                sum[j] += allocation[i][j];
            }
        }
        return sum;
    }

    public boolean calculateAvail() {
        for (int i = 0; i < numResource; ++i) {
            available[i] = resource[i] - sum[i];
        }
        return true;
    }

    public boolean isSafe() {
        int count = 0;
        while (count < numProcess) {
            if (!findIsVisited(currentIndex, 0)) {
                boolean isUnsafe = false;
                isVisited[currentIndex] = true;
                for (int j = 0; j < numResource; ++j) {
                    if (available[j] < need[currentIndex][j]) {
                        isUnsafe = true;
                        break;
                    }
                }
                if (!isUnsafe) {
                    safeState.add(currentIndex);
                    currentIndex = (currentIndex + 1) % numProcess;
                    return true;

                }
            }
            currentIndex = (currentIndex + 1) % numProcess;
            ++count;
        }
        return false;
    }

    public boolean findIsVisited(int numProcess, int numResource) {
        if (numResource == this.numResource - 1) {
            return true;
        }
        if (max[numProcess][numResource] == 0) {
            return findIsVisited(numProcess, numResource + 1);
        }
        return false;
    }

    public boolean isAllSafe() {
        int in = 0;
        while (safeState.size() < numProcess) {
            printMax();
            System.out.println();
            printAllo();
            System.out.println();
            printNeed();
            System.out.println();
            printAvail();
            if (isSafe()) {
                for (int i = 0; i < numResource; i++) {
                    need[currentIndex == 0 ? numProcess - 1 : currentIndex - 1][i] = 0;
                    allocation[currentIndex == 0 ? numProcess - 1 : currentIndex - 1][i] = 0;
                    max[currentIndex == 0 ? numProcess - 1 : currentIndex - 1][i] = 0;
                }
                calculateSum();
                calculateNeed();
                calculateAvail();
            } else {
                System.out.println();
                return false;
            }
            System.out.println("\n--------------------------------");
        }
        return true;
    }

    public void printMax() {
        System.out.println("Max matrix:\n");
        System.out.print("       ");
        for (int i = 0; i < numResource; ++i) {
            System.out.printf("%-7s", "R" + (i + 1));
        }
        System.out.println();
        for (int i = 0; i < numProcess; ++i) {
            System.out.print("P" + i + "     ");
            for (int j = 0; j < numResource; ++j) {
                System.out.printf("%-7s", max[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printAllo() {
        System.out.println("Allocation matrix:\n");
        System.out.print("       ");

        for (int i = 0; i < numResource; ++i) {
            System.out.printf("%-7s", "R" + (i + 1));
        }
        System.out.println();
        for (int i = 0; i < numProcess; ++i) {
            System.out.print("P" + i + "     ");
            for (int j = 0; j < numResource; ++j) {
                System.out.printf("%-7s", allocation[i][j] + " ");
            }
            System.out.println();
        }
    }

    public void printNeed() {
        System.out.println("Needed matrix:\n");
        System.out.print("       ");

        for (int i = 0; i < numResource; ++i) {
            System.out.printf("%-7s", "R" + (i + 1));
        }
        System.out.println();

        for (int i = 0; i < numProcess; ++i) {
            System.out.print("P" + i + "     ");
            for (int j = 0; j < numResource; ++j) {
                System.out.printf("%-7s", need[i][j] + " ");
            }
            System.out.println();
        }
    }

    public boolean printAvail() {
        System.out.println("Resource is");
        for (int i = 1; i <= numResource; i++) {
            System.out.print("R"+i+"    ");
        }
        System.out.println();
        printResource();
        System.out.println("\n-----------------------");
        System.out.println("Sum is");
        for (int i = 1; i <= numResource; i++) {
            System.out.print("R"+i+"    ");
        }
        System.out.println();
        printSum();
        System.out.print("\n------------------");
        System.out.println("\nAvailable Vector: ");
        for (int i = 0; i < numResource; ++i) {
            System.out.print("R" + (i + 1) + " = " + available[i] + ",   ");
        }
        return true;
    }

    public boolean printResource() {
        for (int i = 0; i < numResource; ++i) {
            System.out.print(resource[i] + "    ");
        }
        return true;
    }

    public boolean printSum() {
        for (int i = 0; i < numResource; ++i) {
            System.out.print(sum[i] + "     ");
        }
        return true;
    }

    public boolean printSafe() {
        System.out.println("Safe State Order : ");
        for (int i : safeState) {
            System.out.print("P" + (i) + ", ");
        }
        return true;
    }
}
