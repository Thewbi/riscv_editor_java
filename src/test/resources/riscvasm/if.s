    li      a4, 4           # 00400713  # pc = 0    # a4 = 5, a4 is used to hold the constant 5
    li      a5, 5           # 00500793  # pc = 4    # a5 = 5, a5 is the variable a5 used in the if statement
    li      a6, 1           # 00100813  # pc = 8    # a6 = 1
    bne     a4, a5, .L3     # 00f71463  # pc = 12    # if (a5 == 5)
    li      a5, 3           # 00300793  # pc = 16    #   a5 = 3,    code executed when the if is taken
    li      a6, 1           # 00100813  # pc = 20    # some code which is skipped by the if branch
    li      a6, 2           # 00200813  # pc = 24
    li      a6, 3           # 00300813  # pc = 28
    li      a6, 4           # 00400813  # pc = 32
.L3:
    li      a6, 5           # 00500813  # pc = 36
    li      a6, 6           # 00600813  # pc = 40
    li      a6, 7           # 00700813  # pc = 44
    li      a6, 8           # 00800813  # pc = 48
