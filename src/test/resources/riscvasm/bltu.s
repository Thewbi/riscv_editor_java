__main:
    li t0, -3
    lui t1, 2
    bltu t0, t1, -8
    blt t0, t1, -12

# Although both instructions, look at the same operands, bltu does not
# perform the jump whereas blt performs the jump (creating an endless loop)
#
# Why?
#
# bltu interprets the operands as unsigned values.
# -3 (0xFFFFFFFD) as unsigned is not interpreted as twos-complement and
# becomes a very large positive number (4294967293)
# 2 remains the value 2.
# bltu 4294967293, 2, -8 performs the comparison 4294967293 < 2 which is false and the jump is not taken
#
# blt interprets the operands as signed values
# -3 remains -3, 2 remains 2
# blt -3, 2, -12 performs the comparison -3 < 2 and the jump is taken