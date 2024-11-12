## Von Nuemann Emulator / Little Man Computer in Java
# What this is.
This project was completed supplementary to the City, University of London Comp Sci course, during which I was taught about both programming in Java and System Architecture including the Von Nuemann Architecture. I decided to combine these 2 modules ina singular project by creating my own Little Man Computer Style Von Nuemann CPU Emulator.
# The LMC
This project shares a lot with the Little Man computer by Peter Higginson, originally created by Dr. Stuart Madnick in 1965, in that it uses the same instruction set, also sharing some registers such as the Program Counter, Accumulator and Arithmetic Logic Unit.
# How to use it.
You can run LMC assembly code by either directly writing into, or copying and pasting, your code into a plane .txt file. Ensure you place the text file in both the src and out folders of the project. Once done, run the java program. It will ask for the name of the program, so input the name of the .txt file that your program is stored in.
# How it works.
The CPU is split into 3 different proccesses, Fetch, Decode and Execute. However before this, we must parse through the assembly code and convert it into machine code that the CPU understands. Below I will explain the Compiler.
# Converting to Machine code
There are a few different data structures that work together to parse through the code, The first of which is the array. The integer array MainMemory emulates the RAM of the Computer that is being emulated and is where our machine code is going to be loaded into before running the program. Next is the HashMap. The Hashmap uses key - value pairs to store data that is quickly accessable. They have searching Time Complexities of O(1). There are 3 HashMaps used in this project. The first is instructSet; this aptly named HashMap holds every instruction in our instruction set and it's machine code equivalent. The machine code equivalent is in binary and will be the opcode in each instruction.
The HashMap dataLocals is used to store the name of the data locations the programmer has created using the DAT instruction along with the memory location in the Main Memory where this named data location is found. The final HashMap branches works similar to datalocals, but instead stores the names of named branches with the data location in memory where that branch should branch to if called. Now that we have our data structures we need to get our code to compile. We first open the file using an InputStream and then run a scanner over it. Before this we will need to create a temporary String array of the same length as MainMemory as this will need to hold our retrieved lines of code which we will progressively run through and compile. Once we have our temp array filled with our code, we can first run through it looking for any branches or data locations so that we can store the locations for later. The location of the instruction is directly linked to its index in the temp array and so we can use that as the data location.

Now that we have our data locations and we have our code, we can start converting it into our binary machine code. First we check if the line of code is null; this means the line is empty and so there is no more code left in the program. Then we check for our opcode only instructions, these three instructions, OUT, INP and HLT do not have any operand as they do not require one and so they can just be written in their full machine code form in the main memory. We then need to split our assembly by the " " in between each part of the lines of code. This will split our opcode and operand and so now we can start comparing. However before this, we must check if the instruction is branhced by comparing the first value of the line and searching it within the branches HashMap. This is because branched code can have more than 2 parts e.g. REPEAT LDA 100 , here REPEAT is the name of the branch which indicates that this line is the location we will be returning to when a branch is called with the REPEAT name. We will need to ignore this part of the code as the only important part of that code to us is LDA 100.

To convert an instruction like LDA 100 into machine code, we first check the first part of the instruction, in this case LDA, and look for its binary representation in the instructSet, which in this case is 0b0001. with this binary, we now have our opcode, however, since we will be appending our operand to our line of code, we can't leave it like this. Instead, we need to shift this up away from where our operand will go, which in our case is 9. After this, we can append our operand. This is where datalocals becomes important since now, we need to differentiate between direct and immediate addresing. We know that when a program uses direct addressing, the name of the location will be in that HashMap, and so we can retrieve the location in memory of that data location from datalocals and append it to our code as an operand. The problem is differentiating between direct and immediate and direct addressing since they both appear as byte values to the CPU. We can fix this by assigning an extra bit to our operand that indicates which type fo addressing we are using. This is why the opcode is shifted 9 bits instead of 8