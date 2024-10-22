import com.sun.tools.javac.Main;

import javax.print.DocFlavor;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CPU {

    public static Integer[] MainMemory = new Integer[200];
    public static HashMap<String, Integer> dataLocals = new HashMap<String, Integer>();
    public int pc = 0;
    public int alu = 0;
    public int acc = 0;
    public int cir = 0;
    public int mdr = 0;
    public int mar = 0;
    public int cu = 0;
    public static void main(String[] args) {
        loadToMemory("base");
        new CPU().Fetch();
    }


    private static void loadToMemory(String f) {

        HashMap<String, Integer> instructSet = new HashMap<String, Integer>(); // Holds the instruction set
        instructSet.put("LDA", 1);//Loads data from the data location given to the ACC
        instructSet.put("STA", 2);//Stores data from the ACC to the given data location
        instructSet.put("ADD", 3);//Adds the value from the given data location to the ACC
        instructSet.put("SUB", 4);//Subtracts the data from the given data location from the ACC
        instructSet.put("BRZ", 5);//Branches to the given instruction if the content in the ACC is 0
        instructSet.put("BRP", 6);//Branches to the given instruction id the content in the ACC is greater than 0
        instructSet.put("BRA", 7);//Branched to the given data location no matter what
        instructSet.put("DAT", 8);//Allocates a data location with the given name



        InputStream inputStream = CPU.class.getClassLoader().getResourceAsStream("base.txt");
        Scanner readLine = new Scanner(inputStream);

        String[] temp = new String[MainMemory.length];//initialises string array to hold lines of code

        int currIndex = 0;
        while (readLine.hasNextLine()) {//loops through every line in the program
            String output = readLine.nextLine();
            temp[currIndex] = output;//adds the line into the string ar
            currIndex++;//increments the counter
        }


        for (int i = 0; i < temp.length - 1; i++) { // loops through each line of code
            if (temp[i] == null) {
            } else {
                if (temp[i].equals("OUT")) {
                } else {
                    String[] s = temp[i].split(" "); // Lexical Analysis, split code into tokens
                    if (s[0].equals("DAT")) {
                        dataLocals.put(s[1], i + 1); // saves the name of the data location used with the location address
                    }
                }
            }
        }



        int opcode =0;//to store the opcode
        int operand = 0;//to store the operand
        int num = 0;
        for(String l : temp){//loops through every element of the temp array
            if(l == null){

            }
            else {
                if (l.equals("OUT")) {
                    MainMemory[num] = 9999;
                } else {
                    String[] s = l.split(" ");//Lexical Analysis, split code into tokens
                    if (dataLocals.get(s[1]) != null) {//checks if a known data location is referenced
                        operand = dataLocals.get(s[1]);//replaces the name of the location with the address in the code
                    } else {
                        try {
                            operand = Integer.parseInt(s[1]);//adds the immediate value of the integer provided
                        } catch (NumberFormatException e) {
                            System.out.println("Error on line " + num + " relating to the data location reference.");
                        }


                    }
                    try {
                        opcode = instructSet.get(s[0]) * 1000;//gets the integer value of the instruction from the instruction set and adds it to the opcode
                    } catch (NullPointerException e) {
                        System.out.println("Error on line " + num + " relating to the instruction spelling (Ensure all characters are CAPS).");
                    }
                    MainMemory[num] = opcode + operand;//combines the opcode and operand and loads it into the main memory
                    //increments the data address
                }
            }
            num++;
        }
        for(int i = 0; i < MainMemory.length; i++) {
            if (MainMemory[i] != null) {
                System.out.println(MainMemory[i]);
            }
        }



    }

    public void Fetch(){
        mar = pc;
        if(MainMemory[mar] == null){
            return;
        }
        else{
            cir = MainMemory[mar];
            cu = cir;
            pc++;
            Decode();
        }

    }

    public void Decode(){
        int opcode = cu/1000;
        int operand = cu%1000;
        cir = opcode;
        mdr = operand;
        Execute();
    }

    public void Execute(){
        switch (cir){
            case 1:
                acc = MainMemory[mdr];
                System.out.println("Loaded data to memory");
                break;
            case 2:
                MainMemory[mdr] = acc;
                System.out.println("Stored data to memory");
                break;
            case 3:
                alu = MainMemory[mdr];
                alu +=acc;
                acc = alu;
                System.out.println("Added to the accumulator");
                break;
            case 4:
                alu = acc;
                alu -= MainMemory[mdr];
                acc = alu;
                System.out.println("Subtracted from the accumulator");
                break;
            case 5:
                //Branch to the given data location if the value in the acc is 0
                break;
            case 6:
                break;
            case 7:
                break;
            case 9:
                System.out.println("OUTPUT: " + acc);
                break;
        }
        Fetch();
    }
}

//PROBLEM: CANNOT IMMEDIATE ACCESS AS CPU ASSUMES DIRECT ADDRESSING ALWAYS
//SOLUTION: A WAY OF INDICATING A DIFFERENCE BETWEEN DATA LOCATIONS AND IMMEDIATE DATA
//POSSIBLE SOLUTION: USE A CHECK TO SEE IF NUMBER IS A KNOWN DATA LOCATION
//POSSIBLE SOLUTION: IMMEDIATE DATA IS NEGATIVE NUMBERS, DATA LOCATION ARE POSITIVE FC,FDDDDDDDDDDDDDINT

