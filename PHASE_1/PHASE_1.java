import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PHASE_1 {
    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("----------------------------Welcome to TECH PHANTOM'S Simulator----------------------");
            System.out.println("\n");
            // string array of 32 registers
            String[] regs = { "x0", "x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9",
                    "x10", "x11", "x12", "x13", "x14",
                    "x15", "x16", "x17", "x18", "x19", "x20", "x21", "x22", "x23", "x24", "x25",
                    "x26", "x27", "x28", "x29",
                    "x30", "x31" };
            // initialising 32 registers to zero
            int[] regvalues = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
                    0, 0,
                    0 };
            // integer array of memory cells
            int[] memory = new int[1024];
            // initialising memory cells to zero
            for (int i = 0; i < 1024; i++) {
                memory[i] = 0;
            }
            String temp = "";
            System.out.println("Enter your file name: ");
            // input the .asm file
            String s = input.nextLine();
            System.out.println("--------------The code within the input file--------------");
            String s1 = s + ".asm";
            File file = new File(s1);
            Scanner sc = new Scanner(file);
            String[] arr = new String[1000];
            int k = 0;
            while (sc.hasNextLine()) {
                arr[k] = sc.nextLine();
                System.out.println(arr[k]); // storing the text in .asm file in string array
                k++;
            }
            int dataStart = -1; // initialising the index at which data section starts with -1
            int textStart = -1; // initialising the index at which text section starts with -1
            int main = -1; // initialising the index at which main section starts with -1
            if (arr[0].contains(".data")) {
                dataStart = 0;
                for (int i = 1; i < arr.length; i++) {
                    if (arr[i] != null) {
                        if (arr[i].contains(".text")) {
                            textStart = i; // getting the index at which text start starts

                        }
                    }

                }
                if (textStart == 1) {
                    if (arr[textStart + 1].contains(".globl main") && arr[textStart + 2].contains("main:")) {
                        main = textStart + 2;
                    } else {
                        System.out.println("!!!Error Found in text section!!!");
                        System.exit(0);
                    }
                } else {

                    for (int i = dataStart + 1; i <= textStart - 1; i++) {
                        if (arr[i].contains(":")) {

                            arr[i] = arr[i].substring(arr[i].indexOf(":") + 2);
                            if (arr[i].substring(0, 6).contains(".word ")) {
                                String s2 = arr[i].substring(6);
                                String[] s3 = s2.split(",");
                                for (int j = 0; j < s3.length; j++) {
                                    int h = Integer.parseInt(s3[j]);
                                    memory[j] = h; // storing the values in word to memory
                                }
                            }
                        }
                    }
                    if (arr[textStart + 1].contains(".globl main") && arr[textStart + 2].contains("main:")) {
                        main = textStart + 2;
                    } else {
                        System.out.println("!!!Error Found in text section!!!");
                        System.exit(0);
                    }
                }
            } else {
                System.out.println("!!!Error Found in data section!!!");
                System.exit(0);
            }
            for (int i = main + 1; i < arr.length; i++) {
                if (arr[i] != null) {
                    if (arr[i].startsWith("#")) {
                        continue; // if the instruction line starts with comments, continue
                    }
                    if (arr[i].contains(":")) {
                        continue;
                    }
                    arr[i] = arr[i].replace(" ", "");
                    if (arr[i].substring(0, 3).contains("add") && !arr[i].substring(3, 4).contains("i")) {
                        // if the current instruction is add
                        String s2 = arr[i].substring(3);
                        String[] s4 = s2.split("#");
                        String[] s3 = s4[0].split(",");
                        int[] a2 = new int[s3.length];
                        int p = 0;
                        for (String k1 : s3) {
                            for (int j = 0; j < 32; j++) {
                                if (k1.contains(regs[j])) {
                                    a2[p] = j; // store register numbers in array
                                    p++;
                                }
                            }
                        }

                        regvalues[a2[0]] = regvalues[a2[1]] + regvalues[a2[2]]; // performing addition operation

                    } else if (arr[i].substring(0, 3).contains("sub") && !arr[i].substring(3, 4).contains("i")) {
                        // if the current instruction is sub
                        String s2 = arr[i].substring(3);
                        String[] s4 = s2.split("#");
                        String[] s3 = s4[0].split(",");
                        int[] a2 = new int[s3.length];
                        int p = 0;
                        for (String k1 : s3) {
                            for (int j = 0; j < 32; j++) {
                                if (k1.contains(regs[j])) {
                                    a2[p] = j; // store register numbers in array
                                    p++;
                                }
                            }
                        }
                        regvalues[a2[0]] = regvalues[a2[1]] - regvalues[a2[2]]; // performing subtraction operation

                    } else if (arr[i].substring(0, 3).contains("bne")) {
                        // if the current instruction in bne
                        String s2 = arr[i].substring(3);
                        String[] s4 = s2.split("#");
                        String[] s3 = s4[0].split(",");
                        int[] a2 = new int[s3.length - 1];
                        int p = 0;
                        for (int k1 = 0; k1 < 2; k1++) {
                            for (int j = 0; j < 32; j++) {
                                if (s3[k1].contains(regs[j])) {
                                    a2[p] = j; // store register numbers in array
                                    p++;
                                }
                            }
                        }
                        if (regvalues[a2[0]] != regvalues[a2[1]]) {
                            // if the values in registers are not equal, then go to label
                            String[] s5 = s2.split("#");
                            temp = s5[0].substring(6);
                            temp = temp + ":";
                            for (int i2 = 0; i2 < arr.length; i2++) {
                                if (arr[i2] != null) {
                                    if (arr[i2].contains(temp)) {
                                        i = i2; // index of line consisting label
                                        break;
                                    }
                                }

                            }

                        } else {
                            continue;
                        }

                    } else if (arr[i].substring(0, 3).contains("jal")) {
                        // if current instruction is jal
                        String[] s5 = arr[i].split("#");
                        temp = s5[0].substring(3); // temp is label
                        temp = temp + ":";
                        for (int i2 = 0; i2 < arr.length; i2++) {
                            if (arr[i2] != null) {
                                if (arr[i2].contains(temp)) {
                                    i = i2; // index of line consists of label
                                    break;
                                }
                            }
                        }
                    } else if (arr[i].substring(0, 4).contains("addi")) {
                        // if current instruction is addi
                        String s2 = arr[i].substring(4);
                        String[] s4 = s2.split("#");
                        String[] s3 = s4[0].split(",");
                        int i2 = Integer.parseInt(s3[2]);
                        int[] a2 = new int[2];
                        int p = 0;
                        for (String k1 : s3) {
                            for (int j = 0; j < 32; j++) {
                                if (k1.contains(regs[j])) {
                                    a2[p] = j; // store register numbers in array
                                    p++;
                                }
                            }
                        }
                        regvalues[a2[0]] = regvalues[a2[1]] + i2; // performing add operation
                    } else if (arr[i].substring(0, 4).contains("subi")) {
                        // if current instruction is subi
                        String s2 = arr[i].substring(4);
                        String[] s4 = s2.split("#");
                        String[] s3 = s4[0].split(",");
                        int i2 = Integer.parseInt(s3[2]);
                        int[] a2 = new int[2];
                        int p = 0;
                        for (String k1 : s3) {
                            for (int j = 0; j < 32; j++) {
                                if (k1.contains(regs[j])) {
                                    a2[p] = j; // store register numbers in array
                                    p++;
                                }
                            }
                        }
                        regvalues[a2[0]] = regvalues[a2[1]] - i2; // performing subtraction operation
                    } else if (arr[i].substring(0, 2).contains("lw")) {
                        // if current instruction is lw
                        int x = 0, y = 0;
                        String s2 = arr[i].substring(2);
                        String s6[] = s2.split("#");
                        String[] s3 = s6[0].split(",");
                        String s4 = s3[1].substring(0, s3[1].indexOf("("));
                        String s5 = s3[1].substring(s3[1].indexOf("(") + 1, s3[1].indexOf(")"));
                        int i2 = Integer.parseInt(s4);
                        for (int j = 0; j < 32; j++) {
                            if (s3[0] != null) {
                                if (regs[j].contains(s3[0])) {
                                    x = j;
                                }
                            }

                        }
                        for (int j = 0; j < 32; j++) {
                            if (s5 != null) {
                                if (s5.contains(regs[j])) {
                                    y = j;
                                }
                            }

                        }
                        regvalues[x] = memory[(i2 + regvalues[y])]; // loading memory value in register
                    } else if (arr[i].substring(0, 2).contains("sw")) {
                        // if current instruction is sw
                        int x = 0, y = 0;
                        String s2 = arr[i].substring(2);
                        String s6[] = s2.split("#");
                        String[] s3 = s6[0].split(",");
                        String s4 = s3[1].substring(0, s3[1].indexOf("("));
                        String s5 = s3[1].substring(s3[1].indexOf("(") + 1, s3[1].indexOf(")"));
                        int i2 = Integer.parseInt(s4);
                        for (int j = 0; j < 32; j++) {
                            if (s3[0] != null) {
                                if (regs[j].contains(s3[0])) {
                                    x = j;
                                }
                            }

                        }
                        for (int j = 0; j < 32; j++) {
                            if (s5 != null) {
                                if (s5.contains(regs[j])) {
                                    y = j;
                                }
                            }

                        }
                        memory[(i2 + regvalues[y])] = regvalues[x]; // storing register value in memory
                    } else if (arr[i].substring(0, 3).contains("slt")) {
                        // if current instruction is slt
                        String s2 = arr[i].substring(3);
                        String[] s4 = s2.split("#");
                        String[] s3 = s4[0].split(",");
                        int[] a2 = new int[s3.length];
                        int p = 0;
                        for (String k1 : s3) {
                            for (int j = 0; j < 32; j++) {
                                if (k1.contains(regs[j])) {
                                    a2[p] = j;
                                    p++;
                                }
                            }
                        }
                        if (regvalues[a2[1]] < regvalues[a2[2]]) {
                            regvalues[a2[0]] = 1;
                        } else {
                            regvalues[a2[0]] = 0;
                        }
                    } else if (arr[i].substring(0, 3).contains("sgt")) {
                        // if current instruction is sgt
                        String s2 = arr[i].substring(3);
                        String[] s4 = s2.split("#");
                        String[] s3 = s4[0].split(",");
                        int[] a2 = new int[s3.length];
                        int p = 0;
                        for (String k1 : s3) {
                            for (int j = 0; j < 32; j++) {
                                if (k1.contains(regs[j])) {
                                    a2[p] = j;
                                    p++;
                                }
                            }
                        }
                        if (regvalues[a2[1]] > regvalues[a2[2]]) {
                            regvalues[a2[0]] = 1;
                        } else {
                            regvalues[a2[0]] = 0;
                        }
                    } else if (arr[i].substring(0, 2).contains("li")) {
                        String s2 = arr[i].substring(2);
                        int x = 0;
                        String[] s4 = s2.split("#");
                        String[] s3 = s4[0].split(",");
                        int i2 = Integer.parseInt(s3[1]);
                        for (int j = 0; j < 32; j++) {
                            if (s3[0] != null) {
                                if (regs[j].contains(s3[0])) {
                                    x = j;
                                    break;
                                }
                            }

                        }
                        regvalues[x] = i2;
                    } else {
                        System.out.println("!!!Error in the instructions!!!");
                        System.exit(0);
                    }
                }

            }
            // printing register values
            System.out.println("-------------The values in the registers are as follows--------------");
            for (int i = 0; i < 32; i++) {
                System.out.println(regs[i] + " : " + regvalues[i]);
            }
            // printing memory cells
            // The sorted array will be updated in the memory cells in ascending order
            System.out.println("-------------The values in the memory are as follows--------------");
            for (int i = 0; i < 32; i++) {
                System.out.println("memory cell " + i + ": " + memory[i]);
            }
            input.close();
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Cannot be found"); // file not found exception
        }
    }
}