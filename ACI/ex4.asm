.text
.global main
main:
	li a0,3
	li a1,6
	jal ra,count_odd
	li a7,7
	ecall
count_odd:
	mv t0,a0
	mv t1,a1
	li t2,0
	li t3,0
loop:
	bgt t0,t1,end
	andi t3,t0,1
	beqz t3,even
	addi t2,t2,1
	addi t0,t0,1
	j loop
even:
	addi t0,t0,1
	j loop
end:
	mv a0,t2
	ret

	
	