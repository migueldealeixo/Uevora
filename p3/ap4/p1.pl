sequencia(A,B,[A]):-A= B.
sequencia(A,B,[A|T]):-A<B, A1 is A+1,sequencia(A1,B,T).

double([],[]).
double([X|T],[X,X|T2]):-double(T,T2).

adj(E1,E2,[E1,E2|_]).
adj(E1,E2,[E2,E1|_]).
adj(E1,E2,[_|T]):-
    adj(E1,E2,T).

sel(E,[E|T],T).
sel(E,[X|T],[X|T2]):-sel(E,T,T2).

soma([],0).
soma([X|T],S):-
    soma(T,S1),
    S is X+ S1.