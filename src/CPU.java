
import java.io.InputStream;
import java.lang.*;
import java.util.*;

public class CPU {

    public static int[] MainMemory = new int[200];
    public static HashMap<String, Integer> dataLocals = new HashMap<>();
    public static HashMap<String, Integer> branches = new HashMap<>();
    static HashMap<String, Integer> instructSet = new HashMap<>(); // Holds the instruction set
    public int pc = 0;
    public byte alu = 0;
    public byte acc = 0;
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



        InputStream inputStream = CPU.class.getClassLoader().getResourceAsStream(f + ".txt");
        assert inputStream != null;
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
                return;
            } else {
                if(!(temp[i].equals("OUT"))) {
                    String[] s = temp[i].split(" "); // Lexical Analysis, split code into tokens
                    if (!instructSet.containsKey(s[0])) {
                        branches.put(s[0], i );
                    }
                    if (s[0].equals("DAT")) {
                        dataLocals.put(s[1], i); // saves the name of the data location used with the location address
                    }
                }
            }
        }



        int num = 0;
        for(String l : temp){//loops through every element of the temp array
            if(l == null){
                return;
            }
            else {
                if (l.equals("OUT")) {
                    MainMemory[num] = 0b1111000000000;
                }
                else {
                    String[] s = l.split(" ");
                    if(s[1].equals("OUT")){
                        MainMemory[num] = 0b1111000000000;
                    }else {
                        if (branches.containsKey(s[0])) {
                            MainMemory[num] = branchedInstruction(s);
                        } else {
                            MainMemory[num] = Instruction(s);
                        }
                    }
                }
            }
            num++;
        }


        for(int i = 0; i < MainMemory.length; i++) {
            if (MainMemory[i] == 0) {
                System.out.println( i + ". " + Integer.toBinaryString(MainMemory[i]));
            }
        }



    }
    public static int Instruction(String[] s) {
        int opcode = instructSet.get(s[0])<<9;
        int operand =0;
        if (dataLocals.containsKey(s[1])) {
            operand = dataLocals.get(s[1]) | 0b100000000;
        } else if (branches.containsKey(s[1])) {
            operand= branches.get(s[1]);
        } else{
            try{
                operand = Integer.parseInt(s[1]);
            }catch(Exception e){
                System.out.println(s[1]);
                System.out.println("Error with data Location.");
            }
        }
        return opcode | operand;
    }
    public static int branchedInstruction(String[] strings) {
        int opcode = 0;
        if(strings[1].equals("OUT")) {
            return 0b1111000000000;
        }

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
        if(MainMemory[mar]!=0){
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
        if(opcode!= 0b1000){
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
                    acc = (byte) MainMemory[mdr&0b011111111];
                    System.out.println("Data at location " + (mdr&0b011111111) + " Loaded into Accumulator.");
                }
                else{                    acc = (byte) (mdr&0b011111111);
                    System.out.println("Value " + (mdr&0b011111111) + " loaded into accumulator.");
                }
                break;
            case 0b0010:
                MainMemory[mdr & 0b011111111] = acc;
                System.out.println("Stored data from accumulator to memory location " + (mdr&0b011111111));
                break;
            case 0b0011:
                if((mdr&0b100000000) == 0b100000000 ){
                    alu = (byte) MainMemory[mdr&0b011111111];
                    System.out.println("Added data from memory location " + (mdr&0b011111111) + " to accumulator.");
                }
                else{
                    alu = (byte) (mdr&0b011111111);
                    System.out.println("Value " + (mdr&0b011111111) + " added to accumulator.");
                }

                alu = fullAdder(alu,acc);
                acc=alu;
                break;
            case 0b0100:
                if((mdr&0b100000000) == 0b100000000 ){
                    alu = (byte) MainMemory[mdr&0b011111111];
                    System.out.println("Subtracted data from memory location " + (mdr&0b011111111) + " to accumulator.");
                }
                else{
                    alu = (byte) (mdr&0b011111111);
                    System.out.println("Subtracted value " + (mdr&0b011111111) + " to accumulator.");
                }
                alu =Compliment2(alu);
                alu = fullAdder(alu,acc);
                acc = alu;
                break;
            case 0b0101:
                if(acc == 0) {
                    pc = mdr&0b011111111;
                }
                break;
            case 0b0110:
                if(acc>=0){
                    pc = mdr&0b011111111;
                }
                break;
            case 0b0111:
                pc= mdr&0b011111111;
                break;
            case 0b1111:
                System.out.println("OUTPUT: " + acc);
                break;
        }
        Fetch();
    }
    public byte fullAdder(int n, int m){
        int c = 0;//to hold the carry bit
        int s = 0;//to hold the final added result
        for(int i = 0; i<8; i++){//loops through all bits in the int
            int bitmask = 0b1<<i;//creates a bit mask to cycle through each bit
            int x = n&bitmask;//applies the bit mask to the alu and acc values
            int y = m&bitmask;
            int sum = x^y^c;//calculates the sum using XOR
            s = s | sum;//concatenates the bit to the final result using OR
            c= (y&(x^c) | x&c)<<1;//calculates the carry bit and shifts it by one for the next calculation
        }
        return (byte) s;
    }

    public byte Compliment2(int x){
        return (byte) (~x +1);
    }
}



//PROBLEM: CANNOT IMMEDIATE ACCESS AS CPU ASSUMES DIRECT ADDRESSING ALWAYS
//SOLUTION: A WAY OF INDICATING A DIFFERENCE BETWEEN DATA LOCATIONS AND IMMEDIATE DATA
//POSSIBLE SOLUTION: USE A CHECK TO SEE IF NUMBER IS A KNOWN DATA LOCATION
//POSSIBLE SOLUTION: IMMEDIATE DATA IS NEGATIVE NUMBERS, DATA LOCATION ARE POSITIVE
//LIKELY SOLUTION [WORKED] : CONVERT INTEGER DATA LOCATIONS AND INSTRUCTIONS INTO BINARY, USE 1 BIT TO INDICATE DIRECt OR IMMEDIATE ADDRESSING

//PROBLEM: CANNOT READ BRANCH STATEMENTS OR BRANCHED AREAS OF CODE
//SOLUTION: INTEGRATE SOME WAY OF CHECKING IF A LINE OF CODE CONTAINS A BRANCH
//LIKELY SOLUTION [WORKED]: SPLIT THE LOAD TO MEMORY FUNCTION INTO SEPARATE FUNCTION WITH SOME DESIGNED FOR BRANCHED AND SOME FOR NOT BRANCHED CODE

//PROBLEM: NEED TO SUBTRACT TO BINARY VALUES FROM EACH OTHER
//SOLUTION: CONVERT BOTH BINARY INTEGERS INTO 2'S COMPLIMENT, THEN MAKE THE NUMBER WE ARE TAKING AWAY INTO A NEGATIVE NUMBER, THEN USE THE FULL ADDER FUNCTION
//LIKELY SOLUTION: CYCLE THROUGH BOTH INTEGERS BITS, CONVERTING THEM INTO ONES COMPLEMENT AND THEN ADDING 1 TO THEM USING THE ADDER FUNCTION, THEN FLIPPING THE INTEGER BRING TAKEN AWAY TO MAKE IT NEGATIVE, THEN PUTTING BOTH BINARIES THROUGH THE ADDER FUNCTION AGAIN
//SOLUTION AMENDMENT [TOOK FORWARD]: JAVA AUTOMATICALLY SAVES INTEGERS IN 2S COMPLEMENT AND SO  NO CONVERSION TO 2S COMPLIMENT IS NEEDED, ALL I NEED TO DO IS FLIP AND ADD

//PROBLEM: SUBTRACTION NOT WORKING, MOST LIKELY DUE TO NUMBER OF BITS DURING CALCULATIONS
//SOLUTION: A WAY OF ENSURING THE NUMBER OF BITS DO NOT INTERFERE WITH THE SUBTRACTION
//LIKELY SOLUTION : USE THE BYTE DATA TYPE TO LIMIT THE COMPUTING TO 8 BITS ONLY
//LIKELY SOLUTION: ONLY THE NEGATIVE NUMBER NEEDS TO BE CONVERTED USING 2S COMPLEMENT, THEN ADDED
