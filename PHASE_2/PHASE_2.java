
//implementing pipeling
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class PHASE_2 {
    public static int bflag; // variable bflag which decides whether the instruction is branch instruction or
                             // not
    // string array of 32 registers
    public static String[] regs = { "x0", "x1", "x2", "x3", "x4", "x5", "x6", "x7", "x8", "x9",
            "x10", "x11", "x12", "x13", "x14", "x15", "x16", "x17", "x18", "x19", "x20", "x21", "x22", "x23", "x24",
            "x25", "x26", "x27", "x28", "x29", "x30", "x31" };
    // initialising 32 registers to zero
    public static int[] regvalues = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
            0, 0,
            0, 0,
            0 };
    // string 2d array of pipelining
    public static String[][] pp = new String[5000][10000];

    public static void pre(String arr[], int[] memory) {

        int dataStart = -1; // initialising the index at which data section starts with -1
        int textStart = -1; // initialising the index at which text section starts with -1
        int main = -1; // initialising the index at which main section starts with -1
        if (arr[0].contains(".data")) { // start of data section
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
                        if (arr[i].substring(0, 6).contains(".word ")) { // searching in data section
                            String s2 = arr[i].substring(6);
                            String[] s3 = s2.split(",");
                            for (int j = 0; j < s3.length; j++) {
                                int h = Integer.parseInt(s3[j]);
                                memory[j] = h; // storing the values in word to memory
                            }
                        }
                    }
                }
                if (arr[textStart + 1].contains(".globl main") && arr[textStart + 2].contains("main:")) {// searching
                                                                                                         // for main
                                                                                                         // section to
                                                                                                         // start
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
    }

    public static void prep(String arr[], int[] memory) {
        String temp = "";
        int textStart = 0;
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] != null) {
                if (arr[i].contains(".text")) {
                    textStart = i; // getting the index at which text start starts

                }
            }

        }
        int main = textStart + 2; // main section starts at main: , which is 2 lines ahead of text section
        for (int i = main + 1; i < arr.length; i++) {
            if (arr[i] != null) {
                if (arr[i].startsWith("#")) {
                    continue; // if the instruction line starts with comments, continue
                }
                if (arr[i].contains(":")) {
                    continue; // if current instruction line contains label name,then continue
                }
                arr[i] = arr[i].replace(" ", ""); // replace all spaces with empty string
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
                    // if current instruction is li
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
                    System.out.println("!!!Error in the instructions!!!"); // reporting error if the instruction is not
                                                                           // found
                    System.exit(0);
                }
            }

        }
    }

    public static void fill(int x, int y, int If, int id, int ex, int mem, int wb) {
        try {
            while (If != 0) {
                pp[x][y] = "stall"; // if If is not equal to 0 , then consider it as stall
                y++;
                If--;
            }

            pp[x][y] = "IF"; // if If is equal to 0 , then consider it as IF
            y++;
            while (id != 0) {
                pp[x][y] = "stall"; // if id is not equal to 0 , then consider it as stall
                y++;
                id--;
            }

            pp[x][y] = "ID"; // if id is to 0 , then consider it as ID
            y++;
            while (ex != 0) {
                pp[x][y] = "stall"; // if ex is not equal to 0 , then consider it as stall
                y++;
                ex--;
            }
            pp[x][y] = "EX"; // if ex is to 0 , then consider it as EX
            y++;
            while (mem != 0) {
                pp[x][y] = "stall"; // if mem is not equal to 0 , then consider it as stall
                y++;
                mem--;
            }
            pp[x][y] = "MEM"; // if mem is to 0 , then consider it as MEM
            y++;
            while (wb != 0) {
                pp[x][y] = "stall"; // if wb is not equal to 0 , then consider it as stall
                y++;
                wb--;
            }
            pp[x][y] = "WB"; // if wb is to 0 , then consider it as WB
            while (pp[x][y].contains("stall")) {
                pp[x + 1][y] = "stall"; // if a cell is stall, then continue the whole column as stall
                x++;
            }
        } catch (NullPointerException e) {
            // for first intruction
            pp[x][y] = "IF";
            y++;
            pp[x][y] = "ID";
            y++;
            pp[x][y] = "EX";
            y++;
            pp[x][y] = "MEM";
            y++;
            pp[x][y] = "WB";
        }
    }

    public static String hazard(String ins) {
        if (ins.substring(0, 3).contains("add") && !ins.substring(3, 4).contains("i")) {
            return ins.substring(3, 5); // return destination register
        }
        if (ins.substring(0, 3).contains("sub") && !ins.substring(3, 4).contains("i")) {
            return ins.substring(3, 5); // return destination register
        }
        if (ins.substring(0, 4).contains("addi")) {
            return ins.substring(4, 6); // return destination register
        }
        if (ins.substring(0, 4).contains("subi")) {
            return ins.substring(4, 6); // return destination register
        }
        if (ins.substring(0, 2).contains("lw")) {
            return ins.substring(2, 4); // return destination register
        }
        if (ins.substring(0, 2).contains("sw")) {
            return ins.substring(2, 4); // return destination register
        }
        if (ins.substring(0, 3).contains("slt")) {
            return ins.substring(3, 5); // return destination register
        }
        if (ins.substring(0, 3).contains("sgt")) {
            return ins.substring(3, 5); // return destination register
        }
        if (ins.substring(0, 2).contains("li")) {
            return ins.substring(2, 4); // return destination register
        }

        return "null";
    }

    public static boolean branchhazard(String ins) {
        boolean flag = false;
        if (ins.substring(0, 3) == "bne" || ins.substring(0, 3) == "jal") {
            flag = true; // true if it is branch instruction
        }
        return flag;
    }

    public static void hazard1(int row) {
        int IF = 0, ID = 0, EX = 0, MEM = 0;
        int clk = 0;
        for (int j = 1; j < 10000; j++) {
            if (pp[row][j] != null) {
                if (pp[row][j].contains("WB")) {
                    clk = j; // index at which WB is seen
                }
            }

        }
        for (int j = 1; j < clk; j++) {
            if (pp[row][j] != null) {
                if (pp[row][j].contains("IF")) {
                    IF = j; // index at which IF is seen
                }
                if (pp[row][j].contains("ID")) {
                    ID = j; // index at which ID is seen
                }
                if (pp[row][j].contains("EX")) {
                    EX = j; // index at which EX is seen
                }
                if (pp[row][j].contains("MEM")) {
                    MEM = j; // index at which MEM is seen
                }
            }

        }
        for (int j = IF + 1; j < ID; j++) {
            if (pp[row][j].contains("stall")) {
                pp[row + 1][j] = "stall"; // if a cell is stall, then continue the whole column as stall
            }
        }
        for (int j = ID + 1; j < EX; j++) {
            if (pp[row][j].contains("stall")) {
                pp[row + 1][j] = "stall"; // if a cell is stall, then continue the whole column as stall
            }
        }
        for (int j = EX + 1; j < MEM; j++) {
            if (pp[row][j].contains("stall")) {
                pp[row + 1][j] = "stall"; // if a cell is stall, then continue the whole column as stall
            }
        }
        for (int j = MEM + 1; j < clk; j++) {
            if (pp[row][j].contains("stall")) {
                pp[row + 1][j] = "stall"; // if a cell is stall, then continue the whole column as stall
            }
        }
    }

    public static void pipeline(int row, int flag, String[] arr1) {
        int clk = 1;
        int j = 0;
        for (int i = 0; i < row; i++) {
            j = clk;
            if (pp[i][0] != null) {
                if (pp[i][0].substring(0, 4).contains("addi")) {
                    // if current instruction is addi
                    if (i != 0 && pp[i][0].substring(6, 8).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }
                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else if (i == 0) {
                        fill(i, j, 0, 0, 0, 0, 0);
                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        } else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }
                    }
                }
                if (pp[i][0].substring(0, 4).contains("subi")) {
                    // if current instruction is subi
                    if (i != 0 && pp[i][0].substring(6, 8).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }
                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else if (i == 0) {
                        fill(i, j, 0, 0, 0, 0, 0);
                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        } else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }
                    }
                }
                if (pp[i][0].substring(0, 3).contains("add") && !pp[i][0].substring(3, 4).contains("i")) {
                    // if current instruction is add
                    if (pp[i][0].substring(5, 7).contains(hazard(pp[i - 1][0]))
                            || pp[i][0].substring(7, 9).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }

                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        } else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }
                    }
                }
                if (pp[i][0].substring(0, 3).contains("sub") && !pp[i][0].substring(3, 4).contains("i")) {
                    // if current instruction is sub
                    if (pp[i][0].substring(5, 7).contains(hazard(pp[i - 1][0]))
                            || pp[i][0].substring(7, 9).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }

                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        } else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }
                    }
                }
                if (pp[i][0].substring(0, 3).contains("slt")) {
                    // if current instruction is slt
                    if (pp[i][0].substring(5, 7).contains(hazard(pp[i - 1][0]))
                            || pp[i][0].substring(7, 9).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }
                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        } else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }

                    }
                }
                if (pp[i][0].substring(0, 3).contains("sgt")) {
                    // if current instruction is sgt
                    if (pp[i][0].substring(5, 7).contains(hazard(pp[i - 1][0]))
                            || pp[i][0].substring(7, 9).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }
                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        } else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }

                    }
                }
                if (pp[i][0].substring(0, 3).contains("bne")) {
                    // if current instruction is bne
                    int pc = 0;
                    bflag = 0;
                    for (int j1 = 0; j1 < arr1.length; j1++) {
                        if (pp[i][0].contains(arr1[j]))
                            pc = j;
                    }
                    if (pp[i + 1][0] != null) {
                        if (!pp[i + 1][0].contains(arr1[pc + 1]))
                            bflag = 1;
                        else
                            bflag = 0;
                    }

                    if (pp[i][0].substring(3, 5).contains(hazard(pp[i - 1][0]))
                            || pp[i][0].substring(5, 7).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            }

                            else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }
                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }

                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        }

                        else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }

                    }

                }
                if (pp[i][0].substring(0, 2).contains("lw")) {
                    // if current instruction is lw
                    if (i != 0 && pp[i][0].substring(pp[i][0].length() - 3,
                            pp[i][0].length() - 1).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }
                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        } else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }
                    }
                }
                if (pp[i][0].substring(0, 2).contains("sw")) {
                    // if current instruction is sw
                    if (i != 0 && pp[i][0].substring(pp[i][0].length() - 3,
                            pp[i][0].length() - 1).contains(hazard(pp[i - 1][0]))) {
                        // if the source register is destination register for previous instruction
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && flag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 2, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 2, 0, 0);
                            }
                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 1, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else {
                        hazard1(i - 1);
                        if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                            fill(i, j, 1, 0, 0, 0, 0);
                        } else {
                            fill(i, j, 0, 0, 0, 0, 0);
                        }
                    }
                }
                if (pp[i][0].substring(0, 2).contains("li")) {
                    // if current instruction is li
                    if (i != 0) {
                        if (flag == 0) {// non-forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 0, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        } else {// forwarding
                            hazard1(i - 1);
                            if (branchhazard(pp[i - 1][0]) && bflag == 1) {// if branch instruction
                                fill(i, j, 0, 0, 0, 0, 0);
                            } else {
                                fill(i, j, 0, 0, 0, 0, 0);
                            }
                        }
                    } else {
                        fill(i, j, 0, 0, 0, 0, 0);
                    }
                }

            }

            for (int q = 1; q < 10000; q++) {
                if (pp[i][q] != null) {
                    if (pp[i][q].contains("IF")) {
                        clk = q + 1;
                    }
                }

            }
        }
    }

    public static void main(String[] args) {
        try {
            Scanner input = new Scanner(System.in);
            System.out.println("----------------------------Welcome to TECH PHANTOM'S Simulator----------------------");
            System.out.println();
            System.out.println("Enter your file name(only .asm files): ");
            String s = input.nextLine(); // taking input of file name
            System.out.println();
            System.out.println("--------------The code within the input file--------------");
            String s1 = s + ".asm";
            File file = new File(s1);
            Scanner sc = new Scanner(file);
            String[] arr = new String[1000];
            String[] arr1 = new String[1000];
            int[] memory = new int[1024];
            for (int i = 0; i < 1024; i++) {
                memory[i] = 0;
            }
            int k = 0;
            while (sc.hasNextLine()) {
                arr[k] = sc.nextLine();
                System.out.println(arr[k]); // storing the text in .asm file in string array
                k++;
            }
            pre(arr, memory); // finding if data and text sections are correct
            int mainindex = 0;
            for (int i = 1; i <= k - 1; i++) {
                if (arr[i].contains("main:")) {
                    mainindex = i;
                    break;
                }
            }
            int programCounter = mainindex + 2;
            int pipeRow = 0;
            int k1 = 0;
            while (programCounter <= k) {

                String y = arr[programCounter - 1];
                String[] s4 = y.split("#");
                s4[0] = s4[0].replace(" ", "");
                String[] s3 = s4[0].split(",");
                String[] s5 = s3[0].split("x");
                prep(arr, memory); // implementing the code within input file
                pp[pipeRow][0] = s4[0];
                arr1[k1] = s5[0];
                pipeRow++;
                programCounter++;
                k1++;
            }
            System.out.println();
            System.out.println("!!!ENTER 1 for Forwarding and 0 for non forwarding in pipelining!!!");
            int flag = input.nextInt(); // taking input of user's choice on data forwarding
            System.out.println();
            System.out.println("-------------------Pipelining-----------------");
            pipeline(pipeRow, flag, arr1);
            for (int l = 0; l < pipeRow; l++) {
                for (int l1 = 0; l1 < l + 6; l1++) {
                    System.out.print(pp[l][l1] + " "); // printing pipeline 2d array
                }
                System.out.println();
            }
            System.out.println();
            int count = 0;
            for (int j = 0; j < 10000; j++) {
                if (pp[pipeRow - 1][j] != null && pp[pipeRow - 1][j].contains("WB")) {
                    System.out.println("Total number of clock cycles: " + j); // checking the total number of clock
                                                                              // cycles
                    count = j;
                }
            }
            String[] stall = new String[5000]; // array to store stall instructions
            int count1 = 0;
            int k2 = 0;
            for (int l = 0; l < pipeRow; l++) {
                for (int l1 = 1; l1 < 10000; l1++) {
                    if (pp[l][l1] != null && pp[l][l1].contains("stall")) {
                        count1++;
                        stall[k2] = pp[l][0]; // storing stalled instructions
                    }
                }
                k2++;
            }
            System.out.println();
            System.out.println("Total number of stalls: " + count1); // printing number of stalls
            System.out.println();
            float ins = (float) pipeRow / count;
            System.out.println("Instructions per cycle is: " + ins); // printing instruction per cycles
            System.out.println();
            System.out.println("List of instructions for which stalls occur: ");
            for (int l = 0; l < k2; l++) {
                if (stall[l] != null && stall[l].contains("")) {
                    System.out.println("Instruction line number: " + l + " - " + stall[l]); // list of instructions at
                                                                                            // which stalls occur
                }
            }
            System.out.println();
            System.out.println("-------------The values in the registers are as follows--------------");
            for (int i = 0; i < 15; i++) { // can print upto 1024 values, printing only 15 due to space issues
                System.out.println(regs[i] + " : " + regvalues[i]); // printing the register values
            }
            System.out.println();
            System.out.println("-------------The values in the memory are as follows--------------");
            for (int i = 0; i < 15; i++) {// can print upto 1024 values, printing only 15 due to space issues
                System.out.println("memory cell " + i + ": " + memory[i]); // printing the register values

            }
            input.close();
            sc.close();
        } catch (FileNotFoundException e) {
            System.out.println("File Cannot be found"); // file not found exception
        }
    }
}
