# https://github.com/mausimus/rvcc
#
# The __fibonacci function computes the n-th fibonacci number.
# The parameter n is expected in the register a0
#
# The fibonacci numbers are computed by:
# f_0 = 0
# f_1 = 1
# f_2 = 1
# f_n = f_(n-1) + f_(n-2)
#
# The indexes:                      0, 1, 2, 3, 4, 5, 6, 7, 8
# belong to the fibonacci numbers:  0, 1, 1, 2, 3, 5, 8, 13, 21
#
# This means __fibonacci(6) = 8
#
# __fibonacci places the return value into the register a0

__fibonacci:
    addi  sp, sp, -32;          # reserve stack space for function
    sw    s0, 28(sp);           # store previous frame
    sw    ra, 24(sp);           # store return address
    addi  s0, sp, 16;           # set new frame location
    sw    a0, -4(s0);           # store parameter on stack
    addi  a0, s0, 0;            # get address of variable n
    addi  a0, a0, -4;
    lw    a0, 0(a0);            # read value from address into a0
    addi  a1, zero, 0;          # set a1 to zero
    beq   a0, a1, pc + 12;      # compare a0 with a1, if equal jump +3
    addi  a0, zero, 0;          # set a0 to zero
    jal   zero, pc + 8;         # skip next instruction
    addi  a0, zero, 1;          # set a0 to one
    addi  zero, zero, 0;
    beq   a0, zero, pc + 16;    # if a0 is zero, jump forward
    addi  a0, zero, 0;          # else set return value to zero
    jal   zero, pc + 136;       # jump to function exit
    jal   zero, pc + 132;
    addi  a0, s0, 0;            # get address of variable n
    addi  a0, a0, -4;
    lw    a0, 0(a0);            # read value from address into a0
    addi  a1, zero, 1;          # set a1 to one
    beq   a0, a1, pc + 12;      # compare a0 with a1, if equal jump +3
    addi  a0, zero, 0;          # set a0 to zero
    jal   zero, pc + 8;         # skip next instruction
    addi  a0, zero, 1;          # set a0 to one
    addi  zero, zero, 0;
    beq   a0, zero, pc + 16;    # if a0 is zero, jump forward
    addi  a0, zero, 1;          # else set return value to one
    jal   zero, pc + 84;        # jump to function exit
    jal   zero, pc + 80;
    addi  a0, s0, 0;            # get address of variable n
    addi  a0, a0, -4;
    lw    a0, 0(a0);            # read value from address into a0
    addi  a1, zero, 1;          # set a1 to one
    sub   a0, a0, a1;           # subtract a1 from a0
    jal   ra, pc - 144;         # call function fib() into a0
    addi  sp, sp, -16;          # store result on stack
    sw    a0, 0(sp);
    addi  a0, s0, 0;            # get address of variable n
    addi  a0, a0, -4;
    lw    a0, 0(a0);            # read value from address into a0
    addi  a1, zero, 2;          # set a1 to two
    sub   a0, a0, a1;           # subtract a1 from a0
    jal   ra, pc - 176;         # call function fib() into a1
    addi  a1, a0, 0;
    lw    a0, 0(sp);            # retrieve result off stack into a0
    addi  sp, sp, 16;
    add   a0, a0, a1;           # add a1 to a0
    jal   zero, pc + 4;         # jump to function exit
    addi  sp, s0, 16;           # trim stack space
    lw    ra, -8(sp);           # recover return address
    lw    s0, -4(sp);           # recover previous frame
    jalr  zero, ra, 0;          # return from function

__main:
    lui a0, 6
    jal ra, __fibonacci
    addi x0, x0, 0              # result is in a0 (x10)
    addi x0, x0, 0              # result is in a0 (x10)
    addi x0, x0, 0              # result is in a0 (x10)
