# The example code below implements Euclidean algorithm for GCD.
# it reads two numbers from x10 and x11, calculates GCD, and
# puts the result in x10 register.
# you will see x10 = 3 in the output if you run the code.

main:
		li x10, 21
		li x11, 15

loop:   beq  x11, x0,  exit
        remu x5,  x10, x11
        add  x10, x11, x0
        add  x11, x5,  x0
        beq  x0,  x0,  loop
# exit:   sd   x10, 2000(x0)
exit:   ret