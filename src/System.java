import java.lang.*;
import java.io.*;
import java.util.*;
import java.util.Scanner;

public class System {
    public static void main(String[] args) {

        HashMap<String, Integer> instructSet = new HashMap<String, Integer>();
        instructSet.put("LDA",0);//Loads data from the data location given to the ACC
        instructSet.put("STA",1);//Stores data from the ACC to the given data location
        instructSet.put("ADD",3);//Adds the value from the given data location to the ACC
        instructSet.put("SUB",4);//Subtracts the data from the given data location from the ACC
        instructSet.put("OUT",5);//Outputs the content of the ACC
        instructSet.put("BRZ",6);//Branches to the given instruction if the content in the ACC is 0
        instructSet.put("BRP",7);//Branches to the given instruction id the content in the ACC is greater than 0
        instructSet.put("BRA",8);//Branched to the given data location no matter what

        String[] MainMemory = new String[200];
        int pc = 0;
        int alu = 0;
        int acc = 0;
        int cir = 0;
        int cdr = 0;
        int car = 0;
        int cu = 0;


    }

    public void LoadToMemory(String f){
        File program = new File(f + ".txt");
        Scanner readLine = new Scanner(program);

    }
    public void Fetch(){
        CAR = PC;
        CIR = MainMemory[CAR];
        CU = CIR;
    }

    public void Decode(){
        String[] inst =CIR.split(" ");
        CIR = opcode;
        CDR = operand;
    }

    public void Execute(){
        switch (CIR){
            case 00:

        }
    }
}
