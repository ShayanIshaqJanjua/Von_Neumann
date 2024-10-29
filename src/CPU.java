import com.sun.tools.javac.Main;

import javax.print.DocFlavor;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CPU {

    public static Integer[] MainMemory = new Integer[200];
    public static HashMap<String, Integer> dataLocals = new HashMap<String, Integer>();
    public static HashMap<String, Integer> branches = new HashMap<String, Integer>();
    static HashMap<String, Integer> instructSet = new HashMap<String, Integer>(); // Holds the instruction set
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
                    if(!instructSet.containsKey(s[0])) {
                        branches.put(s[0], i);
                    }
                    if (s[0].equals("DAT") || s[0].equals("BRP") || s[0].equals("BRZ") || s[0].equals("BRA")) {
                        dataLocals.put(s[1], i); // saves the name of the data location used with the location address
                    }
                }
            }
        }



        int line = 0;
        int num = 0;
        for(String l : temp){//loops through every element of the temp array
            if(l == null){

            }
            else {
                if (l.equals("OUT")) {
                    MainMemory[num] = 0b1111000000000;
                }
                else {
                    String[] s = l.split(" ");
                    if (branches.containsKey(s[0])) {
                        MainMemory[num] = branchedInstruction(s);
                    }
                    else{
                        MainMemory[num] = Instruction(s);
                    }
                }
            }
            num++;
        }


        for(int i = 0; i < MainMemory.length; i++) {
            if (MainMemory[i] != null) {
                System.out.println( i + ". " + Integer.toBinaryString(MainMemory[i]));
            }
        }



    }
    public static int Instruction(String[] s) {
        int opcode = instructSet.get(s[0])<<9;
        int operand =0;
        if (dataLocals.keySet().contains(s[1])) {
            operand = dataLocals.get(s[1]) | 0b100000000;
        }
        else{
            try{
                System.out.println(s[1]);
                operand = Integer.parseInt(s[1]);
            }catch(Exception e){
                System.out.println("Error with data Location.");
            }
        }
        return opcode | operand;
    }
    public static int branchedInstruction(String[] strings) {
        int opcode = 0;
        try {
            opcode = instructSet.get(strings[1])<<9;
        }catch (Exception e) {
            System.out.println("Error with instruction syntax on a branched line of code");
        }
        int operand = 0;
        if(dataLocals.containsKey(strings[2])) {
            operand = dataLocals.get(strings[2]) | 0b100000000;
        }
        else{
            try{
                operand = Integer.parseInt(strings[2]);
            }
            catch (Exception e) {
                System.out.println("Error with data location syntax on a branched line of code");
            }
        }

        return opcode |operand;
    }

    /*public void OldLoadToMemory() {
        String[] s = l.split(" ");//Lexical Analysis, split code into tokens
        if(branches.get(s[0])!= null){
            branched = true;
            if (dataLocals.get(s[2]) != null) {//checks if a known data location is referenced
                operand = dataLocals.get(s[2]) | 0b100000000;//replaces the name of the location with the address in the code
            } else {
                try {
                    operand = Integer.parseInt(s[2]);//adds the immediate value of the integer provided
                } catch (NumberFormatException e) {
                    System.out.println("Error on line " + num + " relating to the data location reference.");
                }

            }

        }
        if(branches.get(s[1]) != null){
            System.out.println("why");
            operand = branches.get(s[1]) | 0b100000000;
        }else {
            try {
                operand = Integer.parseInt(s[1]);//adds the immediate value of the integer provided
            } catch (NumberFormatException e) {
                System.out.println("is this true");
                System.out.println("Error on line " + num + " relating to the data location reference.");
            }

        }


        try {
            if(branched){
                opcode = instructSet.get(s[1])<<9;
                branched = false;
            }
            else{
                opcode = instructSet.get(s[0])<<9;//gets the integer value of the instruction from the instruction set and adds it to the opcode
            }
        } catch (NullPointerException e) {
            System.out.println("Error on line " + num + " relating to the instruction spelling (Ensure all characters are CAPS).");
        }
        MainMemory[num] = opcode | operand;//combines the opcode and operand and loads it into the main memory

    }*/

    public void Fetch(){
        mar = pc;
        //      System.out.println("Address copied from Program Counter to memory address register.");
        if(MainMemory[mar] == null){
            return;
        }
        else{
            cir = MainMemory[mar];
            //System.out.println("Copied the instruction address from the ");
            cu = cir;
            //System.out.println("Instruction sent to Control Unit");
            pc++;
            //System.out.println("Program counter incremented.");
            Decode();
        }

    }

    public void Decode(){
        int opcode = (cu&0b1111000000000)>>9;
        int operand = cu&0b0000111111111;
        if(opcode == 0b1000){
            return;
        }
        else {
            //System.out.println("Instruction decoded.");
            cir = opcode;
            mdr = operand;
            //System.out.println("Instruction sent to Current Instruction Register.");
            Execute();
        }
    }

    public void Execute(){
        switch (cir){
            case 0b0001:
                if((mdr&0b100000000) == 0b100000000 ){
                    acc = MainMemory[mdr&0b011111111];
                    System.out.println("Data at location " + mdr + " Loaded into Accumulator.");
                }
                else{
                    acc = mdr&0b011111111;
                    System.out.println("Value " + mdr + " loaded into accumulator.");
                }
                break;
            case 0b0010:
                MainMemory[mdr & 0b011111111] = acc;
                System.out.println("Stored data from accumulator to memory location " + mdr);
                break;
            case 0b0011:
                if((mdr&0b100000000) == 0b100000000 ){
                    alu = MainMemory[mdr&0b011111111];
                    System.out.println("Added data from memory location " + mdr + " to accumulator.");
                }
                else{
                    alu = mdr&0b011111111;
                    System.out.println("Value " + mdr + " added to accumulator.");
                }
                alu +=acc;
                acc = alu;
                break;
            case 0b0100:
                alu = acc;
                if((mdr&0b100000000) == 0b100000000 ){
                    alu -= MainMemory[mdr&0b011111111];
                    System.out.println("Subtracted data from memory location " + mdr + " to accumulator.");
                }
                else{
                    alu -= mdr&0b011111111;
                    System.out.println("Subtracted value " + mdr + " to accumulator.");
                }
                acc = alu;
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
    }
}

//PROBLEM: CANNOT IMMEDIATE ACCESS AS CPU ASSUMES DIRECT ADDRESSING ALWAYS
//SOLUTION: A WAY OF INDICATING A DIFFERENCE BETWEEN DATA LOCATIONS AND IMMEDIATE DATA
//POSSIBLE SOLUTION: USE A CHECK TO SEE IF NUMBER IS A KNOWN DATA LOCATION
//POSSIBLE SOLUTION: IMMEDIATE DATA IS NEGATIVE NUMBERS, DATA LOCATION ARE POSITIVE
//LIKELY SOLUTION [WORKED] : CONVERT INTEGER DATA LOCATIONS AND INSTRUCTIONS INTO BINARY, USE 1 BIT TO INDICATE DIRECt OR IMMEDIATE ADDRESSING

//PROBLEM: CANNOT READ BRANCH STATEMENTS OR BRANCHED AREAS OF CODE
//SOLUTION: INTEGRATE SOME WAY OF CHECKING IF A LINE OF CODE CONTAINS A BRANCH
//LIKELY SOLUTION: SPLIT THE LOAD TO MEMORY FUNCTION INTO SEPERATE FUNCTION WITH SOME DESIGNED FOR BRANCHED AND SOME FOR UNBRANCHED CODE
