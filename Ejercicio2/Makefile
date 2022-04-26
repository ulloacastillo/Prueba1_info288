
HOSTNAME = $(shell hostname)
all:
	mpicc ejercicio2.c -o prog
run:
	mpirun -np ${1} --hostfile myhostsfile --host $(HOSTNAME) ./prog ${2}
