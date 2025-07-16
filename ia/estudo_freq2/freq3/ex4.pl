estado_inicial(e([0,0,0,0])).
terminal(e(Linhas)):-
    soma_total(Linhas,12).

valor(E,-1):-
    terminal(E).

valor(E,1):-
    conta(L1,N1),conta(L2,N2),conta(L3,N3),
    N is N1+N2+N3, N > 6.
valor(E,0):-
    conta(L1,N1),conta(L2,N2),conta(L3,N3),
    N is N1+N2+N3, N = 6.
