// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

    @KBD
    D=A
    @end
    M=D     // RAM[end] = KBD (24576)

    @SCREEN
    D=A
    @start
    M=D     // RAM[start] = SCREEN (16384)

    @set   
    M=0     // initialize: RAM[set] = 0


(LOOP)
    @KBD
    D=M     // D = RAM[KBD]
    @CLEAR
    D;JEQ   // if RAM[KBD] == 0
            // then go to CLEAR
            // else go to FILL

(FILL)
    @set
    M=-1    // RAM[set] = -1
    @FILL_OR_CLEAR    // JUMP INTO fill_or_clear function
    0;JMP


(CLEAR)
    @set
    M=0    // RAM[set] = 0
    @FILL_OR_CLEAR
    0;JMP


(FILL_OR_CLEAR)    // fill_or_clear(set);
                   // set =  0:  clear screen
                   // set = -1: fill screen
    @i
    M=0     // RAM[i] = 0
    (INSIDE_LOOP)
        @start
        D=M
        @i
        D=D+M       // D = RAM[start] + RAM[i]
        @addr
        M=D         // RAM[addr] = RAM[start] + RAM[i]

        @end
        D=M
        @addr
        D=D-M       // D = RAM[end] - RAM[addr]

        @LOOP
        D;JLE      // if RAM[end] - RAM[addr] <= 0 
                   // then go to @LOOP

        @set       // D = RAM[set]
        D=M

        @addr
        A = M      // RAM[RAM[addr]] = RAM[set]
        M = D

        @i
        M=M+1      // RAM[i] = RAM[i] + 1
        
        @INSIDE_LOOP
        0;JMP



