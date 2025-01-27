append([],[],L).

append([H|T],L,[H|R]):-
    append(T,L,R).

membro(X,[X|_]).
membro(X,[_|T]):-
    membro(X,T).

conta([],_,0).
conta([X|T],X,N):-
    conta(T,X,N1),
    N is N1 +1.
conta([Y|T],X,N):-
    X\=Y,
    conta(T,X,N).

remove([],_,[]).
remove([X|T],X,T).
remove([Y|T],X,[Y|T1]):-
    X\=Y,
    remove(T,X,T1).

