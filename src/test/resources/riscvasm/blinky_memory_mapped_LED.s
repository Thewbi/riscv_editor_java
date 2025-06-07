__main:
loop_start:
    addi x5, x0, 0x0
    addi x6, x0, 0x0
    lui x7, 0
    addi x7, x7, 2

busy_loop_start:	
    beq x5, x7, 0xC             # if (x5 == x7) jump to loop_end (pc relative jump of +12 bytes)
    addi x5, x5, 1
    jal x0, busy_loop_start     # jal loop head (pc relative jump back -8 bytes)

busy_loop_end:
    lw x6, 52(x0)
    xori x6, x6, 1
    sw x6, 52(x0)

    jal x0, loop_start