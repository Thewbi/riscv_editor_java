swap:
        addi    sp,sp,-48           # make space for stack frame
        sw      ra,44(sp)           # jal, jalr has loade ra, safe ra here for return
        sw      s0,40(sp)           # safe s0 because it is used in the next line
        addi    s0,sp,48            # s0 points to both function parameters on the stack

        sw      a0,-36(s0)          # place int* a on the stack
        sw      a1,-40(s0)          # place int* b on the stack

        #brk                         # breakpoint for debugging (custom instruction)

        # int temp = *a;
        lw      a5,-36(s0)          # retrieve int* a from the stack
        lw      a5,0(a5)            # deref int* a, a5 is the value of the arr element
        sw      a5,-20(s0)          # store the value in a temp variable (on the stack)

        # *a = *b;
        lw      a5,-40(s0)          # retrieve int* b from the stack
        lw      a4,0(a5)            # deref int* b, a4 is the value of the arr element
        lw      a5,-36(s0)          # retrieve int* a from the stack
        sw      a4,0(a5)            # store b into a

        # *b = temp;
        lw      a5,-40(s0)          # find b on the stack
        lw      a4,-20(s0)          # get back the temp variable
        sw      a4,0(a5)            # write temp var into b

        #brk                         # breakpoint for debugging (custom instruction)

        # return from swap()
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra



partition:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)

        # int p = arr[low];
        lw      a5,-40(s0)
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        #brk                         # breakpoint
        lw      a5, 0(a5)           # a5 (pivot) has to have the value 4 [OK]
        sw      a5, -28(s0)

        # int i = low;
        lw      a5, -40(s0)         # a5 has to have the value 0 [OK]
        sw      a5, -20(s0)

        # int j = high;
        lw      a5, -44(s0)         # a5 has to have the value 4 [OK]
        sw      a5, -24(s0)

        j       .L3

# inside partition > while
# i++;
.L6:
        lw      a5,-20(s0)
        addi    a5,a5,1
        sw      a5,-20(s0)

# inside partition > while
#
# find the first element greater than
# the pivot (from starting)
#        while (arr[i] <= p && i <= high - 1) {
.L4:
        lw      a5,-20(s0)
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        lw      a5,0(a5)
        lw      a4,-28(s0)
        blt     a4,a5,.L7
        lw      a4,-44(s0)
        lw      a5,-20(s0)
        bgt     a4, a5, .L6
        j       .L7

# j--;
.L9:
        lw      a5, -24(s0)
        addi    a5, a5, -1
        sw      a5, -24(s0)


# find the first element smaller than
# the pivot (from last)
#      while (arr[j] > p && j >= low + 1) {
.L7:
        lw      a5, -24(s0)
        slli    a5, a5, 2
        lw      a4, -36(s0)
        add     a5, a4, a5
        lw      a5, 0(a5)
        lw      a4, -28(s0)
        bge     a4, a5, .L8
        lw      a4, -40(s0)
        lw      a5, -24(s0)
        blt     a4, a5, .L9

# inside partition() > while
# if (i < j) {
#    swap(&arr[i], &arr[j]);
# }
.L8:
        #brk
        lw      a4, -20(s0)         # j, a4 has to be 4
        lw      a5, -24(s0)         # i, a5 has to be 2
        bge     a4, a5, .L3
        #blt     a4, a5, .L3

# swap(&arr[i], &arr[j]);
        lw      a5, -20(s0)
        slli    a5, a5, 2
        lw      a4, -36(s0)
        add     a3, a4, a5
        lw      a5, -24(s0)
        slli    a5, a5, 2
        lw      a4, -36(s0)
        add     a5, a4, a5
        mv      a1, a5
        mv      a0, a3
        call    swap

.L3:
        # while (i < j) {
        lw      a4,-20(s0)
        lw      a5,-24(s0)
        blt     a4,a5,.L4

        # swap(&arr[low], &arr[j]);
        lw      a5,-40(s0)
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a3,a4,a5
        lw      a5,-24(s0)
        slli    a5,a5,2
        lw      a4,-36(s0)
        add     a5,a4,a5
        mv      a1,a5
        mv      a0,a3
        call    swap

        # prepare return value j by writing it into the return register
        lw      a5,-24(s0)

        # return from the partition() function()
        mv      a0,a5
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra

quickSort:
        addi    sp,sp,-48
        sw      ra,44(sp)
        sw      s0,40(sp)
        addi    s0,sp,48
        sw      a0,-36(s0)
        sw      a1,-40(s0)
        sw      a2,-44(s0)
        lw      a4,-40(s0)
        lw      a5,-44(s0)
        bge     a4, a5, .L14
        lw      a2,-44(s0)
        lw      a1,-40(s0)
        lw      a0,-36(s0)
        call    partition
        sw      a0,-20(s0)
        lw      a5,-20(s0)
        addi    a5,a5,-1
        mv      a2,a5
        lw      a1,-40(s0)
        lw      a0,-36(s0)
        call    quickSort
        lw      a5,-20(s0)
        addi    a5,a5,1
        lw      a2,-44(s0)
        mv      a1,a5
        lw      a0,-36(s0)
        call    quickSort
.L14:
        nop
        lw      ra,44(sp)
        lw      s0,40(sp)
        addi    sp,sp,48
        jr      ra


.LC0:
        .word   4
        .word   2
        .word   5
        .word   3
        .word   1

__main:
        addi    sp,sp,-48           # reserve stack frame
        sw      ra,44(sp)           # save return address of caller
        sw      s0,40(sp)           # save top of stack of caller

        addi    s0, sp, 48            # initialize s0 to top of stack

        # determine address of array
        # this is the correct address of the array according to the map file
        # optimized to addi, zero 0x214
        lui     a5, %hi(.LC0)
        addi    a5, a5, %lo(.LC0)

        #brk                         # breakpoint, check a5, has to point to array .LC0

        # this copies the array to the stack !?!

        lw      a1, 0(a5)           # load array element 0 with value 4 int a1
        lw      a2, 4(a5)           # load array element 1 with value 2 int a1
        lw      a3, 8(a5)           # load array element 2 with value 5 int a1
        lw      a4, 12(a5)          # load array element 3 with value 3 int a1
        lw      a5, 16(a5)          # load array element 4 with value 1 int a1

        sw      a1, -40(s0)         # store value 4 onto the stack
        sw      a2, -36(s0)         # store value 2 onto the stack
        sw      a3, -32(s0)         # store value 5 onto the stack
        sw      a4, -28(s0)         # store value 3 onto the stack
        sw      a5, -24(s0)         # store value 1 onto the stack

        # store array length on the stack as a parameter to quicksort()
        li      a5, 5               # length of the array, compiler does hardcode this value
        sw      a5, -20(s0)         # store length of array on the stack

        lw      a5, -20(s0)         # load the same value stored before??? Length of array
        addi    a4, a5, -1          # this computes (n - 1) which is used in the C code as third parameter

        addi    a5, s0, -40         # address of the array somehow???

        # prepare parameters in a0, a1, a2
        mv      a2, a4              # param 3 - is (n - 1) in the C code
        li      a1, 0               # param 2 - is zero in the C code

        #brk                        # breakpoint (custom instruction)

        mv      a0, a5              # param 1 - address of the array
                                    # move pseudo instruction is implemented by addi a0, a5, 0x00

        call    quickSort

        li      a5, 0
        mv      a0, a5
        lw      ra, 44(sp)
        lw      s0, 40(sp)
        addi    sp, sp, 48
        jr      ra