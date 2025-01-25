# Hello World program in RISC-V assembly

.data
message: .asciiz "Hello, World!\n"

.text
.globl main

main:
    # Print message
    li a0, message    # Load address of message into a0
    li a7, 4          # System call code for print string
    ecall             # Perform system call

    # Exit program
    li a7, 10         # System call code for exit
    ecall             # Perform system call
