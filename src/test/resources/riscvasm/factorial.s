# https://en.wikichip.org/wiki/risc-v/registers

	.file	"factorial.c"
	.option nopic
	.attribute arch, "rv32i2p0"
	.attribute unaligned_access, 0
	.attribute stack_align, 16
	.text


	.align	2
	.globl	main
	.type	main, @function
main:
    # construct stackframe
	addi	sp,sp,-32
	sw	    ra,28(sp)
	sw	    s0,24(sp)
	addi	s0,sp,32

    # int init_value = 6;
	li	    a5, 6
	sw	    a5, -20(s0)

    # int result = factorial(6);
    # result is in a0 == x10
	li	    a0, 6
	call	factorial

    brk

    # store return value which is in a0 onto the stack variable 'result'
	sw	    a0, -24(s0)

    brk

    # move a0 into a7 so that the result of the factorial is available in a register
    # ant not only on the stack which makes debugging easier
    # a7 == x17
    mv      a7, a0 

    brk

    # return 0;
	li	    a5, 0
	mv	    a0, a5

    # remove stackframe
	lw	    ra,28(sp)
	lw	    s0,24(sp)
	addi	sp,sp,32
	jr	    ra
	.size	main, .-main


	.align	2
	.globl	mult
	.type	mult, @function
mult:
	addi	sp,sp,-48       # new stack pointer
	sw	    s0,44(sp)       # store old stack frame bottom on the stack
	addi	s0,sp,48        # compute new stack frame bottom
	sw	    a0,-36(s0)      # push parameters onto the stack ?
	sw	    a1,-40(s0)      # push parameters onto the stack ?
	sw	    zero,-20(s0)
	j	    .L4
.L6:
	lw	    a5,-40(s0)
	andi	a5,a5,1
	beq	    a5,zero,.L5
	lw	    a4,-20(s0)
	lw	    a5,-36(s0)
	add	    a5,a4,a5
	sw	    a5,-20(s0)
.L5:
	lw	    a5,-40(s0)
	srai	a5,a5,1
	sw	    a5,-40(s0)
	lw	    a5,-36(s0)
	slli	a5,a5,1
	sw	    a5,-36(s0)
.L4:
	lw	    a5,-40(s0)
	bne	    a5,zero,.L6
	lw	    a5,-20(s0)
	mv	    a0,a5

    # remove stackframe
	lw	    s0,44(sp)
	addi	sp,sp,48
	jr	    ra
	.size	mult, .-mult


	.align	2
	.globl	factorial
	.type	factorial, @function
factorial:
	addi	sp,sp,-32
	sw	    ra,28(sp)
	sw	    s0,24(sp)
	addi	s0,sp,32
	sw	    a0,-20(s0)
	lw	    a5,-20(s0)
	bgt	    a5,zero,.L9
	li	    a5,1
	j	    .L10
.L9:
	lw	    a5,-20(s0)
	addi	a5,a5,-1
	mv	    a0,a5
	call	factorial
	mv	    a5,a0
	mv	    a1,a5
	lw	    a0,-20(s0)
	call	mult
	mv	    a5,a0
.L10:

    # return value in a0
	mv	    a0,a5

    # remove stackframe
	lw	    ra,28(sp)
	lw	    s0,24(sp)
	addi	sp,sp,32
	jr	    ra
	.size	factorial, .-factorial


	.ident	"GCC: (GNU) 11.1.0"
