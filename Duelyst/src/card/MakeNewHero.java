package card;

import java.io.*;
import java.util.Scanner;

public class MakeNewHero {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        try {
            FileReader fileReader = new FileReader("../CardsFile/Hero/template");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            System.out.println("name: ");
            String input = scanner.nextLine();
            File file = new File("../CardsFile/Hero/" + input);
            FileWriter fileWriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                input = scanner.nextLine();

                bufferedWriter.write(line);
                bufferedWriter.write(" " + input);
                bufferedWriter.newLine();
            }
            bufferedWriter.close();

        } catch (FileNotFoundException e) {
            System.out.println("file not found");
        } catch (Exception e) {
            System.out.println("other error");
        }
    }
}