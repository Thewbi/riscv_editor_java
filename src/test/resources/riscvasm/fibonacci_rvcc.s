addi  sp, sp, -32           # reserve stack space for function
sw    s0, 28(sp)            # store previous frame. (s0/fp is the stack zero or frame pointer)
sw    ra, 24(sp)            # store return address
addi  s0, sp, 16            # set new frame location
sw    a0, -4(s0)            # store parameter on stack
addi  a0, s0, 0             # get address of variable n
addi  a0, a0, -4
lw    a0, 0(a0)             # read value from address into a0
addi  a1, zero, 0           # set a1 to zero
beq   a0, a1, 12            # compare a0 with a1, if equal jump +3
addi  a0, zero, 0           # set a0 to zero
jal   zero, 8               # skip next instruction
addi  a0, zero, 1           # set a0 to one
addi  zero, zero, 0
beq   a0, zero, 16          # if a0 is zero, jump forward
addi  a0, zero, 0           # else set return value to zero
jal   zero, 136             # jump to function exit
jal   zero, 132
addi  a0, s0, 0             # get address of variable n
addi  a0, a0, -4
lw    a0, 0(a0)             # read value from address into a0
addi  a1, zero, 1           # set a1 to one
beq   a0, a1, 12            # compare a0 with a1, if equal jump +3
addi  a0, zero, 0           # set a0 to zero
jal   zero, 8               # skip next instruction
addi  a0, zero, 1           # set a0 to one
addi  zero, zero, 0
beq   a0, zero, 16          # if a0 is zero, jump forward
addi  a0, zero, 1           # else set return value to one
jal   zero, 84              # jump to function exit
jal   zero, 80
addi  a0, s0, 0             # get address of variable n
addi  a0, a0, -4
lw    a0, 0(a0)             # read value from address into a0
addi  a1, zero, 1           # set a1 to one
sub   a0, a0, a1            # subtract a1 from a0
jal   ra, -144              # call function fib() into a0
addi  sp, sp, -16           # store result on stack
sw    a0, 0(sp)
addi  a0, s0, 0             # get address of variable n
addi  a0, a0, -4
lw    a0, 0(a0)             # read value from address into a0
addi  a1, zero, 2           # set a1 to two
sub   a0, a0, a1            # subtract a1 from a0
jal   ra, -176              # call function fib() into a1
addi  a1, a0, 0
lw    a0, 0(sp)             # retrieve result off stack into a0
addi  sp, sp, 16
add   a0, a0, a1            # add a1 to a0
jal   zero, 4               # jump to function exit
addi  sp, s0, 16            # trim stack space
lw    ra, -8(sp)            # recover return address
lw    s0, -4(sp)            # recover previous frame
jalr  zero, ra, 0           # return from function