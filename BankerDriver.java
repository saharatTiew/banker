package banker2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class BankerDriver {
    public static void main(String[] args) {
        Scanner scan = new Scanner(System.in);

        System.out.print("Enter process size: ");
        int numProcess = scan.nextInt();
        System.out.print("Enter resource size: ");
        int numResource = scan.nextInt();

        Banker banker;
        System.out.print("Press I for input file or any keys for random data : ");
        scan.nextLine();

        if (scan.next().equalsIgnoreCase("i")) {
            System.out.print("Enter for input file name : ");
            String path = scan.next();
            File inputFile = new File(path);
            try {
                Scanner fileReader = new Scanner(inputFile);
                int[][] max = new int[numProcess][numResource];
                int[][] allocation = new int[numProcess][numResource];
                int[] resource = new int[numResource];

                for (int i = 0; i < numProcess; ++i) {
                    for (int j = 0; j < numResource; ++j) {
                        max[i][j] = fileReader.nextInt();
                    }
                }
                //for fix bug after read nextInt then nextLine
                fileReader.nextLine();
                //for skip line that separate between each type of input
                fileReader.nextLine();

                for (int i = 0; i < numProcess; ++i) {
                    for (int j = 0; j < numResource; ++j) {
                        allocation[i][j] = fileReader.nextInt();
                    }
                }

                fileReader.nextLine();
                fileReader.nextLine();

                for (int i = 0; i < numResource; ++i) {
                    resource[i] = fileReader.nextInt();
                }

                fileReader.close();
                banker = new Banker(numProcess, numResource, max, allocation, resource);
            } catch (FileNotFoundException e) {
                System.out.println("File is not found! So We will provide random input instead");
                System.out.println("----------------------------------------------------------");
                banker = new Banker(numProcess, numResource);
            }
        } else {
            banker = new Banker(numProcess, numResource);
        }

        if (banker.isAllSafe()) {
            banker.printSafe();
        } else {
            System.out.println("Deadlock Occurred");
        }
        scan.close();
    }
}
