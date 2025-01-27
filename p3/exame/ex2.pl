call(Goal,Arg1,Arg2):-
    Goal =.. [Functor|Args],
    append(Args,[Arg1,Arg2],NewArgs),
    Goal1 =.. [functor|NewArgs],
    call(Goal1).


mais(X,Y,Z):-Z is X + Y.

map(_,[],[]).
map(P,[X|LI],[Y|LO]):-
    call(P,X,Y),
    map(P,LO,LI).