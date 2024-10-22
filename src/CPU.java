import javax.print.DocFlavor;
import java.lang.*;
import java.io.*;
import java.util.*;

public class CPU {

    public static Integer[] MainMemory = new Integer[200];
    public int pc = 0;
    public int alu = 0;
    public int acc = 0;
    public String cir = "0";
    public int mdr = 0;
    public int mar = 0;
    public int cu = 0;
    public static void main(String[] args) {
        loadToMemory("base");
    }


    private static void loadToMemory(String f) {

        HashMap<String, Integer> instructSet = new HashMap<String, Integer>(); // Holds the instruction set
        instructSet.put("LDA", 0);//Loads data from the data location given to the ACC
        instructSet.put("STA", 1);//Stores data from the ACC to the given data location
        instructSet.put("ADD", 3);//Adds the value from the given data location to the ACC
        instructSet.put("SUB", 4);//Subtracts the data from the given data location from the ACC
        instructSet.put("OUT", 5);//Outputs the content of the ACC
        instructSet.put("BRZ", 6);//Branches to the given instruction if the content in the ACC is 0
        instructSet.put("BRP", 7);//Branches to the given instruction id the content in the ACC is greater than 0
        instructSet.put("BRA", 8);//Branched to the given data location no matter what
        instructSet.put("DAT", 9);//Allocates a data location with the given name

        HashMap<String, Integer> dataLocals = new HashMap<String, Integer>();

        InputStream inputStream = CPU.class.getClassLoader().getResourceAsStream("base.txt");
        Scanner readLine = new Scanner(inputStream);

        String[] temp = new String[MainMemory.length];//initialises string array to hold lines of code

        int currIndex = 0;
        while (readLine.hasNextLine()) {//loops through every line in the program
            String output = readLine.nextLine();
            System.out.println(output);
            temp[currIndex] = output;//adds the line into the string ar
            currIndex++;//increments the counter
        }


        for(int i = 0; i < temp.length - 1; i++) {//loops through each line of code
            String[] s = temp[i].split(" ");//Lexical Analysis, split code into tokens
            System.out.println(s[0]);
            if(s[0] == "DAT"){//checks if the current line of code declares a data location
            dataLocals.put(s[0], i);//saves the name of the data location used with the location address
            }

        }

        int opcode =0;//to store the opcode
        int operand = 0;//to store the operand
        int num = 0;
        for(String l : temp){//loops through every line of code
            String[] s = temp[i].split(" ");//Lexical Analysis, split code into tokens
            System.out.println(s[0]);
            if(dataLocals.get(s[1]) != null ){//checks if a known data location is referenced
                operand = dataLocals.get(s[1]);//replaces the name of the location with the address in the code
            }
            else{
                try{
                    operand = Integer.parseInt(s[1]);//adds the immediate value of the integer provided
                }
                catch(NumberFormatException e){
                    System.out.println("Error on line " + num + " relating to the data location reference.");
                }


            }
            try{
                opcode = instructSet.get(s[0])*1000;//gets the integer value of the instruction from the instruction set and adds it to the opcode
            }
            catch(NullPointerException e){
                System.out.println("Error on line " + num + " relating to the instruction spelling (Ensure all characters are CAPS).");
            }
            MainMemory[num] = opcode+operand;//combines the opcode and operand and loads it into the main memory
            num++;//increments the data address
        }



    }
    public void Fetch(){
       // mar = pc;
        //cir = MainMemory[mar].toString();
        //cu = cir;
        //pc++;
    }

    public void Decode(){
        //String[] inst =cir.split(" ");
        //cir = opcode;
       // mdr = operand;
    }

    public void Execute(){
       // switch (cir){
       //     case 00:

       // }
    }
}
