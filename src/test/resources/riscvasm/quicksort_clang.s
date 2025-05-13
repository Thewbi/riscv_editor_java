# The quicksort source code is available in quicksort.c
# It has been compiled using the https://godbolt.org/
# online compiler page using RISC-V rv32gc clang.
#
# Use the Venus simulator to debug. Also install the RISC.V
# support extension:
# https://marketplace.visualstudio.com/items?itemName=hm.riscv-venus
# https://marketplace.visualstudio.com/items?itemName=zhwu95.riscv
#
# In the Venus simulator, the values to sort are placed
# on the stack and the result can be retrieved from the
# stack at the same location.
#
# The initial stack pointer value for the Venus simulator
# is 0x7FFFFFF0.
#
# The application loads the values 1 3 5 2 4 onto the stack.
# It will sort the values in descending order. The result
# will be 5 4 3 2 1
__main:
        addi    sp, sp, -48
        sw      ra, 44(sp)
        sw      s0, 40(sp)
        addi    s0, sp, 48
        li      a1, 0
        sw      a1, -40(s0)
        sw      a1, -12(s0)
        li      a0, 1
        sw      a0, -16(s0)
        li      a0, 3
        sw      a0, -20(s0)
        li      a0, 5
        sw      a0, -24(s0)
        li      a2, 2
        sw      a2, -28(s0)
        li      a2, 4
        sw      a2, -32(s0)
        sw      a0, -36(s0)
        lw      a0, -36(s0)
        addi    a2, a0, -1
        addi    a0, s0, -32
        call    quickSort
        lw      a0, -40(s0)
        lw      ra, 44(sp)
        lw      s0, 40(sp)
        addi    sp, sp, 48
        ret

swap:
        addi    sp, sp, -32
        sw      ra, 28(sp)
        sw      s0, 24(sp)
        addi    s0, sp, 32
        sw      a0, -12(s0)
        sw      a1, -16(s0)
        lw      a0, -12(s0)
        lw      a0, 0(a0)
        sw      a0, -20(s0)
        lw      a0, -16(s0)
        lw      a0, 0(a0)
        lw      a1, -12(s0)
        sw      a0, 0(a1)
        lw      a0, -20(s0)
        lw      a1, -16(s0)
        sw      a0, 0(a1)
        lw      ra, 28(sp)
        lw      s0, 24(sp)
        addi    sp, sp, 32
        ret

partition:
        addi    sp, sp, -48
        sw      ra, 44(sp)
        sw      s0, 40(sp)
        addi    s0, sp, 48
        sw      a0, -12(s0)
        sw      a1, -16(s0)
        sw      a2, -20(s0)
        lw      a0, -12(s0)
        lw      a1, -16(s0)
        slli    a1, a1, 2
        add     a0, a0, a1
        lw      a0, 0(a0)
        sw      a0, -24(s0)
        lw      a0, -16(s0)
        sw      a0, -28(s0)
        lw      a0, -20(s0)
        sw      a0, -32(s0)
        j       .LBB1_1
.LBB1_1:
        lw      a0, -28(s0)
        lw      a1, -32(s0)
        bge     a0, a1, .LBB1_15
        j       .LBB1_2
.LBB1_2:
        j       .LBB1_3
.LBB1_3:
        lw      a0, -12(s0)
        lw      a1, -28(s0)
        slli    a1, a1, 2
        add     a0, a0, a1
        lw      a1, 0(a0)
        lw      a0, -24(s0)
        li      a2, 0
        sw      a2, -36(s0)
        blt     a0, a1, .LBB1_5
        j       .LBB1_4
.LBB1_4:
        lw      a1, -28(s0)
        lw      a0, -20(s0)
        addi    a0, a0, -1
        slt     a0, a0, a1
        xori    a0, a0, 1
        sw      a0, -36(s0)
        j       .LBB1_5
.LBB1_5:
        lw      a0, -36(s0)
        andi    a0, a0, 1
        beqz    a0, .LBB1_7
        j       .LBB1_6
.LBB1_6:
        lw      a0, -28(s0)
        addi    a0, a0, 1
        sw      a0, -28(s0)
        j       .LBB1_3
.LBB1_7:
        j       .LBB1_8
.LBB1_8:
        lw      a0, -12(s0)
        lw      a1, -32(s0)
        slli    a1, a1, 2
        add     a0, a0, a1
        lw      a1, 0(a0)
        lw      a0, -24(s0)
        li      a2, 0
        sw      a2, -40(s0)
        bge     a0, a1, .LBB1_10
        j       .LBB1_9
.LBB1_9:
        lw      a0, -32(s0)
        lw      a1, -16(s0)
        addi    a1, a1, 1
        slt     a0, a0, a1
        xori    a0, a0, 1
        sw      a0, -40(s0)
        j       .LBB1_10
.LBB1_10:
        lw      a0, -40(s0)
        andi    a0, a0, 1
        beqz    a0, .LBB1_12
        j       .LBB1_11
.LBB1_11:
        lw      a0, -32(s0)
        addi    a0, a0, -1
        sw      a0, -32(s0)
        j       .LBB1_8
.LBB1_12:
        lw      a0, -28(s0)
        lw      a1, -32(s0)
        bge     a0, a1, .LBB1_14
        j       .LBB1_13
.LBB1_13:
        lw      a1, -12(s0)
        lw      a0, -28(s0)
        slli    a0, a0, 2
        add     a0, a0, a1
        lw      a2, -32(s0)
        slli    a2, a2, 2
        add     a1, a1, a2
        call    swap
        j       .LBB1_14
.LBB1_14:
        j       .LBB1_1
.LBB1_15:
        lw      a1, -12(s0)
        lw      a0, -16(s0)
        slli    a0, a0, 2
        add     a0, a0, a1
        lw      a2, -32(s0)
        slli    a2, a2, 2
        add     a1, a1, a2
        call    swap
        lw      a0, -32(s0)
        lw      ra, 44(sp)
        lw      s0, 40(sp)
        addi    sp, sp, 48
        ret

quickSort:
        addi    sp, sp, -32
        sw      ra, 28(sp)
        sw      s0, 24(sp)
        addi    s0, sp, 32
        sw      a0, -12(s0)
        sw      a1, -16(s0)
        sw      a2, -20(s0)
        lw      a0, -16(s0)
        lw      a1, -20(s0)
        bge     a0, a1, .LBB2_2
        j       .LBB2_1
.LBB2_1:
        lw      a0, -12(s0)
        lw      a1, -16(s0)
        lw      a2, -20(s0)
        call    partition
        sw      a0, -24(s0)
        lw      a0, -12(s0)
        lw      a1, -16(s0)
        lw      a2, -24(s0)
        addi    a2, a2, -1
        call    quickSort
        lw      a0, -12(s0)
        lw      a1, -24(s0)
        addi    a1, a1, 1
        lw      a2, -20(s0)
        call    quickSort
        j       .LBB2_2
.LBB2_2:
        lw      ra, 28(sp)
        lw      s0, 24(sp)
        addi    sp, sp, 32
        ret