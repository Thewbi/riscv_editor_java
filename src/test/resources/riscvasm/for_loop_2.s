    li t0, 0                # t0 = 0
    li t2, 10               # t2 = 10
loop_head:
    bge t0, t2, loop_end
                            # Repeated code goes here
    addi t0, t0, 1
    j loop_head
loop_end:

# https://www.riscvschool.com/2022/04/28/risc-v-assembly-tutorial/
# The assembly code above implements the following C++ loop.
#
# for (int i = 0;i < 10;i++) {
#    // Repeated code goes here.
# }