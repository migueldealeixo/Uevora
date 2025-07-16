estado_inicial(e([
    [P,G,P,G],
    [G,P,G,P],
    [P,P,G,G]]
    )).

terminal(e(L1,L2,L3)):-
    conta(L1,N1),conta(L2,N2),conta(L3,N3),
    N is N1+N2+N3, N = 6.

valor(e(L1,L2,L3),1):-
    contaG(L1,N1),contaG(L2,N2),contaG(L3,N3),
    N is N1+N2+N3, N > 3.

valor(e(L1,L2,L3),0):-
    contaG(L1,N1),contaG(L2,N2),contaG(L3,N3),
    N is N1+N2+N3, N = 3.

valor(e(L1,L2,L3),-1):-
    contaG(L1,N1),contaG(L2,N2),contaG(L3,N3),
    N is N1+N2+N3, N < 3.

op(e(L1,L2,L3), tira(1,N,A), e(L11,L2,L3)) :-
    tira(L1,N,A,L11),
    conta(L11,L2,L3,6).

op(e(L1,L2,L3), tira(2,N,A), e(L1,L22,L3)) :-
    tira(L2,N,A,L22),
    conta(L1,L22,L3,6).

op(e(L1,L2,L3), tira(3,N,A), e(L1,L2,L33)) :-
    tira(L3,N,A,L33),
    conta(L1,L2,L33,6).

conta([], 0).
conta([_|T], N) :-
    conta(T, N1),
    N is N1 + 1.

contaG([], 0).
contaG([G|T], N) :-
    G == 'G',
    contaG(T, N1),
    N is N1 + 1.
contaG([H|T], N) :-
    H \== 'G',
    contaG(T, N).

tira(L, N, A, Resto) :-
    prefixo(N, A, Prefixo),
    append(Prefixo, Resto, L).

prefixo(0, _, []).
prefixo(N, A, [A|T]) :-
    N > 0,
    N1 is N - 1,
    prefixo(N1, A, T).
