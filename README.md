# P2-Distribuida

The mission of this repository is to implement and understand the Lamport and Ricart–Agrawala algorithms.<br>
This is an exercise of the Distributed systems subject in computer engineering taught in La Salle University:<br> 
You need to design and implement a distributed application. This application must have two heavyweight processes:

ProcessA | ProcessB
------------ | -------------
Must invoke 3 lightweight processes: <br> ProcessLWA1<br> ProcessLWA2<br> ProcessLWA3.<br> | Must invoke 2 processes: ProcessLWB1<br> ProcessLWB2.
<br>  Each lightweight process must live in a loop
infinity that will consist of displaying its identifier on the screen for 10 times while waiting 1 second at a time
and time.
<br><br>Both heavyweight processes will run on the same machine, so all lightweight processes will compete
by the same share: the screen. A policy will need to be implemented between the two heavyweight processes
token-based mutual exclusion. Among the processes invoked by ProcessA, a Lamport policy will have to be implemented
for mutual exclusion. Among the processes invoked by ProcessB, a Ricart and Agrawala policy will have to be implemented
for mutual exclusion