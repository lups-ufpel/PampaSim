processor core mips 100 count 1;
//scheduler FCFS;
scheduler SJF;
procmanager;

proc start 0 duration 5 priority 2 clr #0000ff; // t1
proc start 0 duration 2 priority 3 clr #888800; // t2
proc start 1 duration 4 priority 1 clr #008888; // t3
proc start 3 duration 1 priority 4 clr #008800; // t4
proc start 5 duration 2 priority 5 clr #880000; // t5
