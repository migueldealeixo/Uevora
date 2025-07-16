estado_inicial(e(10)).

terminal(e(0)).
terminal(e(1)).

op(e(N),e(N1)):-
    N >= 1,
    N1 is N-1.
op(e(N),e(N2)):-
    N >= 2,
    N2 is N-2.

valor(e(0),-1).
valor(e(1),-1).
valor(E,V):-
    \+ terminal(E),
    findall(V1,(op(E,E1),valor(E1,V1)), Vs),
    escolhe_melhor(Vs,V).

escolhe_melhor(Vs,1):-
    member(-1,Vs),!.
escolhe_melhor(_,-1).


