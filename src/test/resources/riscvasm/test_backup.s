        .global add3
        .text
add3:   add a0, a0, a1      # a0 = a0 + a1
	    .byte 1, 2, 3
        add a0, a0, a2      # a0 = a0 + a2
        ret                 # return value in a0