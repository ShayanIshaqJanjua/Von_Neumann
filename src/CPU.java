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
        instructSet.put("LDA", 0b0001);//Loads data from the data location given to the ACC
        instructSet.put("STA", 0b0010);//Stores data from the ACC to the given data location
        instructSet.put("ADD", 0b0011);//Adds the value from the given data location to the ACC
        instructSet.put("SUB", 0b0100);//Subtracts the data from the given data location from the ACC
        instructSet.put("BRZ", 0b0101);//Branches to the given instruction if the content in the ACC is 0
        instructSet.put("BRP", 0b0110);//Branches to the given instruction id the content in the ACC is greater than 0
        instructSet.put("BRA", 0b0111);//Branched to the given data location no matter what
        instructSet.put("DAT", 0b1000);//Allocates a data location with the given name



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
                    MainMemory[num] = 0b1111000000000;
                } else {
                    String[] s = l.split(" ");//Lexical Analysis, split code into tokens
                    if (dataLocals.get(s[1]) != null) {//checks if a known data location is referenced
                        operand = dataLocals.get(s[1]) | 0b100000000;//replaces the name of the location with the address in the code
                    } else {
                        try {
                            operand = Integer.parseInt(s[1]);//adds the immediate value of the integer provided
                        } catch (NumberFormatException e) {
                            System.out.println("Error on line " + num + " relating to the data location reference.");
                        }


                    }
                    try {
                        opcode = instructSet.get(s[0])<<9;//gets the integer value of the instruction from the instruction set and adds it to the opcode
                    } catch (NullPointerException e) {
                        System.out.println("Error on line " + num + " relating to the instruction spelling (Ensure all characters are CAPS).");
                    }
                    MainMemory[num] = opcode | operand;//combines the opcode and operand and loads it into the main memory
                    //increments the data address
                }
            }
            num++;
        }
        for(int i = 0; i < MainMemory.length; i++) {
            if (MainMemory[i] != null) {
                System.out.println(Integer.toBinaryString(MainMemory[i]));
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
            Fetch();
        }

    }

    public void Decode(){
        int opcode = (cu&0b1111000000000)>>9;
        int operand = cu&0b0000111111111;
        cir = opcode;
        mdr = operand;
        Execute();
        Decode();
    }

    public void Execute(){
        switch (cir){
            case 0b0001:
                if((mdr&0b100000000) == 0b100000000 ){
                    acc = MainMemory[mdr&0b011111111];
                }
                else{
                    acc = mdr&0b011111111;
                }
                System.out.println("Loaded data to memory");
                break;
            case 0b0010:
                MainMemory[mdr & 0b011111111] = acc;
                System.out.println("Stored data to memory");
                break;
            case 0b0011:
                if((mdr&0b100000000) == 0b100000000 ){
                    alu = MainMemory[mdr&0b011111111];
                }
                else{
                    alu = mdr&0b011111111;
                }
                alu +=acc;
                acc = alu;
                System.out.println("Added to the accumulator");
                break;
            case 0b0100:
                alu = acc;
                if((mdr&0b100000000) == 0b100000000 ){
                    alu -= MainMemory[mdr&0b011111111];
                }
                else{
                    alu -= mdr&0b011111111;
                }
                acc = alu;
                System.out.println("Subtracted from the accumulator");
                break;
            case 0b0101:
                //Branch to the given data location if the value in the acc is 0
                break;
            case 0b0110:
                break;
            case 0b0111:
                break;
            case 0b1111:
                System.out.println("OUTPUT: " + acc);
                break;
        }
        Fetch();
        Execute();
    }
}

//PROBLEM: CANNOT IMMEDIATE ACCESS AS CPU ASSUMES DIRECT ADDRESSING ALWAYS
//SOLUTION: A WAY OF INDICATING A DIFFERENCE BETWEEN DATA LOCATIONS AND IMMEDIATE DATA
//POSSIBLE SOLUTION: USE A CHECK TO SEE IF NUMBER IS A KNOWN DATA LOCATION
//POSSIBLE SOLUTION: IMMEDIATE DATA IS NEGATIVE NUMBERS, DATA LOCATION ARE POSITIVE FC,FDDDDDDDDDDDDDINT
//LIKELY SOLUTION: CONVERT INTEGER DATA LOCATIONS AND INSTRUCTIONS INTO BINARY, USE 1 BIT TO INDICATE DIRECt OR IMMEDIATE ADDRESSING
