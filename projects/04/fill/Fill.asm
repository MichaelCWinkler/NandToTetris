// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input. 
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel. When no key is pressed, the
// program clears the screen, i.e. writes "white" in every pixel.

(RESTART)
	// Set i (which holds the number of the current register
	// to be set to either white or black) to the top-left corner
	// of the screen 
	@16384
	D=A
	@i
	M=D
	
	// If a key is being pressed, start turning the screen black
	// otherwise, no jump will be executed, so the program will
	// start turning pixels white
	@24576
	D=M
	@BLACK
	D;JGT

(WHITE)
	// Set the current register to 0
	@i
	A=M
	M=0

	// Update the current register
	@i
	MD=M+1
	
	// If we've hit the bottom right corner of the screen,
	// start again from the top left
	@24575
	D=D-A
	@RESTART
	D;JGT

	// If no key is being pressed, continue turning pixels white.
	// Otherwise no jump will be executed, so the program will begin
	// turning pixels black
	@24576
	D=M
	@WHITE
	D;JEQ

(BLACK)
	// Set the current register to -1 (all the bits will be 1)
	@i
	A=M
	M=-1
	
	// Update the current register
	@i
	MD=M+1

	// If we've hit the bottom right corner of the screen,
	// start again from the top left
	@24575
	D=D-A
	@RESTART
	D;JGT
	
	// If no key is being pressed, begin turning pixels white, otherwise
	// continue turning pixels black
	@24576
	D=M
	@WHITE
	D;JEQ
	@BLACK
	0;JMP

